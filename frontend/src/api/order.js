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

// 全量替换明细：删旧插新
export function updateOrderWithItems(id, data) {
  return request({ url: `/order/${id}/with-items`, method: 'put', data })
}

export function deleteOrder(id) {
  return request({ url: `/order/${id}`, method: 'delete' })
}

// 最近订单
export function getRecentOrders(params) {
  return request({ url: '/order/recent', method: 'get', params })
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