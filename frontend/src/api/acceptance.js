import request from '../utils/request'

export function getAcceptancePage(params) {
  return request({ url: '/delivery/acceptance/page', method: 'get', params })
}

export function createAcceptance(data) {
  return request({ url: '/delivery/acceptance', method: 'post', data })
}

export function getAcceptanceDetail(id) {
  return request({ url: `/delivery/acceptance/${id}`, method: 'get' })
}

export function getAcceptanceItems(id) {
  return request({ url: `/delivery/acceptance/${id}/items`, method: 'get' })
}