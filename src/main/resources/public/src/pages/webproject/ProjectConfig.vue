<template>
  <page-layout title="项目配置">
    <project-list></project-list>
    <a-card class="card" title="作者信息" :bordered="false">
        <author-table></author-table>
    </a-card>
  </page-layout>
</template>

<script>
  const PageLayout = httpVueLoader('../../layouts/PageLayout.vue');
  const AuthorTable = httpVueLoader("./projectConfig/AuthorTable.vue");
  const ProjectList = httpVueLoader("./projectConfig/ProjectList.vue");

  module.exports = {
    name: 'BasicDetail',
    components: {PageLayout, AuthorTable, ProjectList },
    data () {
      return {
      }
    }
  }
</script>

<style scoped>
  .card {
    margin-bottom: 24px;
  }
</style>
