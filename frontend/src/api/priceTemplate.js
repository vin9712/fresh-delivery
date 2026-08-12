import request from '../utils/request'

export function getTemplatePage(params) {
  return request({ url: '/price/template/page', method: 'get', params })
}

export function getTemplateDetail(id) {
  return request({ url: `/price/template/${id}`, method: 'get' })
}

export function getTemplateSkus(id) {
  return request({ url: `/price/template/${id}/skus`, method: 'get' })
}

export function createTemplate(data) {
  return request({ url: '/price/template', method: 'post', data })
}

export function updateTemplate(id, data) {
  return request({ url: `/price/template/${id}`, method: 'put', data })
}

export function saveTemplateSkus(id, data) {
  return request({ url: `/price/template/${id}/skus`, method: 'put', data })
}

export function deleteTemplate(id) {
  return request({ url: `/price/template/${id}`, method: 'delete' })
}