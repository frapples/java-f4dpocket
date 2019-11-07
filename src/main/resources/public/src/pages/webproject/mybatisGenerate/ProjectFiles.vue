<template>
    <a-table :columns="columns" :data-source="data" :loading="loading">


        <template slot="operation" slot-scope="text, record, index">
            <a-popconfirm title="是否要重新生成此文件？" >
                <a>更新</a>
            </a-popconfirm>
        </template>

    </a-table>
</template>

<script>
    const columns = [
        {
            title: '文件',
            dataIndex: 'name',
            key: 'name',
        },
        {

            title: '表名',
            dataIndex: 'dbTableName',
            key: 'dbTableName',
        },

        {
            title: '操作',
            key: 'action',
            scopedSlots: { customRender: 'operation' }
        }

    ];
    module.exports = {
        props: ["projectIndex"],
        name: 'ProjectFiles',
        data() {
            return {
                columns: columns,
                data: [],
                loading: false
            }
        },

        computed: {

            projectList() {
                return this.$store.state.autocode.projects;
            },

        },

        methods: {
            detect() {
                const project = this.projectList[this.projectIndex];
                if (!project) {
                    return;
                }

                this.loading = true;
                httpWithJsonRpc("generator.detectAllModules", {
                    path: project.path
                })
                .then((response) => {
                    this.data = response.result;
                    this.loading = false;
                });
            }
        }
    };
</script>

