<template>
  <el-form ref="userForm" :model="user" :rules="rules">
    <el-form-item label="用户名">
      <el-input v-model.trim="user.username" />
    </el-form-item>
    <el-form-item label="手机号">
      <el-input v-model.trim="user.phone" />
    </el-form-item>
    <el-form-item label="邮箱">
      <el-input v-model.trim="user.email" />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="submit">更新</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import userApi from '@/api/user'
import userManageApi from '@/api/userManage'
import { getToken } from '@/utils/auth'
export default {
  data() {
    const validatePhone = (rule, value, callback) => {
      const phoneRegex = /^1[3-9]\d{9}$/
      if (!phoneRegex.test(value)) {
        callback(new Error('手机号码不合法'))
      } else {
        callback()
      }
    }
    const checkEmail = (rule, value, callback) => {
      const reg =
        /^[a-zA-Z0-9]+([-_.][a-zA-Z0-9]+)*@[a-zA-Z0-9]+([-_.][a-zA-Z0-9]+)*\.[a-z]{2,}$/
      if (!reg.test(value)) {
        return callback(new Error('邮箱格式错误'))
      }
      callback()
    }
    return {
      user: {
        id: '',
        phone: '',
        username: '',
        email: ''
      },
      rules: {
        phone: [{ require: true, trigger: 'blur', validator: validatePhone }],
        email: [{ require: true, trigger: 'blur', validator: checkEmail }]
      }
    }
  },
  created() {
    this.refresh()
  },
  methods: {
    submit() {
      this.$refs.userForm.validate(valid => {
        if (valid) {
          userManageApi.updateUser(this.user).then(
            response => {
              this.$message({
                message: '用户信息成功更新',
                type: 'success',
                duration: 5 * 1000
              })
            }
          )
        }
      })
    },
    refresh() {
      userApi.getAllInfo(getToken()).then(
        response => {
          this.user.id = response.data.id
          this.user.phone = response.data.phone
          this.user.email = response.data.email
          this.user.username = response.data.username
        }
      )
    }
  }
}
</script>
