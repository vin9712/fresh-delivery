import request from '../utils/request'

export function getSkuPage(params) {
  return request({ url: '/base/sku/page', method: 'get', params })
}

export function getSkuDetail(id) {
  return request({ url: `/base/sku/${id}`, method: 'get' })
}

export function createSku(data) {
  return request({ url: '/base/sku', method: 'post', data })
}

export function updateSku(id, data) {
  return request({ url: `/base/sku/${id}`, method: 'put', data })
}

export function deleteSku(id) {
  return request({ url: `/base/sku/${id}`, method: 'delete' })
}