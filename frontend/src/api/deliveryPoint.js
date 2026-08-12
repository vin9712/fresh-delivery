import request from '../utils/request'

export function getDeliveryPointPage(params) {
  return request({ url: '/base/delivery-point/page', method: 'get', params })
}

export function getDeliveryPointByCustomer(customerId) {
  return request({ url: `/base/delivery-point/customer/${customerId}`, method: 'get' })
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