<template>
  <div class="report-center">
    <el-card>
      <div class="filter-bar">
        <el-form inline>
          <el-form-item label="开始日期">
            <el-date-picker v-model="startDate" type="date" format="YYYY-MM-DD" value-format="YYYY-MM-DD" style="width: 160px" />
          </el-form-item>
          <el-form-item label="结束日期">
            <el-date-picker v-model="endDate" type="date" format="YYYY-MM-DD" value-format="YYYY-MM-DD" style="width: 160px" />
          </el-form-item>
          <el-form-item label="客户">
            <el-select v-model="customerId" placeholder="全部" clearable style="width: 140px">
              <el-option v-for="c in customers" :key="c.id" :label="c.name" :value="c.id" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadCurrent">查询</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-tabs v-model="activeTab" class="report-tabs">
        <el-tab-pane label="销售日报" name="daily">
          <div class="tab-bar"><span>销售日报 — 某天所有客户实收汇总</span></div>
          <el-table :data="reports.daily" v-loading="loading" border stripe>
            <el-table-column prop="deliveryDate" label="送货日期" width="120" />
            <el-table-column prop="customerName" label="客户" width="140" />
            <el-table-column prop="orderCount" label="单数" align="right" />
            <el-table-column prop="totalAmount" label="实收总额" align="right" />
          </el-table>
          <div class="export-bar">
            <el-button type="primary" size="small" @click="exportCSV('daily')">导出 CSV</el-button>
          </div>
        </el-tab-pane>

        <el-tab-pane label="月结单" name="monthly">
          <div class="tab-bar"><span>月结单 — 客户当月实收汇总</span></div>
          <el-table :data="reports.monthly" v-loading="loading" border stripe>
            <el-table-column prop="month" label="月份" width="100" />
            <el-table-column prop="customerName" label="客户" width="140" />
            <el-table-column prop="orderCount" label="单数" align="right" />
            <el-table-column prop="totalAmount" label="实收总额" align="right" />
            <el-table-column prop="lossAmount" label="损耗金额" align="right" />
          </el-table>
          <div class="export-bar">
            <el-button type="primary" size="small" @click="exportCSV('monthly')">导出 CSV</el-button>
          </div>
        </el-tab-pane>

        <el-tab-pane label="利润报表" name="profit">
          <div class="tab-bar"><span>利润报表 — 销售总额 − 采购总额</span></div>
          <el-table :data="reports.profit" v-loading="loading" border stripe>
            <el-table-column prop="month" label="月份" width="100" />
            <el-table-column prop="salesTotal" label="销售总额" align="right" />
            <el-table-column prop="purchaseTotal" label="采购总额" align="right" />
            <el-table-column prop="profit" label="利润" align="right">
              <template #default="{ row }">
                <span :style="{ color: (row.profit || 0) >= 0 ? '#67c23a' : '#f56c6c' }">
                  {{ Number(row.profit || 0).toFixed(2) }}
                </span>
              </template>
            </el-table-column>
          </el-table>
          <div class="export-bar">
            <el-button type="primary" size="small" @click="exportCSV('profit')">导出 CSV</el-button>
          </div>
        </el-tab-pane>

        <el-tab-pane label="销售明细" name="detail">
          <div class="tab-bar"><span>销售明细 — 按客户/时间/商品筛选</span></div>
          <el-table :data="reports.detail" v-loading="loading" border stripe>
            <el-table-column prop="orderDate" label="订单日期" width="110" />
            <el-table-column prop="customerName" label="客户" width="130" />
            <el-table-column prop="itemName" label="品名" />
            <el-table-column prop="itemSpec" label="规格" width="90" />
            <el-table-column prop="itemUnit" label="单位" width="70" />
            <el-table-column prop="quantity" label="数量" align="right" />
            <el-table-column prop="unitPrice" label="单价" align="right" />
            <el-table-column prop="subtotal" label="小计" align="right" />
          </el-table>
          <div class="export-bar">
            <el-button type="primary" size="small" @click="exportCSV('detail')">导出 CSV</el-button>
          </div>
        </el-tab-pane>

        <el-tab-pane label="损耗报表" name="loss">
          <div class="tab-bar"><span>损耗报表 — 送货与验收差额</span></div>
          <el-table :data="reports.loss" v-loading="loading" border stripe>
            <el-table-column prop="deliveryDate" label="送货日期" width="110" />
            <el-table-column prop="customerName" label="客户" width="130" />
            <el-table-column prop="itemName" label="品名" />
            <el-table-column prop="itemSpec" label="规格" width="90" />
            <el-table-column prop="deliveredQuantity" label="送货数量" align="right" />
            <el-table-column prop="actualQuantity" label="实收数量" align="right" />
            <el-table-column prop="lossQuantity" label="损耗数量" align="right">
              <template #default="{ row }">
                <span :style="{ color: (row.lossQuantity || 0) < 0 ? '#f56c6c' : '#67c23a' }">
                  {{ Number(row.lossQuantity || 0).toFixed(2) }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="unitPrice" label="单价" align="right" />
          </el-table>
          <div class="export-bar">
            <el-button type="primary" size="small" @click="exportCSV('loss')">导出 CSV</el-button>
          </div>
        </el-tab-pane>

        <el-tab-pane label="采购报表" name="purchase">
          <div class="tab-bar"><span>采购报表 — 采购明细与成本</span></div>
          <el-table :data="reports.purchase" v-loading="loading" border stripe>
            <el-table-column prop="orderDate" label="采购日期" width="110" />
            <el-table-column prop="supplierName" label="供应商" width="130" />
            <el-table-column prop="itemName" label="品名" />
            <el-table-column prop="itemSpec" label="规格" width="90" />
            <el-table-column prop="quantity" label="数量" align="right" />
            <el-table-column prop="unitPrice" label="单价" align="right" />
            <el-table-column prop="subtotal" label="小计" align="right" />
          </el-table>
          <div class="export-bar">
            <el-button type="primary" size="small" @click="exportCSV('purchase')">导出 CSV</el-button>
          </div>
        </el-tab-pane>

        <el-tab-pane label="客户对账单" name="statement">
          <div class="tab-bar"><span>客户对账单 — 月结用，含账期</span></div>
          <el-table :data="reports.statement" v-loading="loading" border stripe>
            <el-table-column prop="month" label="月份" width="100" />
            <el-table-column prop="customerName" label="客户" width="140" />
            <el-table-column prop="settlementCycle" label="账期(天)" align="right" width="90" />
            <el-table-column prop="orderCount" label="单数" align="right" />
            <el-table-column prop="totalAmount" label="实收总额" align="right" />
            <el-table-column prop="lossAmount" label="损耗金额" align="right" />
          </el-table>
          <div class="export-bar">
            <el-button type="primary" size="small" @click="exportCSV('statement')">导出 CSV</el-button>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import dayjs from 'dayjs'
import {
  getSalesDaily, getMonthlyReport, getProfitReport,
  getSalesDetail, getLossReport, getPurchaseReport, getStatement
} from '../../api/report'
import { getCustomerPage } from '../../api/customer'

const activeTab = ref('daily')
const loading = ref(false)
const startDate = ref(dayjs().startOf('month').format('YYYY-MM-DD'))
const endDate = ref(dayjs().endOf('month').format('YYYY-MM-DD'))
const customerId = ref(null)
const customers = ref([])

const reports = reactive({
  daily: [], monthly: [], profit: [],
  detail: [], loss: [], purchase: [], statement: []
})

const loaderMap = {
  daily: () => getSalesDaily({ startDate: startDate.value, endDate: endDate.value }),
  monthly: () => getMonthlyReport({ startDate: startDate.value, endDate: endDate.value }),
  profit: () => getProfitReport({ startDate: startDate.value, endDate: endDate.value }),
  detail: () => getSalesDetail({ startDate: startDate.value, endDate: endDate.value, customerId: customerId.value }),
  loss: () => getLossReport({ startDate: startDate.value, endDate: endDate.value, customerId: customerId.value }),
  purchase: () => getPurchaseReport({ startDate: startDate.value, endDate: endDate.value }),
  statement: () => getStatement({ startDate: startDate.value, endDate: endDate.value, customerId: customerId.value })
}

async function load(tab) {
  loading.value = true
  try {
    const res = await loaderMap[tab]()
    reports[tab] = res.data
  } finally {
    loading.value = false
  }
}

function loadCurrent() {
  load(activeTab.value)
}

watch(activeTab, (t) => load(t))

function exportCSV(tab) {
  const headers = {
    daily: ['送货日期', '客户', '单数', '实收总额'],
    monthly: ['月份', '客户', '单数', '实收总额', '损耗金额'],
    profit: ['月份', '销售总额', '采购总额', '利润'],
    detail: ['订单日期', '客户', '品名', '规格', '单位', '数量', '单价', '小计'],
    loss: ['送货日期', '客户', '品名', '规格', '送货数量', '实收数量', '损耗数量', '单价'],
    purchase: ['采购日期', '供应商', '品名', '规格', '数量', '单价', '小计'],
    statement: ['月份', '客户', '账期(天)', '单数', '实收总额', '损耗金额']
  }
  const data = reports[tab]
  const keys = {
    daily: ['deliveryDate', 'customerName', 'orderCount', 'totalAmount'],
    monthly: ['month', 'customerName', 'orderCount', 'totalAmount', 'lossAmount'],
    profit: ['month', 'salesTotal', 'purchaseTotal', 'profit'],
    detail: ['orderDate', 'customerName', 'itemName', 'itemSpec', 'itemUnit', 'quantity', 'unitPrice', 'subtotal'],
    loss: ['deliveryDate', 'customerName', 'itemName', 'itemSpec', 'deliveredQuantity', 'actualQuantity', 'lossQuantity', 'unitPrice'],
    purchase: ['orderDate', 'supplierName', 'itemName', 'itemSpec', 'quantity', 'unitPrice', 'subtotal'],
    statement: ['month', 'customerName', 'settlementCycle', 'orderCount', 'totalAmount', 'lossAmount']
  }[tab]

  const headerRow = headers[tab].join(',')
  const rows = data.map(r => keys.map(k => {
    const v = r[k]
    return v === null || v === undefined ? '' : String(v).replace(/,/g, '，')
  }).join(','))
  const csv = '﻿' + [headerRow, ...rows].join('\n')
  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8' })
  const a = document.createElement('a')
  a.href = URL.createObjectURL(blob)
  a.download = `${tab}_${startDate.value}_${endDate.value}.csv`
  a.click()
  URL.revokeObjectURL(a.href)
}

async function loadCustomers() {
  const res = await getCustomerPage({ pageNum: 1, pageSize: 500 })
  customers.value = res.data.records
}

onMounted(() => { loadCustomers(); load('daily') })
</script>

<style scoped>
.report-center { padding: 16px }
.filter-bar { margin-bottom: 16px }
.tab-bar { margin-bottom: 12px; color: #666; font-size: 13px }
.export-bar { margin-top: 12px; text-align: right }
</style>