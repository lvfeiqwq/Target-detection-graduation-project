<template>
  <div style="margin-top: 40px">
    <div
      v-loading="loading"
      class="container"
      element-loading-text="拼命加载中"
      element-loading-spinner="el-icon-loading"
      element-loading-background="rgba(255, 255, 255, 1)"
    >
      <div class="handle-box" @keyup.enter.native="getDetectHistory">
        <el-input
          v-model="searchModel.name"
          :clearable="true"
          placeholder="搜索作品名称"
          class="handle-input mr10"
          @keyup.enter.native="getDetectHistory"
        />
        <el-date-picker
          v-model="searchModel.date"
          type="datetimerange"
          value-format="yyyy-MM-dd hh:mm:ss"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        />
        <el-select v-model="searchModel.sortType" placeholder="请选择" @change="getDetectHistory" @keyup.enter.native="getDetectHistory">
          <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value"
            :disabled="item.disabled"
          />
        </el-select>
        <el-select v-model="searchModel.editedType" placeholder="请选择图片类型" @change="getDetectHistory" @keyup.enter.native="getDetectHistory">
          <el-option
            v-for="item in options1"
            :key="item.value"
            :label="item.label"
            :value="item.value"
            :disabled="item.disabled"
          />
        </el-select>
        <el-button type="primary" icon="el-icon-search" @click="handleSearch" @keyup.enter.native="getDetectHistory">
          搜索
        </el-button>
        <el-button type="primary" icon="el-icon-refresh" @click="handleRefresh" @keyup.enter.native="getDetectHistory">
          刷新
        </el-button>
        <el-tooltip placement="top">
          <div slot="content">
            1.所有图片均为用户自己上传并检测<br>
            2.目前暂不支持视频查询
          </div>
          <i class="el-icon-question" />>
        </el-tooltip>
      </div>
      <div>
        <el-row class="row-box">
          <el-col
            v-for="item in imageData"
            :key="item.id"
            :span="5"
            style="margin-bottom: 10px"
            :offset="1"
          >
            <el-card shadow="hover" class="el-card">
              <div slot="header" class="clearfix">
                <i class="el-icon-picture-outline" /><span style="margin-left: 5px">{{ item.name }}</span>
                <el-tag v-if="item.edited === 0" size="small" type="danger">未确认</el-tag>
                <el-tag v-if="item.edited === 1" size="small" effect="dark" type="success">已确认</el-tag>
              </div>
              <div>
                <ul class="role-info">
                  <li>
                    <div class="role-left">
                      检测时间{{ item.time }}
                    </div>
                  </li>
                  <li>
                    <div class="role-left">
                      <el-image style="height: 150px" :src="item.base64" />
                    </div>
                  </li>
                </ul>
              </div>
              <el-button type="primary" :disabled="item.edited === 1" size="mini" @click.native="handleEdit(item.id)">编辑</el-button>
              <el-popover
                :model="visible"
                placement="bottom"
                width="160"
              >
                <p>确定要删除图像吗？</p>
                <div style="text-align: right; margin: 0">
                  <el-button size="mini" type="text" @click.native="visible = false">取消</el-button>
                  <el-button type="primary" size="mini" @click="handleDelete(item.id)">确定</el-button>
                </div>
                <el-button slot="reference" size="mini" type="danger">删除</el-button>
              </el-popover>
              <div
                style="display: inline-block; float: right; cursor: pointer"
              >
                <el-tooltip
                  effect="dark"
                  content="点击查看详情"
                  placement="bottom"
                  @click.native="handleDetail(item.id)"
                >
                  <i class="el-icon-menu" />
                </el-tooltip>
              </div>
            </el-card>
          </el-col>
        </el-row>
        <el-dialog
          :title="title"
          :visible.sync="dialogVisible"
          width="30%"
        >
          <span style="font-size: large">检测时间：{{ time }}</span><br><br>
          <el-card>
            <el-table :data="result">
              <el-table-column label="序号">
                <template slot-scope="scope">
                  <span>{{ scope.$index + 1 }}</span>
                </template>
              </el-table-column>
              <el-table-column :formatter="tableFormatter" prop="category" label="种类">
                <!--                <template slot-scope="scope">-->
                <!--                  <el-select v-model="scope.row.category">-->
                <!--                    <el-option :label="scope.row.category">A</el-option>-->
                <!--                    <el-option label="B">B</el-option>-->
                <!--                    <el-option label="C">C</el-option>-->
                <!--                  </el-select>-->
                <!--                </template>-->
              </el-table-column>
              <el-table-column prop="coordinate" label="坐标(左上右下)" width="150px">
                <template slot-scope="scope">
                  <div>{{ "[" + scope.row.coordinate.join(",") + "]" }}</div>
                </template>
              </el-table-column>
              <el-table-column prop="accuracy" label="置信度" width="100px" />
            </el-table>
            <!--            <span>检测时间：{{ time }}</span>-->
          </el-card>
          <el-card>
            <el-image :src="base64" :preview-src-list="[base64]" :z-index="parseInt(height)" />
          </el-card>
          <span slot="footer" class="dialog-footer">
            <el-button @click="dialogVisible = false">取 消</el-button>
            <el-button type="primary" @click="dialogVisible = false">确 定</el-button>
          </span>
        </el-dialog>

        <el-dialog
          :title="title"
          :visible.sync="dialogVisible1"
          width="35%"
        >
          <span style="font-size: large">检测时间：{{ time }}</span><br><br>
          <el-card>
            <el-table :data="result">
              <el-table-column label="序号" width="60px">
                <template slot-scope="scope">
                  <span>{{ scope.$index + 1 }}</span>
                </template>
              </el-table-column>
              <el-table-column width="150px" :formatter="tableFormatter" prop="category" label="种类">
                <template slot-scope="scope">
                  <el-select v-model="scope.row.category" style="width: 150px">
                    <el-option label="安全帽" :value="0" />
                    <el-option label="人" :value="1" />
                    <el-option label="工装" :value="2" />
                    <el-option label="非工装" :value="3" />
                    <el-option label="非安全帽" value="4" />
                    <el-option label="无效信息" :value="5" />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column prop="coordinate" label="坐标(左上右下)" width="150px">
                <template slot-scope="scope">
                  <div>{{ "[" + scope.row.coordinate.join(",") + "]" }}</div>
                </template>
              </el-table-column>
              <el-table-column prop="accuracy" label="置信度" width="100px" />
            </el-table>
            <!--            <span>检测时间：{{ time }}</span>-->
          </el-card>
          <el-card>
            <el-image :src="base64" :preview-src-list="[base64]" :z-index="parseInt(height)" />
          </el-card>
          <span slot="footer" class="dialog-footer">
            <el-button @click="dialogVisible = false">取 消</el-button>
            <el-button type="primary" @click="handleSave">确 定</el-button>
          </span>
        </el-dialog>
      </div>
      <div class="pagination">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :current-page="searchModel.pageNo"
          :page-size="searchModel.pageSize"
          :total="total"
          :page-sizes="[8, 12, 20]"
          @current-change="handleCurrentChange"
          @size-change="handleSizeChange"
        />
      </div>
    </div>
  </div>
</template>

<script>
import imageApi from '@/api/image'

export default {
  name: 'ResourceCenter',

  data() {
    return {
      url: '',
      type: '',
      state: '',
      total: 0,
      imageData: [],
      title: '',
      time: '',
      result: [],
      name: '',
      base64: '',
      height: 2000,
      searchModel: {
        pageNo: 1,
        pageSize: 8,
        name: '',
        date: ['', ''],
        sortType: 1,
        editedType: ''
      },
      visible: false,
      salesData: [],
      loading: false,
      dialogVisible: false,
      dialogVisible1: false,
      options: [
        {
          value: 1,
          label: '按时间由近及远排序'
        },
        {
          value: 2,
          label: '按时间由远及近排序'
        }
      ],
      options1: [
        {
          value: 0,
          label: '未确认'
        },
        {
          value: 1,
          label: '已编辑并确认'
        }
      ]
    }
  },
  created() {
    this.getDetectHistory()
  },
  methods: {
    process(str) {
      switch (str) {
        case 0:
          return '安全帽'
        case 1:
          return '人'
        case 2:
          return '工装'
        case 3:
          return '非工装'
        case 4:
          return '非安全帽'
        case 5:
          return '无效识别'
      }
    },
    tableFormatter(row) {
      switch (row.category) {
        case 0:
          return '安全帽'
        case 1:
          return '人'
        case 2:
          return '工装'
        case 3:
          return '非工装'
        case 4:
          return '非安全帽'
        case 5:
          return '无效识别'
      }
    },
    getDetectHistory() {
      this.loading = true
      imageApi.getDetectHistory(this.searchModel).then(
        response => {
          this.imageData = response.data.rows
          this.total = response.data.total
        }
      )
      setTimeout(() => {
        this.loading = false
      }, 1000)
    },
    handleSizeChange(pageSize) {
      this.searchModel.pageSize = pageSize
      this.getDetectHistory()
    },
    handleClose() {
      this.visible = false
    },
    handleCurrentChange(pageNo) {
      this.searchModel.pageNo = pageNo
      this.getDetectHistory()
    },
    // 触发搜索按钮
    handleSearch() {
      this.getDetectHistory()
    },
    handleRefresh() {
      this.getDetectHistory()
    },
    handleDetail(id) {
      this.dialogVisible = true
      imageApi.getDetailByID(id).then(
        response => {
          this.url = response.data.url
          this.title = response.data.name
          this.time = response.data.time
          this.base64 = response.data.base64
          const result = JSON.parse(response.data.result)
          this.result = result.map(obj => {
            const accuracy = obj.accuracy
            const formattedAccuracy = accuracy.toFixed(3)
            return { ...obj, accuracy: formattedAccuracy }
          })
          // console.log(this.result)
        })
    },
    handleDelete(id) {
      imageApi.deleteById(id).then(
        response => {
          this.$notify.success({
            title: '消息',
            message: '成功删除图像'
          })
          this.getDetectHistory()
        }
      )
      this.visible = false
    },
    handleEdit(id) {
      this.dialogVisible1 = true
      imageApi.getDetailByID(id).then(
        response => {
          this.url = response.data.url
          this.title = response.data.name
          this.time = response.data.time
          this.base64 = response.data.base64
          const result = JSON.parse(response.data.result)
          this.result = result.map(obj => {
            const accuracy = obj.accuracy
            const formattedAccuracy = accuracy.toFixed(3)
            return { ...obj, accuracy: formattedAccuracy }
          })
          // console.log(this.result)
        })
    },
    handleSave(done) {
      this.$confirm('只有一次修改机会,确认保存?')
        .then(_ => {
          const formData = new FormData()
          const formObjects = this.result.reduce((acc, item, index) => {
            const formObj = {
              category: item.category,
              accuracy: item.accuracy,
              coordinate: item.coordinate
            }
            acc.push(formObj)
            formData.append(`formObjects[${index}]`, JSON.stringify(formObj))
            return acc
          }, [])
          // formObjects.append('id', this.id)
          console.log(typeof formObjects)
          console.log(formObjects)
          imageApi.editLabel(formObjects, this.url).then(
            response => {
              this.$message({
                message: response.message,
                type: 'success'
              })
              this.getDetectHistory()
              this.dialogVisible1 = false
            }
          )
          done()
          // this.getDetectHistory()
        })
        .catch()
    }

  }
}
</script>

<style lang="scss">
.handle-box {
  margin-bottom: 20px;
}

.handle-select {
  width: 120px;
}

.row-box {
  display: flex;
  flex-flow: wrap;
}

.row-box .el-card {
  min-width: 100%;
  height: 100%;
  margin-right: 30px;
  border: 0;
}

.handle-input {
  width: 200px;
  display: inline-block;
}

.table {
  width: 100%;
  font-size: 14px;
}

.red {
  color: #ff0000;
}

.mr10 {
  margin-right: 10px;
}

.table-td-thumb {
  display: block;
  margin: auto;
  width: 40px;
  height: 40px;
}

.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}

.clearfix:after {
  clear: both
}

.role-info {
  margin-top: 0;
  padding-top: 0;
  padding-left: 0;
  list-style: none;

  li {
    border-bottom: 1px solid #f0f3f4;
    padding: 11px 0;
    font-size: 12px;
  }

  .role-left {
    color: rgb(148, 137, 137);
    overflow: hidden;
    white-space: nowrap;
    text-align: left;
    text-overflow: ellipsis;
  }

  .line {
    width: 100%;
    height: 1px;
    border-top: 1px solid #ccc;
  }
}
</style>
