import request1 from '@/utils/request1'

export default {
  postImage(formData) {
    return request1({
      url: '/detect',
      data: formData,
      method: 'post'
    })
  }
}
