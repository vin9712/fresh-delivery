import request from '../utils/request'

export function getAdjustmentPage(params) {
  return request({ url: '/order/adjustment/page', method: 'get', params })
}

export function createAdjustment(data) {
  return request({ url: '/order/adjustment', method: 'post', data })
}

export function deleteAdjustment(id) {
  return request({ url: `/order/adjustment/${id}`, method: 'delete' })
}

export function lookupPrice(customerId, skuId, orderDate) {
  return request({ url: '/order/price/lookup', method: 'get', params: { customerId, skuId, orderDate } })
}