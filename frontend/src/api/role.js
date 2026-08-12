import request from '../utils/request'

export function getRoleList() {
  return request({ url: '/sys/role/list', method: 'get' })
}

export function createRole(data) {
  return request({ url: '/sys/role', method: 'post', data })
}

export function updateRole(id, data) {
  return request({ url: `/sys/role/${id}`, method: 'put', data })
}

export function findRolePermissions(id) {
  return request({ url: `/sys/role/${id}/permissions`, method: 'get' })
}