<template>
  <div class="footer">
    <el-footer height="auto">
      <el-row>
        <el-link href="https://element.eleme.io" target="_blank" :underline="false">关于博客</el-link><span class="footer-label">丨</span>
        <el-link href="https://element.eleme.io" target="_blank" :underline="false">免责声明</el-link><span class="footer-label">丨</span>
        <el-link href="https://element.eleme.io" target="_blank" :underline="false">文章投稿</el-link><span class="footer-label">丨</span>
        <el-link href="https://element.eleme.io" target="_blank" :underline="false">友链申请</el-link><span class="footer-label">丨</span>
        <el-link href="https://element.eleme.io" target="_blank" :underline="false">意见反馈</el-link>
      </el-row>
      <el-row>
        <span class="footer-label">Copyright © 2016-2019 LOLDK.All Rights Reserved. <el-link href="https://element.eleme.io" target="_blank" :underline="false" >粤ICP备16065100号-2</el-link></span>
      </el-row>
    </el-footer>
  </div>
</template>
<script>
  export default {
    name: 'Bottom',
    data() {
      return {
        menuAttribute: {
          mode: 'horizontal',
          defaultActive: '1',
          router: true
        }
      };
    }
  }
</script>
<style scoped>
  .footer {
    text-align: center;
  }

  .footer .footer-label {
    font-weight: 500;
    font-size: 14px;
    color: #606266;
  }

  .footer a {
    margin: 0.8rem;
  }

  .el-footer {
    width: 80%;
    margin: 0 auto;
  }
</style>
