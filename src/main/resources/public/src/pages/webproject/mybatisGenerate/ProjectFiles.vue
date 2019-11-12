<template>
    <a-table :pagination="false" :columns="columns" :data-source="data" :loading="loading">
        <template slot="file" slot-scope="text, record, index">
            <a-popover trigger="hover"
                :title="null" >
                <span slot="content"> {{record.path }}</span>
                {{ record.filename }}
            </a-popover>
        </template>

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
            key: 'file',
            scopedSlots: { customRender: 'file' }
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

        watch: {
            projectIndex(newV, oldV) {
                this.data = [];
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
                    this.data = response.result.map(item => {
                        item.filename = basename(item.path);
                        return item;
                    });
                    this.loading = false;
                });
            }
        }
    };
</script>

