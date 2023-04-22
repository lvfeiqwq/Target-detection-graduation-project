<template>
  <div>
    <el-card class="card">
      <el-date-picker v-model="selectedDate" type="date" :picker-options="pickerOptions" @change="updateData" />
      <el-button type="primary" @click="prevWeek">上一周</el-button>
      <el-button type="primary" @click="nextWeek">下一周</el-button>
      <div ref="chart" style="height: 300px; width: 100%" />
    </el-card>
    <div class="block">
      <el-card class="el-card-define">
        <div>
          <!--        <span>青春是一个短暂的美梦, 当你醒来时, 它早已消失无踪</span>-->
          <!--        <el-divider></el-divider>-->
          <!--        <span>少量的邪恶足以抵消全部高贵的品质, 害得人声名狼藉</span>-->
          <div slot="header">
            <h3>近期违规记录</h3>
          </div>
          <el-table :data="incidentData" :show-header="false">
            <el-table-column prop="category" :formatter="tableFormatter" />
            <el-table-column prop="detail" :show-overflow-tooltip="true" />
            <el-table-column prop="incidentDate" />
          </el-table>
          <el-link type="primary">
            <router-link to="/incident/incident">
              点击查看更多<i class="el-icon-view el-icon--right" />
            </router-link>
          </el-link>
        </div>
      </el-card>
      <el-card class="el-card-define1">
<!--        <el-progress type="circle" :percentage="25" />-->
        <div ref="chart1" style="height: 300px; width: 100%" />
      </el-card>
    </div>
    <el-card>
      <div>
        <span>青春是一个短暂的美梦, 当你醒来时, 它早已消失无踪</span>
        <el-divider />
        <span>少量的邪恶足以抵消全部高贵的品质, 害得人声名狼藉</span>
      </div>
    </el-card>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import moment from 'moment'
import incidentApi from '@/api/incident'

export default {
  data() {
    return {
      searchModel: {
        date: ['', ''],
        pageNo: 1,
        pageSize: 5
      },
      incidentData: [],
      selectedDate: new Date(),
      chartData: {},
      paiData: {},
      pickerOptions: {
        disabledDate(date) {
          return date && date > new Date()
        }
      }
    }
  },
  mounted() {
    this.initChart()
    this.updateData()
    this.renderChart()
    this.renderChart1()
  },
  created() {
    this.getData()
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
    getData() {
      incidentApi.getIncidentList(this.searchModel).then(
        response => {
          this.incidentData = response.data.rows
        }
      )
    },
    initChart() {
      this.chart = echarts.init(this.$refs.chart)
      this.chart1 = echarts.init(this.$refs.chart1)
      this.updateData()
      this.renderChart()
      this.renderChart1()
    },
    async updateData() {
      const startDate = moment(this.selectedDate).startOf('week').format('YYYY-MM-DD')
      const endDate = moment(this.selectedDate).endOf('week').format('YYYY-MM-DD')
      // fetch data from backend with start and end date
      await incidentApi.getEchartsData(startDate, endDate).then(
        response => {
          this.chartData = response.data
        }
      )
      this.renderChart()
      this.renderChart1()
    },
    renderChart() {
      this.chart.setOption({
        animationDelay: 500,
        animationDuration: 2000,
        title: {
          text: '近期工人违规次数'
        },
        toolbox: {
          show: true
        },
        legend: {},
        tooltip: {
          trigger: 'axis',
          textStyle: {
            color: 'black'
          },
          backgroundColor: 'white'
        },
        xAxis: {
          type: 'category',
          // 横坐标，日期时间
          data: this.chartData.dates
        },
        yAxis: {
          type: 'value',
          axisLabel: {
            formatter: '{value} 次'
          }
        },
        series: [{
          // 每天发生的事件的数量
          areaStyle: {},
          name: '无安全帽无工装',
          data: this.chartData.counts1,
          type: 'line',
          smooth: true,
          itemStyle: {
            color: '#5470C6'
          },
          lineStyle: { // 设置线条的style等
            normal: {
              color: '#5470C6'
            }
          }
        },
        {
          // 每天发生的事件的数量
          areaStyle: {},
          name: '仅无安全帽',
          data: this.chartData.counts2,
          type: 'line',
          smooth: true,
          itemStyle: {
            color: '#FF7070'
          },
          lineStyle: { // 设置线条的style等
            normal: {
              color: '#FF7070'
            }
          }
        },
        {
          // 每天发生的事件的数量
          areaStyle: {},
          name: '仅无工装',
          data: this.chartData.counts3,
          type: 'line',
          smooth: true,
          itemStyle: {
            color: '#FFDC60'
          },
          lineStyle: { // 设置线条的style等
            normal: {
              color: '#FFDC60'
            }
          }
        }
        ]
      })
    },
    renderChart1() {
      this.chart1.setOption({
        tooltip: {
          trigger: 'item'
        },
        legend: {
          top: '5%',
          left: 'center'
        },
        series: [
          {
            name: 'Access From',
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 15,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: 40,
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: [
              { value: this.chartData.total[0], name: '无安全帽和工装' },
              { value: this.chartData.total[1], name: '仅无安全帽' },
              { value: this.chartData.total[2], name: '仅无工装' }
            ]
          }
        ]
      })
    },
    prevWeek() {
      this.selectedDate = moment(this.selectedDate).subtract(7, 'days').toDate()
      this.updateData()
    },
    nextWeek() {
      this.selectedDate = moment(this.selectedDate).add(7, 'days').toDate()
      this.updateData()
      // this.getData()
    }
  }
}
</script>

<style scoped>
.card {
  margin-top: 36px;
  width: 100%;
  padding: 0;
}

.el-card-define {
  width: 400px;
  padding: 10px;
  flex: 1;
  margin-right: 10px;
}
.el-card-define1 {
  flex: 1;
  margin-left: 10px;
}
.block {
  display: flex;
}
</style>
