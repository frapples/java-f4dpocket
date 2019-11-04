<template>
    <page-layout title="mybatis代码生成">
        <a-card class="card" title="代码生成" :bordered="false">
            <a-form :form="form">
                <a-row :gutter="24">
                    <a-col :span="12" >
                        <a-form-item label="项目" :label-col="{ span: 5 }" :wrapper-col="{ span: 17 }">
                            <a-select :default-value="projectList.length > 0 ? 0 : null" @change="onProjectSelected">
                                <a-select-option v-for="(project, index) in projectList" :key="index" :value="index">
                                    <span style="margin-right: 20px"> {{project.name}}({{project.code}}) </span>
                                    <span style="color: #9ea7b4; font-size: 12px; float: right">{{ project.path }}</span>
                                </a-select-option>
                            </a-select>
                        </a-form-item>
                    </a-col>

                    <a-col :span="12" >
                        <a-form-item label="表" :label-col="{ span: 5 }" :wrapper-col="{ span: 17 }">
                            <a-spin size="small" :spinning="tableLoading" >
                                <a-select :default-value="tables.length > 0 ? 0 : null"
                                          :filter-option="tableFilterOption"
                                    show-search option-filter-prop="children">
                                    <a-select-option v-for="(table, index) in tables" :key="index" :value="index">
                                        <span style="margin-right: 20px"> {{table.tableName}} </span>
                                        <span style="color: #9ea7b4; font-size: 12px; float: right">{{ table.tableComment }}</span>
                                    </a-select-option>
                                </a-select>
                            </a-spin>
                        </a-form-item>
                    </a-col>
                </a-row>

                <a-row :gutter="24">
                    <a-col :span="12" >
                        <a-form-item label="作者" :label-col="{ span: 5 }" :wrapper-col="{ span: 17 }">
                            <a-select :default-value="projectList.length > 0 ? 0 : null">
                                <a-select-option v-for="(author, index) in authors"
                                                 :key="index" :value="index">
                                    <span style="margin-right: 20px"> {{author.name}} </span>
                                    <span style="color: #9ea7b4; font-size: 12px; float: right">{{author.nickName}} <{{ author.email }}></span>
                                </a-select-option>
                            </a-select>
                        </a-form-item>
                    </a-col>
                </a-row>

                <a-divider dashed orientation="left">Entity文件</a-divider>

                <a-row :gutter="24">
                    <a-col :span="12" >
                        <a-form-item label="src目录" :label-col="{ span: 5 }" :wrapper-col="{ span: 17 }">
                            <a-input
                                v-decorator="['name', { rules: [{ required: true, message: 'Please input your note!' }] }]">
                            </a-input>
                        </a-form-item>
                    </a-col>
                    <a-col :span="12" >
                        <a-form-item label="包名" :label-col="{ span: 5 }" :wrapper-col="{ span: 17 }">
                            <a-input
                                v-decorator="['code', { rules: [{ required: true, message: 'Please input your note!' }] }]">
                            </a-input>
                        </a-form-item>
                    </a-col>
                </a-row>
                <a-divider dashed orientation="left">*mapper.xml文件</a-divider>

                <a-row :gutter="24">
                    <a-col :span="12" >
                        <a-form-item label="src目录" :label-col="{ span: 5 }" :wrapper-col="{ span: 17 }">
                            <a-input
                                v-decorator="['name', { rules: [{ required: true, message: 'Please input your note!' }] }]">
                            </a-input>
                        </a-form-item>
                    </a-col>
                    <a-col :span="12" >
                        <a-form-item label="包名" :label-col="{ span: 5 }" :wrapper-col="{ span: 17 }">
                            <a-input
                                v-decorator="['code', { rules: [{ required: true, message: 'Please input your note!' }] }]">
                            </a-input>
                        </a-form-item>
                    </a-col>
                </a-row>

                <a-divider dashed orientation="left">*Mapper接口文件</a-divider>
                <a-row :gutter="24">
                    <a-col :span="12" >
                        <a-form-item label="src目录" :label-col="{ span: 5 }" :wrapper-col="{ span: 17 }">
                            <a-input
                                v-decorator="['name', { rules: [{ required: true, message: 'Please input your note!' }] }]">
                            </a-input>
                        </a-form-item>
                    </a-col>
                    <a-col :span="12" >
                        <a-form-item label="包名" :label-col="{ span: 5 }" :wrapper-col="{ span: 17 }">
                            <a-input
                                v-decorator="['code', { rules: [{ required: true, message: 'Please input your note!' }] }]">
                            </a-input>
                        </a-form-item>
                    </a-col>
                </a-row>
            </a-form>
        </a-card>
    </page-layout>
</template>

<script>
    const PageLayout = httpVueLoader('../../layouts/PageLayout.vue');

    module.exports = {
        name: 'MybatisGenerate',
        components: {PageLayout },
        data () {
            return {
                form: this.$form.createForm(this, { name: 'coordinated' }),
                tableLoading: false,
                tables: [],
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
                httpWithJsonRpc("project.getDatabaseTables", { path: path})
                .then(data => {
                    this.tables = data.result;
                    this.tableLoading = false;
                });
            },

            tableFilterOption(input, option) {
                const text = option.componentOptions.children[0].children[0].text;
                return text.toLowerCase().indexOf(input.toLowerCase()) >= 0;
            }
        }
    }
</script>

<style scoped>
    .card {
        margin-bottom: 24px;
    }
</style>
