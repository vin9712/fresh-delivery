<template>
  <div class="order-page">
    <el-row :gutter="10">
      <!-- 做单区 -->
      <el-col :span="16">
        <el-card class="order-card">
          <div class="order-header">
            <el-form
              ref="orderFormRef"
              :model="orderForm"
              :rules="rules"
              size="small"
              inline
              label-width="100px"
            >
              <el-form-item label="订单日期" prop="orderDate">
                <el-date-picker
                  v-model="orderForm.orderDate"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                  placeholder="请选择订单日期"
                  clearable
                />
              </el-form-item>
              <el-form-item label="客户" prop="customerId">
                <el-select
                  v-model="orderForm.customerId"
                  placeholder="请选择客户"
                  filterable
                  clearable
                  @change="handleCustomerChange"
                  style="width: 240px"
                >
                  <el-option
                    v-for="c in customerOptions"
                    :key="c.id"
                    :label="c.name"
                    :value="c.id"
                  />
                </el-select>
              </el-form-item>
              <el-form-item label="配送点" prop="pointId">
                <el-select
                  v-model="orderForm.pointId"
                  placeholder="请选择配送点"
                  filterable
                  clearable
                  style="width: 240px"
                >
                  <el-option
                    v-for="p in pointOptions"
                    :key="p.id"
                    :label="p.name"
                    :value="p.id"
                  />
                </el-select>
              </el-form-item>
              <el-form-item label="自动新增">
                <el-switch v-model="isContinueAdd" />
              </el-form-item>
              <el-form-item label="订单备注">
                <el-input v-model="orderForm.remark" placeholder="请输入备注" />
              </el-form-item>
            </el-form>
          </div>

          <div class="order-table-container" :style="{ height: tableHeight }">
            <vxe-table
              border
              resizable
              show-footer
              show-overflow
              keep-source
              ref="xTable"
              size="small"
              class="order-table"
              :height="tableInnerHeight"
              :row-config="{ isHover: true, useKey: true }"
              :mouse-config="{ selected: true }"
              :keyboard-config="{
                isArrow: true,
                isDel: true,
                isEnter: true,
                isTab: true,
                isEdit: true,
                isChecked: true,
              }"
              :footer-method="footerMethod"
              :edit-config="{
                trigger: 'click',
                mode: 'cell',
                beforeEditMethod: checkTableActive,
              }"
              :data="orderDetailList"
              @cell-mouseenter="cellMouseenterEvent"
              @cell-mouseleave="cellMouseleaveEvent"
            >
              <vxe-column field="operate" width="63">
                <template #default="{ row, rowIndex }">
                  <span v-if="currentHoverRow === row" class="drag-btn">
                    <i class="el-icon-s-operation"></i>
                  </span>
                  <span @click="throttledAddRow(rowIndex)">
                    <i class="el-icon-plus"></i>
                  </span>
                  <span @click="handleRemoveRow(row)">
                    <i class="el-icon-minus"></i>
                  </span>
                </template>
              </vxe-column>

              <vxe-column type="seq" width="50" />

              <vxe-column
                field="productName"
                title="商品名称"
                :edit-render="{ name: 'VxeInput', autoselect: true }"
                width="22%"
              >
                <template #edit="{ row: parentRow }">
                  <vxe-pulldown
                    :ref="(el) => setPulldownRef(el, parentRow)"
                    transfer
                  >
                    <template #default>
                      <vxe-input
                        v-model="parentRow.productName"
                        placeholder="请输入商品名称"
                        clearable
                        @keyup="keyupProductNameEvent"
                        @focus="focusProductNameEvent(parentRow)"
                        @blur="blurProductNameEvent(parentRow, $event)"
                        @clear="clearProductNameEvent(parentRow)"
                      />
                    </template>
                    <template #dropdown>
                      <div class="product-dropdown-planel">
                        <vxe-grid
                          border
                          auto-resize
                          height="auto"
                          :row-config="{ isHover: true }"
                          :data="pulldownTableData"
                          :columns="pulldownTableColumn"
                          @cell-click="pulldownCellClickEvent(parentRow, $event)"
                        />
                      </div>
                    </template>
                  </vxe-pulldown>
                </template>
              </vxe-column>

              <vxe-column
                field="productUnit"
                title="单位"
                width="8%"
                :edit-render="{ name: 'VxeInput', autoselect: true }"
              >
                <template #edit="{ row }">
                  <vxe-input
                    v-model="row.productUnit"
                    @change="changedProductUnitEvent(row)"
                  />
                </template>
              </vxe-column>

              <vxe-column
                field="num"
                title="数量"
                :formatter="decimalFormatter('num')"
                :edit-render="{ name: 'VxeInput', autoselect: true }"
              >
                <template #edit="{ row }">
                  <vxe-input v-model="row.num" @change="calcAmount(row)" />
                </template>
              </vxe-column>

              <vxe-column
                field="productPrice"
                title="单价"
                :formatter="decimalFormatter('productPrice')"
                :edit-render="{ name: 'VxeInput', autoselect: true }"
              >
                <template #edit="{ row }">
                  <vxe-input v-model="row.productPrice" @change="calcAmount(row)" />
                </template>
              </vxe-column>

              <vxe-column field="amount" title="金额" />

              <vxe-column
                field="productSpec"
                title="规格"
                :edit-render="{ name: 'VxeInput', autoselect: true }"
              />

              <vxe-column
                field="remark"
                title="备注"
                :edit-render="{ name: 'VxeInput', autoselect: true }"
              />
            </vxe-table>
          </div>

          <div class="order-footer">
            <el-button @click="resetOrderForm">重置</el-button>
            <el-button type="primary" :loading="submitting" @click="submitForm">保存</el-button>
            <el-button @click="close">返回</el-button>
          </div>
        </el-card>
      </el-col>

      <!-- 选单区 -->
      <el-col :span="8">
        <el-card class="recent-order-card">
          <template #header>
            <span>最近订单</span>
          </template>
          <el-form :model="recentQuery" size="small" label-width="70px">
            <el-row :gutter="10">
              <el-col :span="10">
                <el-form-item label="天数">
                  <el-select
                    v-model="recentQuery.days"
                    @change="loadRecentOrders"
                    style="width: 100%"
                  >
                    <el-option label="1天" :value="1" />
                    <el-option label="3天" :value="3" />
                    <el-option label="7天" :value="7" />
                    <el-option label="14天" :value="14" />
                    <el-option label="30天" :value="30" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="14">
                <el-form-item label="客户">
                  <el-select
                    v-model="recentQuery.customerId"
                    @change="loadRecentOrders"
                    filterable
                    clearable
                    style="width: 100%"
                  >
                    <el-option
                      v-for="c in customerOptions"
                      :key="c.id"
                      :label="c.name"
                      :value="c.id"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="20">
                <el-form-item label="搜索词">
                  <el-input
                    v-model="recentQuery.keyword"
                    placeholder="订单编号/备注"
                    clearable
                    @keyup.enter="loadRecentOrders"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="4" style="display: flex; justify-content: flex-end">
                <el-button type="primary" @click="loadRecentOrders" circle title="搜索">
                  <el-icon><Search /></el-icon>
                </el-button>
                <el-button @click="resetRecentQuery" circle title="重置" style="margin-left: 6px">
                  <el-icon><Refresh /></el-icon>
                </el-button>
              </el-col>
            </el-row>
          </el-form>

          <div class="recent-order-table-container">
            <vxe-grid
              border
              auto-resize
              ref="recentOrderTable"
              size="small"
              height="auto"
              :row-config="{ isHover: true, isCurrent: true }"
              :data="recentOrderList"
              :columns="recentTableColumns"
              @current-change="handleRecentOrderRowChange"
            />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import XEUtils from 'xe-utils'
import Sortable from 'sortablejs'
import { getOrderPage, createOrder, getOrderDetail, getOrderItems, updateOrderWithItems } from '../../api/order'
import { getCustomerPage } from '../../api/customer'
import { getCustomerSkuPrices } from '../../api/customer'
import { getDeliveryPointByCustomer } from '../../api/deliveryPoint'

const route = useRoute()
const router = useRouter()

// --- 状态 ---
const orderFormRef = ref(null)
const xTable = ref(null)
const recentOrderTable = ref(null)
const pulldownRefs = ref({})
const submitting = ref(false)

const orderForm = reactive({
  orderDate: '',
  customerId: null,
  pointId: null,
  remark: ''
})

const orderDetailList = ref([])
const originalOrderDetailList = ref([])

const rules = {
  orderDate: [{ required: true, message: '请选择订单日期', trigger: 'blur' }],
  customerId: [{ required: true, message: '请选择客户', trigger: 'blur' }]
}

// --- 客户 / 配送点 / 报价 ---
const customerOptions = ref([])
const pointOptions = ref([])
const skuQuoteDetails = ref([])

// --- 下拉表格 ---
const pulldownTableData = ref([])
const pulldownTableColumn = [
  { field: 'productName', title: '商品名称' },
  { field: 'productUnit', title: '单位' },
  { field: 'price', title: '单价' },
  { field: 'productSpec', title: '规格' },
  { field: 'remark', title: '备注' }
]

// --- 最近订单 ---
const recentQuery = reactive({ customerId: null, keyword: '', days: 3 })
const recentOrderList = ref([])
const recentTableColumns = [
  { field: 'orderNo', title: '订单编号' },
  { field: 'orderDate', title: '日期' },
  { field: 'remark', title: '备注' }
]

// --- 行交互 ---
const currentHoverRow = ref(null)
const rowHeight = 40
const maxRows = 15
const isContinueAdd = ref(true)

// --- 计算属性 ---
const tableHeight = computed(() => {
  const headerFooterHeight = 140
  const calculatedHeight = maxRows * rowHeight + 'px'
  return `min(${calculatedHeight}, calc(100vh - ${headerFooterHeight}px))`
})
const recentTableHeight = computed(() => {
  const headerFooterHeight = 180
  const calculatedHeight = 17 * rowHeight + 'px'
  return `min(${calculatedHeight}, calc(100vh - ${headerFooterHeight}px))`
})
const tableInnerHeight = computed(() => `${maxRows * rowHeight}px`)

// --- 节流 ---
function throttle(ms, fn) {
  let timer = null
  return function(...args) {
    if (timer) return
    timer = setTimeout(() => {
      timer = null
      fn.apply(this, args)
    }, ms)
  }
}
const throttledAddRow = ref(() => {})

// --- 工具 ---
function deepCloneOrderDetails(items) {
  if (!items) return []
  return items.map(i => ({
    ...i,
    _X_ROW_KEY: undefined
  }))
}

// --- 初始化 ---
async function loadCustomers() {
  const res = await getCustomerPage({ pageNum: 1, pageSize: 500 })
  customerOptions.value = res.data.records || []
}

async function loadPoints(customerId) {
  if (!customerId) {
    pointOptions.value = []
    return
  }
  const res = await getDeliveryPointByCustomer(customerId)
  pointOptions.value = res.data || []
}

async function loadSkuQuoteDetails(customerId) {
  if (!customerId) {
    skuQuoteDetails.value = []
    pulldownTableData.value = []
    return
  }
  const res = await getCustomerSkuPrices(customerId)
  const list = res.data || []
  skuQuoteDetails.value = list.map(item => ({
    ...item,
    price: XEUtils.commafy(item.price, { digits: 2 })
  }))
  pulldownTableData.value = skuQuoteDetails.value
}

// --- 订单操作 ---
function getOrderId() {
  const q = route.query.orderId
  return q ? parseInt(q, 10) : null
}

async function initPage() {
  const orderId = getOrderId()
  if (orderId) {
    await loadExistingOrder(orderId)
  } else {
    resetForm()
  }
  await loadRecentOrders()
}

function resetForm() {
  orderForm.orderDate = ''
  orderForm.customerId = null
  orderForm.pointId = null
  orderForm.remark = ''
  orderDetailList.value = []
  originalOrderDetailList.value = []
  handleAddRow()
  loadSkuQuoteDetails(null)
  loadPoints(null)
}

async function loadExistingOrder(orderId) {
  const orderRes = await getOrderDetail(orderId)
  const order = orderRes.data
  orderForm.orderDate = order.orderDate
  orderForm.customerId = order.customerId
  orderForm.pointId = order.pointId
  orderForm.remark = order.remark || ''

  await loadPoints(order.customerId)
  await loadSkuQuoteDetails(order.customerId)

  const itemsRes = await getOrderItems(orderId)
  const items = itemsRes.data || []
  const detailList = items.map(i => ({
    skuId: i.skuId,
    productId: i.productId,
    productName: i.itemName,
    productUnit: i.itemUnit,
    num: XEUtils.commafy(i.quantity, { digits: 2 }),
    productPrice: XEUtils.commafy(i.unitPrice, { digits: 2 }),
    amount: XEUtils.commafy(i.subtotal, { digits: 2 }),
    productSpec: i.itemSpec || '',
    remark: i.remark || ''
  }))
  orderDetailList.value = deepCloneOrderDetails(detailList)
  originalOrderDetailList.value = deepCloneOrderDetails(detailList)
  if (orderDetailList.value.length === 0) {
    handleAddRow()
  }
}

// --- 表格行 ---
function handleAddRow(rowIndex) {
  const newRecord = {
    skuId: null,
    productId: null,
    productName: '',
    productUnit: '斤',
    num: '0.00',
    productPrice: '0.00',
    amount: '0.00',
    productSpec: '',
    remark: ''
  }
  const index = rowIndex == null || rowIndex === -1
    ? orderDetailList.value.length
    : rowIndex + 1
  orderDetailList.value.splice(index, 0, newRecord)
}

async function handleRemoveRow(row) {
  const index = orderDetailList.value.indexOf(row)
  try {
    await ElMessageBox.confirm(`确定删除第 ${index + 1} 行？`, '提示', { type: 'warning' })
  } catch {
    return
  }
  const length = orderDetailList.value.length
  const rowId = row.__id || row._X_ROW_KEY || JSON.stringify(row)
  delete pulldownRefs.value[rowId]
  orderDetailList.value.splice(index, 1)
  if (length <= 1) {
    throttledAddRow.value(-1)
  }
}

// --- 拖拽 ---
let sortableInstance = null
function initDrag() {
  nextTick(() => {
    const tableEl = xTable.value
    if (!tableEl || !tableEl.$el) return
    const tbody = tableEl.$el.querySelector('.body--wrapper>.vxe-table--body tbody')
    if (!tbody) return
    if (sortableInstance) sortableInstance.destroy()
    sortableInstance = Sortable.create(tbody, {
      handle: '.drag-btn',
      onEnd: ({ newIndex, oldIndex }) => {
        const currRow = orderDetailList.value.splice(oldIndex, 1)[0]
        orderDetailList.value.splice(newIndex, 0, currRow)
        const newArr = orderDetailList.value.slice(0)
        orderDetailList.value = []
        nextTick(() => {
          orderDetailList.value = newArr
        })
      }
    })
  })
}

// --- 单元格事件 ---
function cellMouseenterEvent({ row }) {
  if (currentHoverRow.value && currentHoverRow.value === row) return
  currentHoverRow.value = row
}
function cellMouseleaveEvent({ row, rowIndex }) {
  if (
    currentHoverRow.value &&
    currentHoverRow.value === row &&
    rowIndex !== 0 &&
    rowIndex !== orderDetailList.value.length - 1
  ) return
  currentHoverRow.value = null
}
function checkTableActive() {
  return true
}

// --- pulldown ---
function keyupProductNameEvent() {
  // 值由 v-model 同步到 parentRow.productName
  const row = getCurrentEditRow()
  if (!row) return
  initPulldownData(row.productName)
}
function setPulldownRef(el, parentRow) {
  if (!el) return
  const rowId = parentRow.__id || parentRow._X_ROW_KEY || JSON.stringify(parentRow)
  pulldownRefs.value[rowId] = el
}

function focusProductNameEvent(parentRow) {
  initPulldownData(parentRow.productName)
  nextTick(() => {
    const rowId = parentRow.__id || parentRow._X_ROW_KEY || JSON.stringify(parentRow)
    const $pulldown = pulldownRefs.value[rowId]
    if ($pulldown) $pulldown.showPanel()
  })
}
function blurProductNameEvent(parentRow, evt) {
  const value = parentRow.productName
  if (!parentRow) return
  const quoteItem = pulldownTableData.value.find(q => q.productName === value)
  if (quoteItem) {
    parentRow.skuId = quoteItem.skuId
    parentRow.productId = quoteItem.productId
    parentRow.productUnit = quoteItem.productUnit
    parentRow.productSpec = quoteItem.productSpec || ''
    parentRow.remark = quoteItem.remark || ''
  } else {
    parentRow.skuId = null
    parentRow.productId = null
    parentRow.productUnit = '斤'
    parentRow.productSpec = ''
    parentRow.remark = ''
  }
  const isLastRow = orderDetailList.value.indexOf(parentRow) === orderDetailList.value.length - 1
  const isValidRow = !!parentRow.productName && !XEUtils.isEmpty(parentRow.productName)
  if (isLastRow && isValidRow) {
    throttledAddRow.value(-1)
  }
}
function clearProductNameEvent(parentRow) {
  if (!parentRow) return
  parentRow.skuId = null
  parentRow.productId = null
  parentRow.productName = ''
  parentRow.productUnit = ''
  parentRow.productSpec = ''
  parentRow.remark = ''
  parentRow.num = '0.00'
  parentRow.productPrice = '0.00'
  parentRow.amount = '0.00'
  pulldownTableData.value = skuQuoteDetails.value
}
function getCurrentEditRow() {
  const table = xTable.value
  if (!table) return null
  try {
    const activeRecord = table.getActiveRecord()
    return activeRecord ? table.getRecord(activeRecord) : null
  } catch {
    return null
  }
}
function pulldownCellClickEvent(parentRow, evt) {
  const row = evt.row
  if (!row) return
  parentRow.productName = row.productName
  parentRow.productUnit = row.productUnit
  parentRow.productSpec = row.productSpec || ''
  parentRow.productPrice = XEUtils.commafy(safeToNumber(row.price), { digits: 2 })
  parentRow.skuId = row.skuId
  parentRow.productId = row.productId
  calcAmount(parentRow)
  const table = xTable.value
  if (table) {
    try {
      table.setEditCell(parentRow, 'num')
    } catch { /* ignore */ }
  }
  const isLastRow = orderDetailList.value.indexOf(parentRow) === orderDetailList.value.length - 1
  if (isLastRow) {
    throttledAddRow.value(-1)
  }
}
function initPulldownData(value) {
  if (value && value.trim()) {
    const regex = new RegExp(value, 'i')
    pulldownTableData.value = skuQuoteDetails.value.filter(
      row => regex.test(row.productName)
    )
  } else {
    pulldownTableData.value = skuQuoteDetails.value
  }
}
function changedProductUnitEvent(row) {
  const skuQuote = skuQuoteDetails.value.find(
    item => item.productName === row.productName &&
            item.productUnit === row.productUnit &&
            item.productSpec === row.productSpec
  )
  if (skuQuote) {
    row.skuId = skuQuote.skuId
    row.productId = skuQuote.productId
  } else {
    row.skuId = null
    row.productId = null
  }
}

// --- 金额 ---
function safeToNumber(value) {
  const raw = String(value).replace(/,/g, '')
  const n = XEUtils.toNumber(raw)
  return isNaN(n) ? 0 : n
}

function decimalFormatter(key) {
  return ({ row }) => {
    if (!row || typeof row[key] === 'undefined') return '0.00'
    let value = safeToNumber(row[key])
    let formatValue = XEUtils.commafy(value, { digits: 2 })
    if (formatValue <= 0 || formatValue < 0.01) formatValue = '0.00'
    row[key] = formatValue
    return formatValue
  }
}
function calcAmount(row) {
  if (!row) return
  const price = safeToNumber(row.productPrice)
  const num = safeToNumber(row.num)
  row.amount = XEUtils.commafy(price * num, { digits: 2 })
}
function footerMethod({ columns, data }) {
  return [
    columns.map((col, idx) => {
      if (idx === 0) return '合计'
      if (col.property === 'num') {
        return sumNum(data, 'num')
      }
      if (col.property === 'amount') {
        return sumNum(data, 'amount')
      }
      return ''
    })
  ]
}
function sumNum(list, field) {
  let count = 0
  if (list && list.length) {
    list.forEach(item => {
      const value = safeToNumber(item[field])
      count += value
    })
  }
  return count
}

// --- 客户变更 ---
function handleCustomerChange(customerId) {
  orderForm.pointId = null
  orderDetailList.value = []
  handleAddRow()
  loadPoints(customerId)
  loadSkuQuoteDetails(customerId)
}

// --- 最近订单 ---
async function loadRecentOrders() {
  try {
    const res = await getRecentOrders({
      days: recentQuery.days,
      customerId: recentQuery.customerId,
      keyword: recentQuery.keyword
    })
    recentOrderList.value = res.data || []
  } catch {
    recentOrderList.value = []
  }
}
function resetRecentQuery() {
  recentQuery.customerId = null
  recentQuery.keyword = ''
  recentQuery.days = 3
  loadRecentOrders()
}

function deepEqual(a, b) {
  return JSON.stringify(a) === JSON.stringify(b)
}
function checkTableUpdated() {
  const oldList = deepCloneOrderDetails(originalOrderDetailList.value)
  const newList = deepCloneOrderDetails(orderDetailList.value)
  return !deepEqual(oldList, newList)
}

// --- 提交 ---
async function submitForm() {
  try {
    await orderFormRef.value.validate()
  } catch {
    return
  }

  // 过滤有效行
  const validItems = orderDetailList.value.filter(row => {
    const num = safeToNumber(row.num)
    return row.productName && row.productUnit && num > 0
  })

  if (validItems.length === 0) {
    ElMessage.error('订单明细列表不能为空！')
    return
  }

  const items = validItems.map(row => ({
    skuId: row.skuId,
    productId: row.productId,
    itemName: row.productName,
    itemSpec: row.productSpec,
    itemUnit: row.productUnit,
    quantity: safeToNumber(row.num),
    unitPrice: safeToNumber(row.productPrice),
    remark: row.remark
  }))

  const order = {
    orderDate: orderForm.orderDate,
    customerId: orderForm.customerId,
    pointId: orderForm.pointId,
    remark: orderForm.remark
  }

  submitting.value = true
  try {
    const orderId = getOrderId()
    if (orderId) {
      await updateOrderWithItems(orderId, { order, items })
      ElMessage.success('修改成功')
    } else {
      await createOrder({ order, items })
      ElMessage.success('新增成功')
    }
    await initPage()
  } catch {
    // error handled by interceptor
  } finally {
    submitting.value = false
  }
}

function resetOrderForm() {
  const isUpdated = checkTableUpdated()
  if (isUpdated) {
    ElMessageBox.confirm('当前订单明细有改动，是否确认重置？', '提示', { type: 'warning' })
      .then(() => {
        initPage()
      })
      .catch(() => {})
    return
  }
  initPage()
}

function close() {
  const isUpdated = checkTableUpdated()
  if (isUpdated) {
    ElMessageBox.confirm('当前订单明细有改动，是否确认关闭？', '提示', { type: 'warning' })
      .then(() => {
        router.push('/order')
      })
      .catch(() => {})
    return
  }
  router.push('/order')
}

// --- 最近订单行选中 ---
async function handleRecentOrderRowChange(evt) {
  if (!evt || !evt.row) return
  const row = evt.row
  const orderId = row.id || row.orderId
  if (!orderId) return
  const isUpdated = checkTableUpdated()
  if (isUpdated) {
    try {
      await ElMessageBox.confirm('当前订单明细有改动，是否确认切换？', '提示', { type: 'warning' })
    } catch {
      recentOrderTable.value && recentOrderTable.value.clearCurrentRow()
      return
    }
  }
  router.push({ path: '/order', query: { orderId } })
}

// --- mounted ---
onMounted(async () => {
  throttledAddRow.value = throttle(150, handleAddRow)
  await loadCustomers()
  await initPage()
  initDrag()
})
</script>

<style scoped>
.order-page { padding: 16px; }

.order-card,
.recent-order-card {
  height: calc(100vh - 60px);
  display: flex;
  flex-direction: column;
}

.order-header { flex-shrink: 0; margin-bottom: 12px; }

.order-table-container {
  flex: 1;
  overflow-y: auto;
}

.order-footer {
  flex-shrink: 0;
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-top: 12px;
}

.recent-order-table-container {
  flex: 1;
  overflow-y: auto;
}

.drag-btn {
  cursor: move;
  font-size: 12px;
}

.product-dropdown-planel {
  width: 600px;
  height: 300px;
}
</style>