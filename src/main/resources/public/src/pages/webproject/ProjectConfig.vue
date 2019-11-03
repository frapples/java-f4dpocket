<template>
  <page-layout title="项目配置">
    <a-card class="card" title="项目信息" :bordered="false">
      <table-form></table-form>
    </a-card>
    <a-card class="card" title="作者信息" :bordered="false">
      <a-table
          style="margin-bottom: 24px"
          :columns="goodsColumns"
          :data-source="goodsData"
          :pagination="false"
      >
      </a-table>
    </a-card>
  </page-layout>
</template>

<script>
  const PageLayout = httpVueLoader('../../layouts/PageLayout.vue');
  const TableForm = httpVueLoader("./projectConfig/TableForm.vue");

  const goodsColumns = [
    {
      title: '姓名',
      dataIndex: 'name',
      key: 'name'
    },
    {
      title: '注释昵称',
      dataIndex: 'nickName',
      key: 'nickName'
    },
    {
      title: '注释邮箱',
      dataIndex: 'email',
      key: 'email'
    }
  ];

  const goodsData = [
    {
      name: '张三',
      nickName: 'Tom',
      email: 'tom@example.com',
    },
    {
      name: '李四',
      nickName: 'Harry',
      email: 'harry@example.com',
    },
  ];

  module.exports = {
    name: 'BasicDetail',
    components: {PageLayout, TableForm },
    data () {
      return {
        goodsColumns,
        goodsData
      }
    }
  }
</script>

<style scoped>
  .card {
    margin-bottom: 24px;
  }
</style>
