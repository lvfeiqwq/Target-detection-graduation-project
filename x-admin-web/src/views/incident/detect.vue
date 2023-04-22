<template>
  <div>
    <el-card class="card">
      <div slot="header" class="clearfix">
        <span>Yolo v5检测</span>
        <el-tooltip placement="top">
          <div slot="content">
            1.所有图片均为用户自己上传并检测<br>
            2.目前暂不支持视频识别<br>
            3.如需更改样本请前往"检测记录"页面
          </div>
          <i class="el-icon-question" />>
        </el-tooltip>
      </div>
      <el-upload
        ref="upload"
        :show-file-list="false"
        :show-progress="false"
        accept=".jpg,.png"
        class="upload-demo"
        action="http://localhost:9999/image/getImage"
        :on-preview="handlePreview"
        :on-remove="handleRemove"
        :on-change="handleChange"
        :on-success="handleSuccess"
        :file-list="fileList"
        :auto-upload="true"
      >
        <el-button slot="trigger" type="primary">上传图像</el-button>
        <!--        <el-button style="margin-left: 10px;" size="small" type="success" @click="submitUpload">上传到服务器</el-button>-->
        <el-button style="margin-left: 20px;" type="success" @click="gotoDetect">开始检测</el-button>
        <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过500kb</div>
      </el-upload>
      <el-row>
        <el-col :xs="{span: 20}" :sm="{span: 11}" :md="{span: 11}" :lg="{span: 11}" :xl="{span: 11}">
          <el-card
            v-loading="loading1"
            style="width: 600px;height: 450px;"
            element-loading-text="拼命加载中"
            element-loading-spinner="el-icon-loading"
            element-loading-background="rgba(255, 255, 255, 1)"
          >
            <div slot="header" class="clearfix" style="text-align: center;">
              <span>原始图像</span>
            </div>
            <div style="text-align: center;">
              <el-image v-if="show2" :src="base641" :preview-src-list="[base641]" :z-index="height" style="height: 400px;object-fit: contain; max-width: 100%;" />
            </div>
          </el-card>
        </el-col>
        <el-col :xs="{span: 20}" :sm="{span: 11}" :md="{span: 11}" :lg="{span: 11}" :xl="{span: 11}">
          <el-card
            v-loading="loading2"
            style="width: 600px;height: 450px;"
            element-loading-text="正在检测中"
            element-loading-spinner="el-icon-loading"
            element-loading-background="rgba(255, 255, 255, 1)"
          >
            <div slot="header" class="clearfix" style="text-align: center;">
              <span>检测图像</span>
            </div>
            <div style="text-align: center;">
              <el-image v-if="show" :src="base64" :preview-src-list="[base64]" :z-index="height" style="height: 400px; justify-content:center " />
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script>
import detectApi from '@/api/detect'

export default {
  data() {
    return {
      src: 'https://cube.elemecdn.com/6/94/4d3ea53c084bad6931a56d5158a48jpeg.jpeg',
      id: null,
      fileList: [],
      imageUrl: '',
      base64: '',
      base641: '',
      label: '',
      names: '',
      show: false,
      url1: '',
      show2: false,
      loading1: false,
      loading2: false,
      height: 2000,
      checkSize: false
    }
  },
  methods: {
    submitUpload() {
      this.$refs.upload.submit()
    },
    handleRemove(file, fileList) {
      console.log(file, fileList)
    },
    handlePreview(file) {
      console.log(file)
    },
    handleChange(file, fileList) {
      if (this.checkSize === false) {
        this.loading1 = true
      }
      console.log(file)
    },
    handleSuccess(response, file, fileList) {
      // this.$refs.upload.$refs.progress.style.display = 'none' // 隐藏进度条
      this.imageUrl = response.data.image.url
      this.base641 = response.data.base64
      // this.url1 = 'http://localhost:9999/detect/' + response.data.image.url
      this.show2 = true
      this.id = response.data.image.id
      setTimeout(() => {
        this.loading1 = false
      }, 1000)
      this.$message({
        message: '上传成功',
        type: 'success'
      })
    },
    beforeUpload(file) {
      this.$refs.upload.clearFiles() // 清空已选文件
      this.uploadProgress = 0 // 重置上传进度
      this.$nextTick(() => {
        this.$refs.upload.$refs.progress.style.display = 'none' // 隐藏进度条
      })
      const isLt2M = file.size / 1024 / 1024 < 2
      if (!isLt2M) {
        this.$message.error('上传文件大小不能超过 2MB!')
      }
      this.checkSize = isLt2M
      return isLt2M
    },
    gotoDetect() {
      this.$notify.info({
        title: '消息',
        message: '正在检测中'
      })
      this.loading2 = true
      const formData = new FormData()
      formData.append('url', this.imageUrl)
      console.log(this.imageUrl)
      console.log(formData.get('url'))
      detectApi.postImage(formData).then(
        response => {
          this.base64 = 'data:image/png;base64,' + response.img
          this.show = true
          this.label = response.label
          this.names = response.names
          this.loading2 = false
          this.$notify.success({
            title: '成功',
            message: '已完成图像检测'
          })
        }
      )
    }
  }
}
</script>

<style scoped>
.card{
  margin-top: 50px;
  height: 83vh !important;
  width:95vw !important;
}
.el-card {
  flex: 1;
  margin-right: 10px;
  margin-bottom: 10px;
}
</style>
