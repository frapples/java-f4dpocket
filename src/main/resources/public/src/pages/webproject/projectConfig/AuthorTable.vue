<template>
  <form :autoFormCreate="(form) => this.form = form">
    <a-table
        :columns="columns"
        :data-source="dataSource"
        :pagination="false"
    >
      <template  v-for="(col, i) in ['name', 'nickName', 'email']" :slot="col" slot-scope="text, record, index">
        <a-input
            :key="col"
            v-if="record.editable"
            style="margin: -5px 0"
            :value="text"
            :placeholder="columns[i].title"
            @change="e => handleChange(e.target.value, record.key, col)"></a-input>
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
      title: '姓名',
      dataIndex: 'name',
      key: 'name',
      scopedSlots: { customRender: 'name' }
    },
    {

      title: '注释昵称',
      dataIndex: 'nickName',
      key: 'nickName',
      scopedSlots: { customRender: 'nickName' }
    },
    {
      title: '注释邮箱',
      dataIndex: 'email',
      key: 'email',
      scopedSlots: { customRender: 'email' }
    },

    {
      title: '操作',
      key: 'action',
      scopedSlots: { customRender: 'operation' }
    }

  ];

  module.exports = {
    name: 'AuthorTable',
    data () {
      return {
        columns: columns,
        dataSource: this.$store.state.autocode.authors,
      }
    },
    methods: {
      handleSubmit (e) {
        e.preventDefault()
      },
      newMeber () {
        let max = _.maxBy(this.dataSource, it => it.key);
        this.dataSource.push({
          key: _.isNil(max) ? 1 : max.key + 1,
          name: '',
          nickName: '',
          email: '',
          editable: true,
          isNew: true
        })
      },
      remove (key) {
        const newData = this.dataSource.filter(item => item.key !== key)
        this.dataSource = newData;
        this.$store.commit("autocode/setAuthors", this.dataSource);
      },
      saveRow (key) {
        let target = this.dataSource.filter(item => item.key === key)[0]
        target.editable = false
        target.isNew = false
        this.$store.commit("autocode/setAuthors", this.dataSource);
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
      },
    }
  }
</script>

<style scoped>

</style>
