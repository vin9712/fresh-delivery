import request from '../utils/request'

export function getOrderPage(params) {
  return request({ url: '/order/page', method: 'get', params })
}

export function createOrder(data) {
  return request({ url: '/order', method: 'post', data })
}

export function getOrderDetail(id) {
  return request({ url: `/order/${id}`, method: 'get' })
}

export function getOrderItems(id) {
  return request({ url: `/order/${id}/items`, method: 'get' })
}

export function updateOrder(id, data) {
  return request({ url: `/order/${id}`, method: 'put', data })
}

export function deleteOrder(id) {
  return request({ url: `/order/${id}`, method: 'delete' })
}

export function confirmOrder(id) {
  return request({ url: `/order/${id}/confirm`, method: 'put' })
}

export function deliverOrder(id) {
  return request({ url: `/order/${id}/deliver`, method: 'put' })
}

export function acceptOrder(id) {
  return request({ url: `/order/${id}/accept`, method: 'put' })
}

export function settleOrder(id) {
  return request({ url: `/order/${id}/settle`, method: 'put' })
}