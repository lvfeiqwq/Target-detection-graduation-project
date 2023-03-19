import request from '@/utils/request'

export default {
  getIncidentList(searchModel) {
    return request({
      url: '/incident/list',
      method: 'get',
      params: {
        beginDate: searchModel.date[0],
        endDate: searchModel.date[1],
        pageNo: searchModel.pageNo,
        pageSize: searchModel.pageSize,
        category: searchModel.category,
        place: searchModel.place
      }
    })
  },
  saveIncident(formData, id) {
    if (id == null) {
      return this.addIncident(formData)
    } else {
      return this.updateIncident(formData)
    }
  },
  addIncident(formData) {
    return request({
      url: '/incident/addIncident',
      method: 'post',
      data: formData
    })
  },
  updateIncident(formData) {
    return request({
      url: '/incident/updateIncident',
      method: 'post',
      data: formData
    })
  },
  getIncidentById(id) {
    return request({
      url: `/incident/${id}`,
      method: 'get'
    })
  },
  deleteIncidentById(id) {
    return request({
      url: `/incident/${id}`,
      method: 'delete'
    })
  }
}
