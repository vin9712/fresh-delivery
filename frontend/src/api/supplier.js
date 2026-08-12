import request from '../utils/request'

export function getSupplierPage(params) {
  return request({ url: '/purchase/supplier/page', method: 'get', params })
}

export function createSupplier(data) {
  return request({ url: '/purchase/supplier', method: 'post', data })
}

export function updateSupplier(id, data) {
  return request({ url: `/purchase/supplier/${id}`, method: 'put', data })
}

export function deleteSupplier(id) {
  return request({ url: `/purchase/supplier/${id}`, method: 'delete' })
}