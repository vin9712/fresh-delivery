import request from '../utils/request'

export function getCustomerPricePage(params) {
  return request({ url: '/price/customer/page', method: 'get', params })
}

export function createCustomerPrice(data) {
  return request({ url: '/price/customer', method: 'post', data })
}

export function updateCustomerPrice(id, data) {
  return request({ url: `/price/customer/${id}`, method: 'put', data })
}

export function importFromTemplate(templateId, customerId) {
  return request({ url: `/price/customer/import/${templateId}`, method: 'post', params: { customerId } })
}

export function activateCustomerPrice(id) {
  return request({ url: `/price/customer/${id}/activate`, method: 'put' })
}

export function rejectCustomerPrice(id) {
  return request({ url: `/price/customer/${id}/reject`, method: 'put' })
}

export function deleteCustomerPrice(id) {
  return request({ url: `/price/customer/${id}`, method: 'delete' })
}