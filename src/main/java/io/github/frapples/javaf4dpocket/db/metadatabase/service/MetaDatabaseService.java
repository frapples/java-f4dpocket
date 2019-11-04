package io.github.frapples.javaf4dpocket.db.metadatabase.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.github.frapples.javaf4dpocket.comm.response.ErrcodeException;
import io.github.frapples.javaf4dpocket.db.metadatabase.mapper.IMetaDatabaseMapper;
import io.github.frapples.javaf4dpocket.db.metadatabase.model.ColumnsEntity;
import io.github.frapples.javaf4dpocket.db.metadatabase.model.DatabaseConfig;
import io.github.frapples.javaf4dpocket.db.metadatabase.model.TableEntity;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import javax.inject.Singleton;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

/**
 * @author Frapples <isfrapples@outlook.com>
 * @date 2019/4/17
 */
@Singleton
@Slf4j
public class MetaDatabaseService {

    private Cache<DatabaseConfig, SqlSessionFactory> sqlSessionFactories = CacheBuilder.newBuilder()
        .expireAfterAccess(10, TimeUnit.MINUTES)
        .build();

    public String getComment(DatabaseConfig config, String tableName) {
        return mapper(config, IMetaDatabaseMapper.class,
            (metaDatabaseMapper -> metaDatabaseMapper.getComment(config.getDbname(), tableName)));
    }

    public List<ColumnsEntity> getColumn(DatabaseConfig config, String tableName) {
        return mapper(config, IMetaDatabaseMapper.class,
            (metaDatabaseMapper -> metaDatabaseMapper.getColumn(config.getDbname(), tableName)));
    }

    public List<TableEntity> getTables(DatabaseConfig config) {
        return mapper(config, IMetaDatabaseMapper.class,
            (metaDatabaseMapper -> metaDatabaseMapper.getTables(config.getDbname())));
    }

    private <M, R> R mapper(DatabaseConfig config, Class<M> mapperClass, Function<M, R> continuation) {
        SqlSessionFactory sessionFactory = sqlSessionFactory(config);
        // session资源对象，需要close
        try (SqlSession session = sessionFactory.openSession(true)) {
            M mapper = session.getMapper(mapperClass);
            return continuation.apply(mapper);
        }
    }

    public void test(DatabaseConfig config) {
        SqlSessionFactory sessionFactory = sqlSessionFactory(config);
        SqlSession session = sessionFactory.openSession(true);
        try {
            session.getConnection();
        } catch (PersistenceException e) {
            Throwable cause = e.getCause();
            log.error("", e);
            throw ErrcodeException.error("数据库连接失败：" + (cause == null ? "" : cause.getMessage()));
        }
    }

    @SneakyThrows
    private SqlSessionFactory sqlSessionFactory(DatabaseConfig config) {
        DatabaseConfig databaseConfig = new DatabaseConfig().setType(config.getType())
            .setDbname(config.getDbname()).setIpPort(config.getIpPort())
            .setUsername(config.getUsername()).setPassword(config.getPassword());
        return sqlSessionFactories.get(databaseConfig, () -> createSqlSessionFactory(databaseConfig));
    }

    private SqlSessionFactory createSqlSessionFactory(DatabaseConfig databaseConfig) {
        DataSource dataSource = dataSource(databaseConfig);
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", transactionFactory, dataSource);

        // mybatis配置
        Configuration configuration = new Configuration(environment);
        {
            configuration.setCacheEnabled(true);
            configuration.setLazyLoadingEnabled(true);
            configuration.setAggressiveLazyLoading(false);
            configuration.setMultipleResultSetsEnabled(true);
            configuration.setUseColumnLabel(true);
            configuration.setMapUnderscoreToCamelCase(true);
            configuration.setLogImpl(Slf4jImpl.class);

            // 设置Dao
            configuration.addMapper(IMetaDatabaseMapper.class);
        }

        return new SqlSessionFactoryBuilder()
            .build(configuration);
    }

    private DataSource dataSource(DatabaseConfig databaseConfig) {
        String driver;
        String url;
        if ("oracle".equals(databaseConfig.getType())) {
            driver = "oracle.jdbc.driver.OracleDriver";
            url = "jdbc:oracle:thin:@" + databaseConfig.getIpPort() + ":" + databaseConfig.getDbname();
        } else if ("mysql".equals(databaseConfig.getType())) {
            driver = "com.mysql.jdbc.Driver";
            url = "jdbc:mysql://" + databaseConfig.getIpPort() + "/" + databaseConfig.getDbname()
                + "?useUnicode=true&amp;characterEncoding=utf-8";
        } else if ("h2".equals(databaseConfig.getType())) {
            driver = "org.h2.Driver";
            url = "jdbc:h2:" + databaseConfig.getIpPort();
        } else {
            throw ErrcodeException.error("不支持的数据库类型：" + databaseConfig.getType());
        }

        // 使用自带的Datasource足矣，无需使用第三方
        return new UnpooledDataSource(driver, url, databaseConfig.getUsername(), databaseConfig.getPassword());
    }
}
