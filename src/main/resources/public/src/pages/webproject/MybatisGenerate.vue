<template>
    <page-layout title="mybatis代码生成">
        <div slot="headerContent">
            <a-form-item label="请选择项目" :label-col="{ span: 2 }" :wrapper-col="{ span: 10 }">
                <a-select style="width: 300px" v-model="projectIndex" :default-value="projectList.length > 0 ? 0 : null" @change="onProjectSelected"
                >
                    <a-select-option v-for="(project, index) in projectList" :key="index" :value="index">
                        <span style="margin-right: 20px"> {{project.name}}({{project.code}}) </span>
                        <span style="color: #9ea7b4; font-size: 12px; float: right">{{ project.path }}</span>
                    </a-select-option>
                </a-select>
            </a-form-item>
            <a-form-item label="作者" :label-col="{ span: 2 }" :wrapper-col="{ span: 10 }">
                <a-select :default-value="projectList.length > 0 ? 0 : null"
                          v-model="authorIndex"
                >
                    <a-select-option v-for="(author, index) in authors"
                                     :key="index" :value="index">
                        <span style="margin-right: 20px"> {{author.name}} </span>
                        <span style="color: #9ea7b4; font-size: 12px; float: right">{{author.nickName}} <{{ author.email }}></span>
                    </a-select-option>
                </a-select>
            </a-form-item>
        </div>
        <a-card class="card"  :bordered="false">
            <div slot="title">
                <span>已有文件</span>
                <a-button type="link" @click="$refs.projectFilesTable.detect()">探测</a-button>
                <project-files ref="projectFilesTable" :project-index="projectIndex" :author-index="authorIndex"></project-files>
            </div>
        </a-card>
        <a-card class="card" title="代码生成" :bordered="false">

            <a-form :form="form" class="form">
                <a-row :gutter="24" class="form-row" type="flex" justify="start">
                    <a-col :span="8" >
                        <a-form-item label="项目" :label-col="{ span: 5 }" :wrapper-col="{ span: 17 }">
                            <span>{{this.projectList[this.projectIndex].name}}</span>
                        </a-form-item>
                    </a-col>

                    <a-col :span="16" >
                        <a-form-item label="表" :label-col="{ span: 2 }" :wrapper-col="{ span: 10 }">
                            <a-spin size="small" :spinning="tableLoading" >
                                <a-select :filter-option="tableFilterOption"
                                          show-search option-filter-prop="children"
                                          v-decorator="['tableIndex']"
                                          @change="onTableSelected"

                                >
                                    <a-select-option v-for="(table, index) in tables" :key="index" :value="index">
                                        <span style="margin-right: 20px"> {{table.tableName}} </span>
                                        <span style="color: #9ea7b4; font-size: 12px; float: right">{{ table.tableComment }}</span>
                                    </a-select-option>
                                </a-select>
                            </a-spin>
                        </a-form-item>
                    </a-col>
                </a-row>

                <a-row :gutter="24" class="form-row" type="flex" justify="start">
                    <a-col :span="16" >
                        <a-form-item label="实体名" :label-col="{ span: 2 }" :wrapper-col="{ span: 10 }">
                            <div style="display: flex">
                                <a-input v-decorator="['moduleName']"></a-input>
                                <div style="flex:0 0 10px">
                                    <a-tooltip placement="rightTop">
                                        <template slot="title">
                                            <span>1. 实体名作为组成和该表相关各模块的类名元素。</span>
                                            <br>
                                            <span>2. 若实体名为Person, 则Entity为PersonEntity, Mapper为PersonEntityMapper.</span>

                                        </template>
                                        <a-button type="link" style="color: #333"
                                                  shape="circle" icon="question" size="small"></a-button>
                                    </a-tooltip>
                                </div>
                            </div>
                        </a-form-item>
                    </a-col>
                </a-row>

                <a-divider dashed orientation="left">
                    <span class="block-title">Entity文件</span>
                    <a-checkbox v-model="moduleSwitch.entity"></a-checkbox>
                </a-divider>

                <a-row :gutter="24" v-if="moduleSwitch.entity">
                    <a-col :span="9" >
                        <a-form-item label="src目录" :label-col="{ span: 5 }" :wrapper-col="{ span: 18 }">
                            <a-input
                                v-decorator="['entity.srcPath', { rules: [{ required: true, message: 'Please input your note!' }] }]">
                            </a-input>
                        </a-form-item>
                    </a-col>
                    <a-col :span="9" >
                        <a-form-item label="包名" :label-col="{ span: 5 }" :wrapper-col="{ span: 18 }">
                            <a-auto-complete :data-source="allPackageNames" :filter-option="filterOption"
                                v-decorator="['entity.package', { rules: [{ required: true, message: 'Please input your note!' }] }]">
                            </a-auto-complete>
                        </a-form-item>
                    </a-col>
                </a-row>
                <a-divider dashed orientation="left">
                    <span class="block-title">*mapper.xml文件</span>
                    <a-checkbox v-model="moduleSwitch.mapperXml"></a-checkbox>
                </a-divider>
                <a-row :gutter="24" v-if="moduleSwitch.mapperXml">
                    <a-col :span="9" >
                        <a-form-item label="src目录" :label-col="{ span: 5 }" :wrapper-col="{ span: 17 }">
                            <a-input
                                v-decorator="['mapperXml.srcPath', { rules: [{ required: true, message: 'Please input your note!' }] }]">
                            </a-input>
                        </a-form-item>
                    </a-col>
                    <a-col :span="9" >
                        <a-form-item label="包名" :label-col="{ span: 5 }" :wrapper-col="{ span: 17 }">
                            <a-auto-complete  :filter-option="filterOption"
                                v-decorator="['mapperXml.package', { rules: [{ required: true, message: 'Please input your note!' }] }]">
                                <template slot="dataSource">
                                    <a-select-option v-for="(packageName, index) in allPackageNames" :key="packageName">{{packageName}}</a-select-option>
                                </template>
                            </a-auto-complete>
                        </a-form-item>
                    </a-col>
                </a-row>

                <a-divider dashed orientation="left">
                    <span class="block-title">*Mapper接口文件</span>
                    <a-checkbox v-model="moduleSwitch.mapperInterface"></a-checkbox>
                </a-divider>
                <a-row :gutter="24" v-if="moduleSwitch.mapperInterface">
                    <a-col :span="9" >
                        <a-form-item label="src目录" :label-col="{ span: 5 }" :wrapper-col="{ span: 17 }">
                            <a-input
                                v-decorator="['mapperInterface.srcPath', { rules: [{ required: true, message: 'Please input your note!' }] }]">
                            </a-input>
                        </a-form-item>
                    </a-col>
                    <a-col :span="9" >
                        <a-form-item label="包名" :label-col="{ span: 5 }" :wrapper-col="{ span: 17 }">
                            <a-auto-complete :data-source="allPackageNames" :filter-option="filterOption"
                                v-decorator="['mapperInterface.package', { rules: [{ required: true, message: 'Please input your note!' }] }]">
                            </a-auto-complete>
                        </a-form-item>
                    </a-col>
                </a-row>
                <a-form-item :wrapper-col="{ span: 12, offset: 5 }">
                    <a-button type="primary" @click="click">
                        提交
                    </a-button>
                </a-form-item>
            </a-form>
        </a-card>
    </page-layout>
</template>

<script>
    const PageLayout = httpVueLoader('../../layouts/PageLayout.vue');
    const ProjectFiles = httpVueLoader('./mybatisGenerate/projectFiles.vue');

    module.exports = {
        name: 'MybatisGenerate',
        components: {PageLayout, ProjectFiles },
        data () {
            return {
                form: this.$form.createForm(this, { name: 'coordinated' }),
                tableLoading: false,
                projectIndex: 0,
                authorIndex: 0,
                tables: [],
                allPackageNames: [],
                moduleSwitch: {
                    mapperInterface: true,
                    entity: true,
                    mapperXml: true,
                }
            }
        },

        watch: {
            moduleSwitch: {
                handler(newV, oldV) {
                    console.log(newV);
                },
                deep: true
            }
        },

        computed: {

            projectList() {
                return this.$store.state.autocode.projects;
            },

            authors() {
                return this.$store.state.autocode.authors;
            },
        },
        methods: {
            onProjectSelected(index) {
                this.tables = [];
                const path = this.projectList[index].path;
                this.tableLoading = true;
                httpWithJsonRpc("project.getDatabaseTables", {path: path})
                .then(data => {
                    this.tables = data.result;
                    this.form.setFieldsValue({
                        tableIndex: this.tables.length > 0 ? 0 : null
                    });
                    this.tableLoading = false;
                });

                this.allPackageNames = [];
                httpWithJsonRpc("project.allPackageNames", {path: path})
                .then(data => {
                    this.allPackageNames = data.result;
                });
            },

            onTableSelected(index) {
                const table = this.tables[index];
                this.form.setFieldsValue({
                    moduleName: this.underlineToCamelCase(table.tableName)
                });
            },

            underlineToCamelCase(name) {
                const text = name.replace(/\_(\w)/g, function (all, letter) {
                    return letter.toUpperCase();
                });
                return text.length > 0 ? text[0].toUpperCase() + text.slice(1) : text;
            },

            tableFilterOption(input, option) {
                const text = option.componentOptions.children[0].children[0].text;
                return text.toLowerCase().indexOf(input.toLowerCase()) >= 0;
            },

            filterOption (input, option) {
                if (input.length < 3) {
                    return false;
                }
                return option.componentOptions.children[0].text.toUpperCase().indexOf(input.toUpperCase()) >= 0;
            },

            click() {
                this.form.validateFields((err, values) => {
                    if (!err) {
                        const data = {
                            moduleName: values.moduleName,
                            project: this.projectList[this.projectIndex],
                            author: this.authors[this.authorIndex],
                            table: this.tables[values.tableIndex],
                            modules: {
                                entity: values.entity,
                                mapperInterface: values.mapperInterface,
                                mapperXml: values.mapperXml
                            }
                        };
                        console.log(data);
                        httpWithJsonRpc("generator.generate", {param: data })
                        .then(() => {
                            antd.notification.success({
                                message: '生成成功',
                                description: `代码生成成功!`,
                                duration: 5
                            });
                        });
                    }
                });
            }
        }
    }
</script>

<style scoped>
    .card {
        margin-bottom: 24px;
    }

    .form-row {
        margin: 0 -8px
    }
    .form .ant-col-md-12,
    .form .ant-col-sm-24,
    .form .ant-col-lg-6,
    .form .ant-col-lg-8,
    .form .ant-col-lg-10,
    .form .ant-col-xl-8,
    .form .ant-col-xl-6{
        padding: 0 8px
    }

    .block-title {
        font-size: 14px;
        font-weight: 400;
    }
</style>
