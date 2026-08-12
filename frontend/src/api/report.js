import request from '../utils/request'

export function getSalesDaily(params) {
  return request({ url: '/report/daily', method: 'get', params })
}

export function getMonthlyReport(params) {
  return request({ url: '/report/monthly', method: 'get', params })
}

export function getProfitReport(params) {
  return request({ url: '/report/profit', method: 'get', params })
}

export function getSalesDetail(params) {
  return request({ url: '/report/detail', method: 'get', params })
}

export function getLossReport(params) {
  return request({ url: '/report/loss', method: 'get', params })
}

export function getPurchaseReport(params) {
  return request({ url: '/report/purchase', method: 'get', params })
}

export function getStatement(params) {
  return request({ url: '/report/statement', method: 'get', params })
}