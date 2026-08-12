import request from '../utils/request'

export function getProductPage(params) {
  return request({ url: '/base/product/page', method: 'get', params })
}

export function getProductDetail(id) {
  return request({ url: `/base/product/${id}`, method: 'get' })
}

export function createProduct(data) {
  return request({ url: '/base/product', method: 'post', data })
}

export function updateProduct(id, data) {
  return request({ url: `/base/product/${id}`, method: 'put', data })
}

export function deleteProduct(id) {
  return request({ url: `/base/product/${id}`, method: 'delete' })
}

export function getProductAliases(productId) {
  return request({ url: `/base/alias/product/${productId}`, method: 'get' })
}

export function createAlias(data) {
  return request({ url: '/base/alias', method: 'post', data })
}

export function updateAlias(id, data) {
  return request({ url: `/base/alias/${id}`, method: 'put', data })
}

export function deleteAlias(id) {
  return request({ url: `/base/alias/${id}`, method: 'delete' })
}