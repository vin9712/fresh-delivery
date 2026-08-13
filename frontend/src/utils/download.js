import request from './request'

/**
 * 下载文件
 * @param {String} url 下载文件接口地址
 * @param {Object} params 下载参数
 * @param {String} fileName 文件名
 */
export function download(url, params, fileName) {
  request({
    url: url,
    method: 'get',
    params: params,
    responseType: 'blob'
  }).then(res => {
    const blob = new Blob([res.data])
    const link = document.createElement('a')
    link.href = window.URL.createObjectURL(blob)
    link.download = fileName || 'download'
    link.click()
    window.URL.revokeObjectURL(link.href)
  }).catch(error => {
    console.error('下载失败', error)
  })
}