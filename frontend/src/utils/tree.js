/**
 * 将平铺数据转为树形结构
 * @param {Array} data 平铺数据
 * @param {Object} options
 * @returns {Array} 树形数据
 */
export function handleTree(data, options) {
  const config = {
    id: options && options.id || 'id',
    parentId: options && options.parentId || 'parentId',
    children: options && options.children || 'children'
  }

  const deleteChildren = (items) => {
    items.forEach(item => {
      delete item[config.children]
      delete item.hasChildren
    })
  }

  const treeData = []
  const childrenListMap = {}
  const itemMap = {}

  data.forEach(item => {
    const idValue = item[config.id]
    itemMap[idValue] = item
    if (!childrenListMap[idValue]) {
      childrenListMap[idValue] = []
    }
  })

  data.forEach(item => {
    const idValue = item[config.id]
    const parentId = item[config.parentId]
    if (parentId !== 0 && parentId && itemMap[parentId]) {
      if (!childrenListMap[parentId]) {
        childrenListMap[parentId] = []
      }
      childrenListMap[parentId].push(item)
      item[config.children] = []
      item.hasChildren = false
    } else {
      treeData.push(item)
      item[config.children] = []
      item.hasChildren = false
    }
  })

  data.forEach(item => {
    const idValue = item[config.id]
    if (childrenListMap[idValue] && childrenListMap[idValue].length > 0) {
      item[config.children] = childrenListMap[idValue]
      item.hasChildren = true
    }
  })

  return treeData
}