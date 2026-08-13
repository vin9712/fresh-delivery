import request from '../utils/request'

export function getCustomerPage(params) {
  return request({ url: '/base/customer/page', method: 'get', params })
}

export function getCustomerDetail(id) {
  return request({ url: `/base/customer/${id}`, method: 'get' })
}

export function createCustomer(data) {
  return request({ url: '/base/customer', method: 'post', data })
}

export function updateCustomer(id, data) {
  return request({ url: `/base/customer/${id}`, method: 'put', data })
}

export function deleteCustomer(id) {
  return request({ url: `/base/customer/${id}`, method: 'delete' })
}

// 像素级复刻 — 分页查询(按名称/状态过滤)
export function listCustomer(params) {
  return request({ url: '/base/customer/pageFilter', method: 'get', params })
}

// 全部客户列表(用于配送点下拉)
export function getAllCustomers() {
  return request({ url: '/base/customer/list', method: 'get' })
}

// 按客户获取 SKU 报价列表
export function getCustomerSkuPrices(customerId) {
  return request({ url: `/price/customer/sku-prices/${customerId}`, method: 'get' })
}