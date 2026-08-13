import request from '../utils/request'

export function getDeliveryPointPage(params) {
  return request({ url: '/base/delivery-point/page', method: 'get', params })
}

export function getDeliveryPointDetail(id) {
  return request({ url: `/base/delivery-point/${id}`, method: 'get' })
}

export function getDeliveryPointByCustomer(customerId) {
  return request({ url: `/base/delivery-point/customer/${customerId}`, method: 'get' })
}

// 像素级复刻 — 分页查询(按客户/名称/状态过滤)
export function listDeliveryPoint(params) {
  return request({ url: '/base/delivery-point/list', method: 'get', params })
}

export function createDeliveryPoint(data) {
  return request({ url: '/base/delivery-point', method: 'post', data })
}

export function updateDeliveryPoint(id, data) {
  return request({ url: `/base/delivery-point/${id}`, method: 'put', data })
}

export function deleteDeliveryPoint(id) {
  return request({ url: `/base/delivery-point/${id}`, method: 'delete' })
}