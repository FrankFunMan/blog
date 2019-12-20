<template> 
  <el-card class="form-container" shadow="never">
    <el-form :model="permission" :rules="rules" ref="permissionFrom" label-width="150px">
      <el-form-item label="权限名：" prop="name">
        <el-input v-model="permission.name"></el-input>
      </el-form-item>
      <el-form-item label="权限值：">
        <el-input v-model="permission.value"></el-input>
      </el-form-item>
      <el-form-item label="菜单url：">
        <el-input v-model="permission.uri"></el-input>
      </el-form-item>
      <el-form-item label="父级菜单：">
        <el-select v-model="permission.pid" clearable placeholder="请选择">
          <el-option
            v-for="item in menus"
            :key="item.id"
            :label="item.name"
            :value="item.id">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="菜单图像：" prop="logo">
        <single-upload v-model="permission.icon"></single-upload>
      </el-form-item>
      <el-form-item label="权限类型：">
        <el-radio-group v-model="permission.type">
          <el-radio :label="0">目录</el-radio>
          <el-radio :label="1">菜单</el-radio>
          <el-radio :label="2">按钮</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="是否显示：">
        <el-radio-group v-model="permission.status">
          <el-radio :label="1">是</el-radio>
          <el-radio :label="0">否</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit('permissionFrom')">提交</el-button>
        <el-button v-if="!isEdit" @click="resetForm('permissionFrom')">重置</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>
<script>
  import {createPermission, getPermission, updatePermission, list} from '@/api/permission'
  import SingleUpload from '@/components/Upload/singleUpload'

  const defaultPermission = {
    name: '',
    pid:0
  };
  export default {
    name: 'PermissionDetail',
    components: {SingleUpload},
    props: {
      isEdit: {
        type: Boolean,
        default: false
      }
    },
    data() {
      return {
        permission: Object.assign({}, defaultPermission),
        rules: {
          name: [
            {required: true, message: '请输入菜单名', trigger: 'blur'}
          ],

        },
        menus: []
      }
    },
    created() {
      this.getPermissionList();
      if (this.isEdit) {
        getPermission(this.$route.query.id).then(response => {
          this.permission = response.data;
        });
      } else {
        this.permission = Object.assign({}, defaultPermission);
      }
    },
    methods: {
      getPermissionList() {
        list().then(response => {
          this.menus = response.data;
        });
      },
      onSubmit(formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            this.$confirm('是否提交数据', '提示', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning'
            }).then(() => {
              if (this.isEdit) {
                updatePermission(this.$route.query.id, this.permission).then(response => {
                  this.$refs[formName].resetFields();
                  this.$message({
                    message: '修改成功',
                    type: 'success',
                    duration: 1000
                  });
                  this.$router.back();
                });
              } else {
                createPermission(this.permission).then(response => {
                  this.$refs[formName].resetFields();
                  this.permission = Object.assign({}, defaultPermission);
                  this.$message({
                    message: '提交成功',
                    type: 'success',
                    duration: 1000
                  });
                  this.$router.back();
                });
              }
            });

          } else {
            this.$message({
              message: '验证失败',
              type: 'error',
              duration: 1000
            });
            return false;
          }
        });
      },
      resetForm(formName) {
        this.$refs[formName].resetFields();
        this.permission = Object.assign({}, defaultPermission);
      }
    }
  }
</script>
<style>
</style>


