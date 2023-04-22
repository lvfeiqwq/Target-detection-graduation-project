<template>
  <div
    v-loading="loading"
    class="container"
    element-loading-text="拼命加载中"
    element-loading-spinner="el-icon-loading"
    element-loading-background="rgba(255, 255, 255, 1)"
  >
    <el-card id="search" class="search">
      <el-row>
        <el-col :span="20">
          <el-select v-model="searchModel.category" placeholder="请选择事件种类" @change="getIncidentList">
            <el-option
              v-for="item in categories"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
          <el-input
            v-model="searchModel.place"
            placeholder="事件地点"
            clearable
          />
          <el-date-picker
            v-model="searchModel.date"
            format="yyyy 年 MM 月 dd 日"
            value-format="yyyy-MM-dd"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
          />
          <el-button
            type="primary"
            round
            icon="el-icon-search"
            @click="getIncidentList"
          >查询
          </el-button>
          <el-button
            type="danger"
            round
            icon="el-icon-search"
            @click="clearSearch"
          >重置
          </el-button>
        </el-col>
        <el-col :span="4" align="right">
          <el-button
            type="primary"
            icon="el-icon-plus"
            circle
            @click="openEditUI(null)"
          />
        </el-col>
      </el-row>
    </el-card>

    <el-card>
      <el-table :loading="loading" :data="incidentList" stripe style="width: 100%">
        <template slot="empty">
          <el-empty :image-size="200" />
        </template>
        <el-table-column label="#" width="80">
          <template slot-scope="scope">
            <!-- (pageNo-1) * pageSize + index + 1 -->
            {{
              (searchModel.pageNo - 1) * searchModel.pageSize + scope.$index + 1
            }}
          </template>
        </el-table-column>
        <el-table-column prop="id" label="事件ID" width="120" />
        <el-table-column prop="category" :formatter="tableFormatter" label="事件种类" width="200" />
        <el-table-column prop="incidentPlace" label="发生地点" width="180" />
        <el-table-column prop="incidentDate" label="发生日期" width="230" sortable />
        <el-table-column prop="detail" label="事件细节">
          <!--          <template slot-scope="scope">-->
          <!--            <el-tag v-if="scope.row.status == 1">正常</el-tag>-->
          <!--            <el-tag v-else type="danger">禁用</el-tag>-->
          <!--          </template>-->
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template slot-scope="scope">
            <el-button
              type="primary"
              icon="el-icon-edit"
              size="mini"
              circle
              @click="openEditUI(scope.row.id)"
            />
            <el-button
              type="danger"
              icon="el-icon-delete"
              size="mini"
              circle
              @click="deleteIncident(scope.row)"
            />
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-pagination
      :current-page="searchModel.pageNo"
      :page-sizes="[5, 8, 12, 20]"
      :page-size="searchModel.pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      background
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
    <el-dialog
      :title="title"
      :visible.sync="dialogFormVisible"
      @close="clearForm"
    >
      <el-form ref="incidentFormRef" :model="incidentForm" :rules="rules">
        <el-form-item
          label="事件类别"
          prop="category"
          :label-width="formLabelWidth"
        >
          <el-select v-model="incidentForm.category" placeholder="请选择事件种类">
            <el-option
              v-for="item in categories"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item
          label="发生地点"
          prop="incidentPlace"
          :label-width="formLabelWidth"
        >
          <el-input
            v-model="incidentForm.incidentPlace"
            autocomplete="off"
          />
        </el-form-item>
        <el-form-item
          label="发生时间"
          prop="incidentDate"
          :label-width="formLabelWidth"
        >
          <div class="block">
            <el-date-picker
              v-model="incidentForm.incidentDate"
              type="date"
              format="yyyy 年 MM 月 dd 日"
              value-format="yyyy-MM-dd"
              placeholder="选择日期"
            />
          </div>
        </el-form-item>
        <el-form-item
          label="事件细节"
          prop="detail"
          :label-width="formLabelWidth"
        >
          <el-input
            v-model="incidentForm.detail"
            autocomplete="off"
          />
        </el-form-item>

        <!--        <el-form-item-->
        <!--          v-if="title==='新增事件'"-->
        <!--          label="图片"-->
        <!--          :label-width="formLabelWidth"-->
        <!--        >-->
        <!--          <el-input v-model="incidentForm.phone" autocomplete="off" />-->
        <!--        </el-form-item>-->
        <el-form-item
          v-if="title==='新增事件'"
          label="图片"
          :label-width="formLabelWidth"
        >
          <el-upload
            action=""
            list-type="picture-card"
            :on-preview="handlePictureCardPreview"
            :on-remove="handleRemove"
            :on-change="handleChange"
            :on-exceed="handleExceed"
            :auto-upload="false"
            :file-list="fileList"
            limit="1"
            accept=".jpg,.png"
          >
            <i class="el-icon-plus" />
            <div slot="tip" class="el-upload__tip">只能上传一个jpg/png文件，且不超过500kb</div>
          </el-upload>
          <el-dialog :visible.sync="dialogVisible">
            <img width="100%" :src="dialogImageUrl" alt="">
          </el-dialog>
        </el-form-item>
        <el-form-item
          v-if="title==='修改事件'"
          label="图片"
          :label-width="formLabelWidth"
        >
          <el-upload
            action=""
            list-type="picture-card"
            :on-preview="handlePictureCardPreview"
            :on-remove="handleRemove"
            :on-change="handleChange"
            :before-upload="handleBeforeUpload"
            :on-exceed="handleExceed"
            :auto-upload="false"
            :file-list="fileList2"
            accept=".jpg,.png"
          >
            <el-image
              v-if="incidentForm.imgUrl !== null"
              :src="base64"
              class="avatar"
              alt=""
              style="width: 150px; height: 150px"
            />
            <!--            <i v-else class="el-icon-upload"/>-->
            <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过500kb</div>
          </el-upload>
          <el-dialog :visible.sync="dialogVisible">
            <img width="100%" :src="dialogImageUrl" alt="">
          </el-dialog>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="saveIncident">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import incidentApi from '@/api/incident'
import userApi from '@/api/user'
import { getToken } from '@/utils/auth'

export default {
  data() {
    return {
      id: '',
      base64: '',
      fileList: [],
      fileList2: [],
      hasFile: false,
      dialogImageUrl: '',
      dialogVisible: false,
      total: 0,
      formLabelWidth: '120px',
      dialogFormVisible: false,
      title: '',
      searchModel: {
        date: ['', ''],
        pageNo: 1,
        pageSize: 8
      },
      filePath: 'http://localhost:9999/images/',
      loading: false,
      incidentForm: {
        id: '',
        category: '',
        incidentDate: '',
        incidentPlace: '',
        detail: '',
        imgUrl: ''
      },
      incidentList: [],
      categories: [{
        value: 1,
        label: '未佩戴安全帽,未穿工装'
      },
      {
        value: 2,
        label: '未佩戴安全帽,穿工装'
      },
      {
        value: 3,
        label: '佩戴安全帽,未穿工装'
      }],
      value: '',
      rules: {
        category: [
          { required: true, message: '请选择事件种类', trigger: 'blur' }
        ],
        incidentPlace: [
          { required: true, message: '请填写事件地点', trigger: 'blur' }
        ],
        incidentDate: [
          { required: true, message: '请输入发生时间', trigger: 'blur' }
        ]
      }
    }
  },
  mounted() {
    this.getIncidentList()
  },
  methods: {
    tableFormatter(row) {
      switch (row.category) {
        case 1:
          return '未佩戴安全帽,未穿工装'
        case 2:
          return '未佩戴安全帽,穿工装'
        case 3:
          return '佩戴安全帽,未穿工装'
      }
    },
    handleBeforeUpload(file, fileList) {
      file = this.filePath + this.incidentForm.imgUrl
    },
    handleRemove(file, fileList) {
      console.log(file, fileList)
    },
    handleExceed(file, fileList) {
      this.$message.warning('最多上传一个文件')
      this.fileList = file
    },
    handlePictureCardPreview(file) {
      this.dialogImageUrl = file.url
      this.dialogVisible = true
    },
    handleChange(file, fileList) {
      if (fileList.length >= 2) {
        return
      }
      this.fileList = fileList
    },
    // handleChange(e) {
    //   console.log(e, e[0], e[1])
    //   this.startTime = `${e[0]} 00:00:00`
    //   this.endTime = `${e[1]} 23:59:59`
    // },
    deleteIncident(incident) {
      this.$confirm(`您确认删除该事件吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        incidentApi.deleteIncidentById(incident.id).then(response => {
          this.$message({
            type: 'success',
            message: response.message
          })
          this.getIncidentList()
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    },
    handleSizeChange(pageSize) {
      this.searchModel.pageSize = pageSize
      this.getIncidentList()
    },
    handleCurrentChange(pageNo) {
      this.searchModel.pageNo = pageNo
      this.getIncidentList()
    },
    getIncidentList() {
      this.loading = true
      // console.log(this.searchModel)
      incidentApi.getIncidentList(this.searchModel).then(response => {
        this.incidentList = response.data.rows
        this.total = response.data.total
      })
      setTimeout(() => {
        this.loading = false
      }, 800)
    },
    clearForm() {
      this.$refs['incidentFormRef'].resetFields()
      this.$refs.incidentFormRef.clearValidate()
    },
    clearSearch() {
      this.searchModel.category = null
      this.searchModel.place = null
      this.searchModel.date = ['', '']
    },
    openEditUI(id) {
      if (id == null) {
        this.fileList = []
        this.title = '新增事件'
        this.incidentForm = {}
      } else {
        this.fileList2 = []
        this.fileList = []
        this.title = '修改事件'
        // 根据id查询用户数据
        incidentApi.getIncidentById(id).then(response => {
          this.incidentForm = response.data.incident
          this.base64 = response.data.base64
        })
      }
      this.dialogFormVisible = true
      // this.$alert(this.title)
    },
    saveIncident() {
      this.$refs.incidentFormRef.validate((valid) => {
        if (valid) {
          const formData = new FormData()
          for (const key in this.incidentForm) {
            formData.append(key, this.incidentForm[key])
          }
          // console.log('this.fileList.length', this.fileList.length)
          if (this.fileList.length !== 0) {
            formData.append('file', this.fileList[0].raw)
          }
          userApi.getAllInfo(getToken()).then(
            response => {
              this.id = response.data.id
            }
          )
          formData.append('id', this.id)
          formData.append('time', new Date().toString())
          incidentApi.saveIncident(formData, this.incidentForm.id).then(response => {
            // 成功提示
            this.$message({
              message: response.message,
              type: 'success'
            })
            // 关闭对话框
            this.dialogFormVisible = false
            // 刷新表格
            this.incidentForm = {}
            this.fileList2 = []
            this.getIncidentList()
          })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    }
  }
}
</script>

<style>
.search {
  margin-top: 35px;
}

#search .el-input {
  width: 200px;
  margin-right: 10px;
}

.el-dialog .el-input {
  width: 85%;
}
</style>
