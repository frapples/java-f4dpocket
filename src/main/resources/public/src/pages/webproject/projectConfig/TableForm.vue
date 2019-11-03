<template>
  <form :autoFormCreate="(form) => this.form = form">
    <a-table
        :columns="columns"
        :data-source="dataSource"
        :pagination="false"
        :scroll="{ x: 1500 }"
    >
      <template  v-for="(col, i) in ['code', 'path', 'desc', 'ipport', 'dbname', 'dbuser', 'dbpassword']" :slot="col" slot-scope="text, record, index">
        <a-input
            :key="col"
            v-if="record.editable"
            style="margin: -5px 0"
            :value="text"
            :placeholder="columns[i].title"
            @change="e => handleChange(e.target.value, record.key, col)"></a-input>
        <template v-else>{{text}}</template>
      </template>
      <template slot="dbtype" slot-scope="text, record, index">
        <a-select :value="text"
                  v-if="record.editable"
                  style="margin: -5px 0"
                  @change="handleChange(arguments[0], record.key, 'dbtype')">
          <a-select-option value="oracle">oracle</a-select-option>
          <a-select-option value="mysql">mysql</a-select-option>
          <a-select-option value="h2">h2</a-select-option>
        </a-select>
        <template v-else>{{text}}</template>
      </template>
      <template slot="operation" slot-scope="text, record, index">
        <template v-if="record.editable">
          <span v-if="record.isNew">
            <a @click="saveRow(record.key)">添加</a>
            <a-divider type="vertical"></a-divider>
            <a-popconfirm title="是否要删除此行？" @confirm="remove(record.key)">
              <a>删除</a>
            </a-popconfirm>
          </span>
          <span v-else>
            <a @click="saveRow(record.key)">保存</a>
            <a-divider type="vertical"></a-divider>
            <a @click="cancle(record.key)">取消</a>
          </span>
        </template>
        <span v-else>
          <a @click="toggle(record.key)">编辑</a>
          <a-divider type="vertical"></a-divider>
          <a-popconfirm title="是否要删除此行？" @confirm="remove(record.key)">
            <a>删除</a>
          </a-popconfirm>
        </span>
      </template>
    </a-table>
    <a-button style="width: 100%; margin-top: 16px; margin-bottom: 8px" type="dashed" icon="plus" @click="newMeber">新增成员</a-button>
  </form>
</template>

<script>
  const columns = [
    {
      title: '项目',
      width: "5%",
      dataIndex: 'code',
      key: 'code',
      scopedSlots: { customRender: 'code' }
    },
    {

      title: '项目路径',
      width: "10%",
      dataIndex: 'path',
      key: 'path',
      scopedSlots: { customRender: 'path' }
    },
    {
      title: '说明',
      width: "8%",
      dataIndex: 'desc',
      key: 'desc',
      scopedSlots: { customRender: 'desc' }
    },

    {
      title: '数据库类型',
      width: "5%",
      dataIndex: 'dbtype',
      key: 'dbtype',
      scopedSlots: { customRender: 'dbtype' }
    },

    {
      title: '地址和端口',
      width: "8%",
      dataIndex: 'ipport',
      key: 'ipport',
      scopedSlots: { customRender: 'ipport' }
    },

    {
      title: '数据库名',
      width: "8%",
      dataIndex: 'dbname',
      key: 'dbname',
      scopedSlots: { customRender: 'dbname' }
    },

    {
      title: '数据库用户',
      width: "8%",
      dataIndex: 'dbuser',
      key: 'dbuser',
      scopedSlots: { customRender: 'dbuser' }
    },

    {
      title: '数据库密码',
      width: "8%",
      dataIndex: 'dbpassword',
      key: 'dbpassword',
      scopedSlots: { customRender: 'dbpassword' }
    },
    {
      title: '操作',
      width: "10%",
      key: 'action',
      //fixed: 'right',
      scopedSlots: { customRender: 'operation' }
    }
  ];

  const dataSource = [
    {
      key: 1,
      editable: false,
      id: 'wms',
      path: 'D:\\ams',
      desc: '供应商管理项目',
    },
    {
      key: 2,
      editable: false,
      id: 'ams',
      path: 'D:\\ams',
      desc: '供应商管理项目',
    }
  ];



  module.exports = {
    name: 'TableForm',
    data () {
      return {
        columns,
        dataSource
      }
    },
    methods: {
      handleSubmit (e) {
        e.preventDefault()
      },
      newMeber () {
        this.dataSource.push({
          key: '99',
          id: '',
          path: '',
          desc: '',
          editable: true,
          isNew: true
        })
      },
      remove (key) {
        const newData = this.dataSource.filter(item => item.key !== key)
        this.dataSource = newData
      },
      saveRow (key) {
        let target = this.dataSource.filter(item => item.key === key)[0]
        target.editable = false
        target.isNew = false
      },
      toggle (key) {
        let target = this.dataSource.filter(item => item.key === key)[0]
        target.editable = !target.editable
      },
      getRowByKey (key, newData) {
        const data = this.dataSource
        return (newData || data).filter(item => item.key === key)[0]
      },
      cancle (key) {
        let target = this.dataSource.filter(item => item.key === key)[0]
        target.editable = false
      },
      handleChange (value, key, column) {
        const newData = [...this.dataSource]
        const target = newData.filter(item => key === item.key)[0]
        if (target) {
          target[column] = value
          this.dataSource = newData
        }
      }
    }
  }
</script>

<style scoped>

</style>
