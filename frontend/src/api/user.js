import request from '../utils/request'

export function getUserPage(params) {
  return request({ url: '/sys/user/page', method: 'get', params })
}

export function getUserDetail(id) {
  return request({ url: `/sys/user/${id}`, method: 'get' })
}

export function createUser(data) {
  return request({ url: '/sys/user', method: 'post', data })
}

export function updateUser(id, data) {
  return request({ url: `/sys/user/${id}`, method: 'put', data })
}

export function deleteUser(id) {
  return request({ url: `/sys/user/${id}`, method: 'delete' })
}