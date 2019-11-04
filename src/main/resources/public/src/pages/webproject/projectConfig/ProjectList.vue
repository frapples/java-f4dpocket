<template>
    <div class="card-list">
        <a-list
            :grid="{gutter: 24, lg: 3, md: 2, sm: 1, xs: 1}"
            :data-source="projectListWithAdd"
        >
            <a-list-item slot="renderItem" slot-scope="item, index">
                <template v-if="item.isAdd">
                    <a-button class="new-btn" type="dashed" @click="addModal.visible = true">
                        <a-icon type="plus"></a-icon>添加项目
                    </a-button>
                </template>
                <template v-else>
                    <a-card :hoverable="true" :loading="loading">
                        <a-card-meta >
                            <div style="margin-bottom: 3px" slot="title">
                                <span> {{item.name}}({{item.code}}) </span>
                                <a style="float: right" slot="extra" @click="onOpenEditModal(index, true)">More</a>
                            </div>
                            <a-avatar class="card-avatar" slot="avatar" :src="item.avatar" size="large"></a-avatar>
                            <div class="meta-content" slot="description" v-if="!loading">
                                <div>项目路径：{{ item.path }}</div>
                                <div>数据库：{{projectDetails[index].database.type}} {{projectDetails[index].database.ipPort}} {{projectDetails[index].database.dbname}}</div>
                            </div>
                        </a-card-meta>
                        <a slot="actions" @click="onOpenEditModal(index, false)">编辑</a>
                        <a-popconfirm slot="actions" title="是否要删除此行？" @confirm="onRemoveProject(index)">
                            <a>删除</a>
                        </a-popconfirm>
                        <a-popconfirm slot="actions" title="是否要测试数据库配置？" @confirm="onTestDbConnection(index)">
                            <a>测试</a>
                        </a-popconfirm>
                    </a-card>
                </template>
            </a-list-item>
        </a-list>

        <a-modal v-model="addModal.visible" title="Title" :width="420" >
            <template slot="footer">
                <a-button key="back" @click="addModal.visible = false">取消</a-button>
                <a-button key="submit" type="primary" @click="handleSubmit">
                提交
                </a-button>
            </template>
            <a-form :form="addModal.form">
                <a-form-item label="项目名称" :label-col="{ span: 5 }" :wrapper-col="{ span: 17 }">
                    <a-input
                        v-decorator="['name', { rules: [{ required: true, message: 'Please input your note!' }] }]">
                    </a-input>
                </a-form-item>
                <a-form-item label="项目代码" :label-col="{ span: 5 }" :wrapper-col="{ span: 17 }">
                    <a-input
                        v-decorator="['code', { rules: [{ required: true, message: 'Please input your note!' }] }]">
                    </a-input>
                </a-form-item>
                <a-form-item label="项目路径" :label-col="{ span: 5 }" :wrapper-col="{ span: 17 }">
                    <a-input
                        v-decorator="['path', { rules: [{ required: true, message: 'Please input your note!' }] }]">
                    </a-input>
                </a-form-item>
            </a-form>
        </a-modal>

        <a-modal v-model="editModal.visible" title="Title" :width="520">
            <template slot="footer">
                <div v-if="editModal.readOnly">
                    <a-button key="back" type="primary" @click="editModal.visible = false">确认</a-button>
                </div>
                <div v-else>
                    <a-button key="back" @click="editModal.visible = false">取消</a-button>
                    <a-button key="submit" type="primary" @click="onProjectEdited" :loading="editModal.submitLoading" > 提交 </a-button>
                </div>
            </template>
            <a-form :form="editModal.form" >
                <a-form-item label="项目名称" :label-col="{ span: 7 }" :wrapper-col="{ span: 17 }">
                    <a-input :read-only="editModal.readOnly"
                        v-decorator="['name', { rules: [{ required: true, message: 'Please input your note!' }] }]">
                    </a-input>
                </a-form-item>
                <a-form-item label="项目代码" :label-col="{ span: 7 }" :wrapper-col="{ span: 17 }">
                    <a-input :read-only="editModal.readOnly"
                        v-decorator="['code', { rules: [{ required: true, message: 'Please input your note!' }] }]">
                    </a-input>
                </a-form-item>
                <a-form-item label="项目路径" :label-col="{ span: 7 }" :wrapper-col="{ span: 17 }">
                    <a-input :read-only="editModal.readOnly"
                        v-decorator="['path', { rules: [{ required: true, message: 'Please input your note!' }] }]">
                    </a-input>
                </a-form-item>
                <a-divider dashed></a-divider>
                <a-form-item label="数据库地址" :label-col="{ span: 7 }" :wrapper-col="{ span: 17 }">
                    <a-input-group compact>
                        <a-select :disabled="editModal.readOnly"
                                  style="width: 30%"
                                  v-decorator="['type']" >
                            <a-icon slot="suffixIcon" type="database"></a-icon>
                            <a-select-option value="mysql">mysql</a-select-option>
                            <a-select-option value="oracle">oracle</a-select-option>
                            <a-select-option value="h2">h2</a-select-option>
                        </a-select>
                        <a-input style="width: 70%" :read-only="editModal.readOnly"
                                 v-decorator="['ipPort']">
                        </a-input>
                    </a-input-group>
                </a-form-item>
                <a-form-item label="数据库名" :label-col="{ span: 7 }" :wrapper-col="{ span: 17 }">
                    <a-input :read-only="editModal.readOnly"
                             v-decorator="['dbname']">
                    </a-input>
                </a-form-item>
                <a-form-item label="用户名" :label-col="{ span: 7 }" :wrapper-col="{ span: 17 }">
                    <a-input :read-only="editModal.readOnly"
                             v-decorator="['username']">
                    </a-input>
                </a-form-item>
                <a-form-item label="密码" :label-col="{ span: 7 }" :wrapper-col="{ span: 17 }">
                    <a-input :read-only="editModal.readOnly"
                             v-decorator="['password']">
                    </a-input>
                </a-form-item>
            </a-form>
        </a-modal>
    </div>
</template>

<script>
    module.exports = {
        name: 'ProjectList',
        data () {
            return {
                loading: true,

                addModal: {
                    visible: false,
                    form: this.$form.createForm(this, { name: 'coordinated' }),
                },

                editModal: {
                    readOnly: true,
                    visible: false,
                    form: this.$form.createForm(this, { name: 'coordinated' }),
                    projectIndex: null,
                    submitLoading: false
                },

                projectList: this.$store.state.autocode.projects,
                projectDetails: [],
            }
        },

        computed: {
            projectListWithAdd() {
                return _.concat(this.projectList, { isAdd: true });
            }
        },

        mounted() {
            this.fetchProjectDetails();
        },


        methods: {
            fetchProjectDetails() {
                this.loading = true;
                const promises = this.projectList.map((p) => httpWithJsonRpc("project.getProjectConfig", { path: p.path }));
                return Promise.all(promises).then((datas) => {
                    this.projectDetails = datas.map(it => it.result);
                    this.loading = false;
                });
            },

            handleSubmit(e) {
                e.preventDefault();
                this.addModal.form.validateFields((err, values) => {
                    if (!err) {
                        this.projectDetails.push({ database: {} });
                        this.projectList = _.concat(this.projectList, values);
                        this.$store.commit('autocode/setProjects', this.projectList);
                        this.fetchProjectDetails();
                        this.addModal.visible = false;
                        this.addModal.form.resetFields();
                    }
                });
            },

            onProjectEdited(e) {
                e.preventDefault();
                this.editModal.form.validateFields((err, values) => {
                    if (!err) {
                        const index = this.editModal.projectIndex;
                        this.projectList[index].name = values.name;
                        this.projectList[index].code = values.code;
                        const oldPath = this.projectList[index].path;
                        this.projectList[index].path = values.path;
                        this.$store.commit('autocode/setProjects', this.projectList);

                        if (oldPath != values.path) {
                            this.fetchProjectDetails();
                            this.editModal.visible = false;
                        } else {
                            this.editModal.submitLoading = true;
                            httpWithJsonRpc("project.updateProjectConfig", {
                                path: this.projectList[index].path,
                                database: values
                            }).finally(() => {
                                this.editModal.submitLoading = false;
                                this.editModal.visible = false;
                                this.fetchProjectDetails();
                            });

                        }

                    }
                });
            },

            onRemoveProject(index) {
                this.projectList.splice(index);
                this.projectDetails.splice(index);
                this.$store.commit('autocode/setProjects', this.projectList);
            },

            onTestDbConnection(index) {
                const project = this.projectList[index];
                httpWithJsonRpc("project.testDatabaseConnection", {
                    path: project.path
                }).then(response => {
                    if (response.result.success) {
                        antd.notification.success({
                            message: '数据库连接测试成功',
                            description: `项目${project.name}(${project.code})数据库连接测试成功!`,
                            duration: 5
                        });
                    } else {
                        antd.notification.error({
                            message: '数据库连接测试失败',
                            description: `项目${project.name}(${project.code})数据库连接测试失败：${response.result.reason}`,
                            duration: null
                        });

                    }

                });
            },

            onOpenEditModal(index, readOnly) {
                this.editModal.projectIndex = index;
                this.editModal.visible = true;
                this.editModal.readOnly = readOnly;
                this.$nextTick(() => {
                        const values = Object.assign(this.projectList[index], this.projectDetails[index].database);
                        this.editModal.form.setFieldsValue(values);
                    }
                );
            },
        }
    }
</script>

<style scoped>
    .card-avatar {
        width: 48px;
        height: 48px;
        border-radius: 48px;
    }
    .new-btn {
        background-color: #fff;
        border-radius: 2px;
        width: 100%;
        height: 188px;
    }
    .meta-content {
        position: relative;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        height: 64px;
        -webkit-line-clamp: 3;
        -webkit-box-orient: vertical;
    }


    .ant-input-disabled {
        color: rgba(0,0,0,.65);
        cursor: auto;
    }
</style>
