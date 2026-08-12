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