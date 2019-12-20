<template>
  <div id="app">
    <el-container>
      <el-header>
        <TopNav></TopNav>
      </el-header>
      <el-main>
        <router-view></router-view>
      </el-main>
      <el-footer>
        <Bottom></Bottom>
      </el-footer>
    </el-container>
  </div>
</template>
<script>
  import TopNav from './components/TopNav'
  import Bottom from './components/Bottom'
  export default {
    name: 'App',
    created() {
      this.$notify.info({
          title: '消息',
          dangerouslyUseHTMLString: true,
          message: '<i class="el-icon-microphone"></i>欢迎来到本站'
        });
    },
    components: {
      TopNav,
      Bottom
    }
  }
</script>
<style scoped>
  .el-main {
    padding: 1rem;
  }
</style>
