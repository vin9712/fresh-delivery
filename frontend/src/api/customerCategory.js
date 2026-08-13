import request from '../utils/request'

export function getCategoryList() {
  return request({ url: '/base/category/listAll', method: 'get' })
}

export function createCategory(data) {
  return request({ url: '/base/category', method: 'post', data })
}

export function updateCategory(id, data) {
  return request({ url: `/base/category/${id}`, method: 'put', data })
}

export function deleteCategory(id) {
  return request({ url: `/base/category/${id}`, method: 'delete' })
}