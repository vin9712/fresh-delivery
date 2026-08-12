import request from '../utils/request'

export function getPurchaseOrderPage(params) {
  return request({ url: '/purchase/order/page', method: 'get', params })
}

export function createPurchaseOrder(data) {
  return request({ url: '/purchase/order', method: 'post', data })
}

export function getPurchaseOrderDetail(id) {
  return request({ url: `/purchase/order/${id}`, method: 'get' })
}

export function getPurchaseOrderItems(id) {
  return request({ url: `/purchase/order/${id}/items`, method: 'get' })
}

export function deletePurchaseOrder(id) {
  return request({ url: `/purchase/order/${id}`, method: 'delete' })
}

export function confirmPurchaseOrder(id) {
  return request({ url: `/purchase/order/${id}/confirm`, method: 'put' })
}

export function stockIn(id) {
  return request({ url: `/purchase/order/${id}/stock-in`, method: 'put' })
}

export function aggregateFromOrders(orderIds) {
  return request({ url: '/purchase/order/aggregate', method: 'get', params: { orderIds } })
}