import request from '@/utils/request'

export default {
  getDetectHistory(searchModel) {
    return request({
      url: '/image/getHistory',
      params: {
        beginDate: searchModel.date[0],
        endDate: searchModel.date[1],
        pageNo: searchModel.pageNo,
        pageSize: searchModel.pageSize,
        name: searchModel.name,
        sortType: searchModel.sortType,
        editedType: searchModel.editedType
      },
      method: 'get'
    })
  },
  getDetailByID(id) {
    return request({
      url: `/image/${id}`,
      method: 'get'
    })
  },
  editLabel(formData, url) {
    return request({
      url: '/image/edit',
      method: 'post',
      data: formData,
      params: {
        url: url
      }
    })
  },
  deleteById(id) {
    return request({
      url: `/image/${id}`,
      method: 'delete'
    })
  }
}
