<template>
  <div class="top">
    <el-menu :mode="menuAttribute.mode" :default-active="menuAttribute.defaultActive" :router="menuAttribute.router">
      <el-menu-item index="/"><i class="el-icon-s-home"></i>首页</el-menu-item>
      <el-menu-item index="2"><i class="el-icon-sunny"></i>生活笔记</el-menu-item>
      <el-menu-item index="3"><i class="el-icon-hot-water"></i>技术杂谈</el-menu-item>
      <el-menu-item index="4"><i class="el-icon-sugar"></i>福利专区</el-menu-item>
      <el-menu-item index="5"><i class="el-icon-coffee-cup"></i>关于博主</el-menu-item>
      <el-menu-item index="6"><i class="el-icon-ship"></i>给我留言</el-menu-item>
      <el-menu-item index="8"><i class="el-icon-chat-square"></i>技术交流</el-menu-item>
      <el-menu-item index="9"><i class="el-icon-fork-spoon"></i>项目合作</el-menu-item>
      <el-menu-item>
        <el-input size="mini" placeholder="搜索文章" prefix-icon="el-icon-search" clearable></el-input>
      </el-menu-item>
    </el-menu>

  </div>
</template>
<script>
  export default {
    name: 'TopNav',
    data() {
      return {
        menuAttribute: {
          mode: 'horizontal',
          defaultActive: '/',
          router: true
        }
      };
    }
  }
</script>
<style scoped>
</style>
