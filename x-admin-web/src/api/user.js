import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

export function getInfo(token) {
  return request({
    url: '/user/info',
    method: 'get',
    params: { token }
  })
}

export function logout() {
  return request({
    url: '/user/logout',
    method: 'post'
  })
}
export function getAllInfo(token) {
  return request({
    url: '/user/getAllInfo',
    method: 'get',
    params: { token }
  })
}

export default {
  getCode(phone) {
    return request({
      url: '/register/sendMsg',
      method: 'post',
      params: {
        'phone': phone
      }
    })
  },
  check(registerForm) {
    return request({
      url: '/register/check',
      method: 'post',
      params: {
        'username': registerForm.username,
        'phone': registerForm.phone,
        'phoneCode': registerForm.phoneCode,
        'password': registerForm.password
      }
    })
  },
  getAllInfo(token) {
    return request({
      url: '/user/getAllInfo',
      method: 'get',
      params: { 'token': token }
    })
  }
}
