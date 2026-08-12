import request from '../utils/request'

export function getLogPage(params) {
  return request({ url: '/sys/log/page', method: 'get', params })
}