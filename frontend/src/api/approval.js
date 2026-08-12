import request from '../utils/request'

export function getApprovalPage(params) {
  return request({ url: '/sys/approval/page', method: 'get', params })
}

export function getPendingApprovals(params) {
  return request({ url: '/sys/approval/pending', method: 'get', params })
}

export function submitApproval(data) {
  return request({ url: '/sys/approval/submit', method: 'post', data })
}

export function handleApproval(id, data) {
  return request({ url: `/sys/approval/${id}`, method: 'put', data })
}