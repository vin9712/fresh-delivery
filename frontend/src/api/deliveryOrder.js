import request from '../utils/request'

export function getDeliveryOrderPage(params) {
  return request({ url: '/delivery/order/page', method: 'get', params })
}

export function createDeliveryOrder(data) {
  return request({ url: '/delivery/order', method: 'post', data })
}

export function getDeliveryOrderDetail(id) {
  return request({ url: `/delivery/order/${id}`, method: 'get' })
}

export function getDeliveryOrderItems(id) {
  return request({ url: `/delivery/order/${id}/items`, method: 'get' })
}

export function deleteDeliveryOrder(id) {
  return request({ url: `/delivery/order/${id}`, method: 'delete' })
}

export function markDelivered(id) {
  return request({ url: `/delivery/order/${id}/deliver`, method: 'put' })
}

export function markAccepted(id) {
  return request({ url: `/delivery/order/${id}/accept`, method: 'put' })
}

export function printDelivery(id) {
  return request({ url: `/delivery/order/${id}/print`, method: 'put' })
}