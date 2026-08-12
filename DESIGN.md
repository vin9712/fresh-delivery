# 生鲜配送管理系统 — 设计文档

> 版本：v1.0  设计日期：2026-08-12
> 技术栈：Vue 3 + Element Plus + VReport + Print.js + Spring Boot + MySQL 8

---

## 目录

1. [需求回顾](#1-需求回顾)
2. [系统架构](#2-系统架构)
3. [数据库设计](#3-数据库设计)
4. [核心业务流转](#4-核心业务流转)
5. [报表模块](#5-报表模块)
6. [权限与日志](#6-权限与日志)
7. [部署建议](#7-部署建议)

---

## 1. 需求回顾

### 1.1 角色

| 角色 | 职责 |
|------|------|
| **文员**（2人） | 代客下单、录入验收单、录入采购单、维护报价、打印送货单、模板设计 |
| **审批人** | 审批报价单、查看报表、打印送货单、用户管理 |

> 配送员、采购员暂不实现。

### 1.2 核心业务流程

```
D天 下午~11点
    ├── 文员持续代客下单
    ├── 加退单/换货在送货时或验收后发生，归属D天订单
    └── 11点：订单全部入完 → 生成采购单

D天 11点后
    └── 供应商陆续送货到公司

D+1天 清晨
    ├── 按客户一次性打印送货单
    └── 配送员出单送货

D+1天 配送中
    ├── 加单/退单/换货 → 原单标记 + 独立记录
    ├── 大部分加退单单独配送，顺手带去的在原单标记
    └── 每送完几个客户 → 文员录入验收单

每日文员录入
    ├── 验收单（结算依据）
    └── 采购单（含补货，成本核算）
```

### 1.3 核心设计原则

| 原则 | 说明 |
|------|------|
| **订单是入口** | 送货单和采购单都由订单生成，独立管理、不绑定数据 |
| **验收为结算** | 最终结算以验收单实收数据为准 |
| **报价有层级** | 模板 → 客户 → 配送点，逐层覆盖 |
| **非标兼容** | 客户维度临时商品，下单文本录入，可转正式SKU |
| **痕迹保留** | 加退单/换货在原文档留痕 + 独立记录 |
| **数据溯源** | 5年历史，支持按日期/客户/商品追溯 |

---

## 2. 系统架构

### 2.1 模块划分

```
┌──────────┬──────────┬──────────┬──────────┬──────────┬──────────┐
│ 基础数据  │ 报价管理  │ 订单管理  │ 采购管理  │ 配送管理  │ 报表中心  │
├──────────┼──────────┼──────────┼──────────┼──────────┼──────────┤
│商品管理  │报价方案  │代客下单  │采购单    │送货单    │送货单模板│
│SKU管理   │报价审批  │非标品    │供应商    │验收单    │销售报表  │
│客户分类  │取价规则  │订单查询  │成本核算  │打印      │利润报表  │
│客户管理  │客户报价  │订单追溯  │默认供应商│          │月结单    │
│配送点    │配送点报价│加退单   │          │          │损耗报表  │
│别名映射  │          │临时商品  │          │          │对账单    │
├──────────┴──────────┴──────────┴──────────┴──────────┴──────────┤
│ 系统管理  • 用户管理  • 角色权限  • 操作日志  • 审批流  • 模板中心 │
└──────────────────────────────────────────────────────────────────┘
```

### 2.2 技术架构

```
┌─────────────────────────────────────────────────────────────────┐
│  前端 (Vue 3 + Element Plus + VReport + Print.js)               │
│  nginx (静态资源 + 反向代理)                                      │
├─────────────────────────────────────────────────────────────────┤
│  后端 (Spring Boot + MyBatis)                                    │
│  • REST API  • Spring Security + JWT  • AOP操作日志              │
├─────────────────────────────────────────────────────────────────┤
│  数据库 (MySQL 8)           Redis (可选，缓存报价数据)            │
└─────────────────────────────────────────────────────────────────┘
```

---

## 3. 数据库设计

### 3.1 表关系ER图（逻辑）

```
product ──── product_alias (1:N, 全局别名)
product ──── sku (1:N)
                  │
                  ├── customer_sku_mapping (客户维度: 品名+别名+缩写)
                  │
                  ├── price_template_sku (报价方案明细)
                  │
                  ├── customer_price (客户报价)
                  │
                  ├── delivery_point_price (配送点报价)
                  │
                  ├── order_item (订单明细, sku可为NULL)
                  ├── purchase_item (采购明细)
                  │
customer ──── delivery_point (1:N)
customer ──── customer_price
customer ──── temp_product (临时商品, 客户维度)
customer ──── customer_sku_mapping

order ──── order_item (1:N)
order ──── order_adjustment (1:N, 加退单/换货独立记录)

delivery_order ──── delivery_item (1:N)
acceptance ──── acceptance_item (1:N)
```

---

### 3.2 系统管理表

#### `sys_user` 用户表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 主键 |
| username | VARCHAR(50) | 登录名，唯一 |
| password | VARCHAR(100) | 加密密码 |
| real_name | VARCHAR(50) | 真实姓名 |
| role_id | BIGINT | 关联角色 |
| status | TINYINT | 0禁用 1启用 |
| created_at | DATETIME | |

#### `sys_role` 角色表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| role_name | VARCHAR(100) | 角色名称 |
| role_key | VARCHAR(50) | 角色标识 |
| description | VARCHAR(200) | |

#### `sys_permission` 权限表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| name | VARCHAR(100) | 权限名称 |
| permission_key | VARCHAR(50) | 权限标识，格式 `module:action` |
| module | VARCHAR(50) | 所属模块 |
| description | VARCHAR(200) | |

#### `sys_user_role` / `sys_role_permission` 多对多关联表

#### `sys_operation_log` 操作日志表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| user_id | BIGINT | 操作人ID |
| user_name | VARCHAR(50) | 操作人（冗余） |
| module | VARCHAR(50) | 模块标识 |
| action | VARCHAR(50) | 操作类型（新增/修改/删除/导入/打印） |
| target_type | VARCHAR(50) | 目标类型 |
| target_id | BIGINT | 目标ID |
| old_value | JSON | 修改前 |
| new_value | JSON | 修改后 |
| operate_time | DATETIME | |
| ip_address | VARCHAR(50) | |

#### `sys_approval` 审批流表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| biz_type | VARCHAR(50) | 业务类型（PRICE） |
| biz_id | BIGINT | 关联报价ID |
| submit_user | VARCHAR(50) | 提交人 |
| approver | VARCHAR(50) | 审批人 |
| status | TINYINT | 0待审批 1已批准 2已拒绝 |
| remark | VARCHAR(500) | 审批意见 |
| submit_time / approve_time | DATETIME | |

---

### 3.3 商品与SKU表

#### `product` 商品表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| name | VARCHAR(100) | 标准品名 |
| en_short | VARCHAR(20) | 英文缩写，唯一，快速定位 |
| status | TINYINT | 0停用 1启用 |
| created_at / updated_at | DATETIME | |

#### `product_alias` 全局别名表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| product_id | BIGINT | 归属商品 |
| alias | VARCHAR(100) | 别名 |
| en_short | VARCHAR(20) | 别名英文缩写 |

#### `sku` SKU表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| product_id | BIGINT | 归属商品 |
| spec_name | VARCHAR(100) | 规格名称（如"500g/份"） |
| spec_value | VARCHAR(50) | 规格值 |
| unit | VARCHAR(20) | 单位 |
| status | TINYINT | 0停用 1启用 |
| created_at / updated_at | DATETIME | |

---

### 3.4 客户与配送点表

#### `customer_category` 客户分类表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| name | VARCHAR(50) | 分类名 |
| status | TINYINT | |

#### `customer` 客户表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| category_id | BIGINT | 客户分类 |
| name | VARCHAR(100) | 客户名称 |
| contact_person | VARCHAR(50) | 联系人 |
| phone | VARCHAR(20) | 联系电话 |
| address | VARCHAR(200) | 默认地址 |
| settlement_cycle | TINYINT | 结算周期 1周 2月 |
| status | TINYINT | 0停用 1启用 |
| created_at / updated_at | DATETIME | |

#### `delivery_point` 配送点表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| customer_id | BIGINT | 归属客户（多对一） |
| name | VARCHAR(100) | 配送点名称 |
| address | VARCHAR(200) | 配送地址 |
| contact_person | VARCHAR(50) | |
| phone | VARCHAR(20) | |
| status | TINYINT | |
| created_at | DATETIME | |

#### `customer_sku_mapping` 客户SKU映射表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| customer_id | BIGINT | 归属客户 |
| sku_id | BIGINT | 标准SKU |
| customer_name | VARCHAR(100) | 客户维度品名 |
| customer_alias | VARCHAR(200) | 客户维度别名 |
| en_short | VARCHAR(20) | 客户维度英文缩写 |
| status | TINYINT | |

---

### 3.5 报价表

#### `price_template` 报价方案（可复用模板）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| name | VARCHAR(100) | 方案名称 |
| description | VARCHAR(500) | |
| status | TINYINT | |
| created_at | DATETIME | |

#### `price_template_sku` 报价方案明细
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| template_id | BIGINT | |
| sku_id | BIGINT | |
| price | DECIMAL(10,2) | |
| start_date / end_date | DATE | 有效期 |

#### `customer_price` 客户报价
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| customer_id | BIGINT | |
| sku_id | BIGINT | |
| price | DECIMAL(10,2) | |
| start_date / end_date | DATE | |
| source_type | TINYINT | 0手动 1模板导入 |
| source_id | BIGINT | 来源模板ID |
| status | TINYINT | 0草稿 1生效 2已拒绝 |

#### `delivery_point_price` 配送点报价
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| point_id | BIGINT | |
| sku_id | BIGINT | |
| price | DECIMAL(10,2) | |
| start_date / end_date | DATE | |
| status | TINYINT | |

#### 取价服务逻辑（后端实现）

```
getPrice(customerId, pointId, skuId, orderDate):
    1. 查 delivery_point_price（pointId+skuId）→ 区间内优先，过期取最新
    2. 若空 → 查 customer_price（customerId+skuId）→ 同上
    3. 若空 → 查 price_template_sku（客户关联的模板）→ 同上
    4. 若仍空 → 返回 null（文员手动填价）
```

---

### 3.6 订单表

#### `order` 订单总表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| order_no | VARCHAR(30) | 订单编号，格式 `XD202608120001` |
| order_date | DATE | 订单日期（D天） |
| customer_id | BIGINT | |
| point_id | BIGINT | |
| status | TINYINT | 0草稿 1已确认 2已送货 3已验收 4已结算 |
| remark | VARCHAR(500) | |
| created_by / created_at | VARCHAR(50) / DATETIME | |

#### `order_item` 订单明细
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| order_id | BIGINT | |
| sku_id | BIGINT | 标准SKU（可为NULL，非标品） |
| product_id | BIGINT | 商品（可为临时商品ID） |
| item_name | VARCHAR(200) | 下单时实际品名 |
| item_spec | VARCHAR(100) | 实际规格 |
| item_unit | VARCHAR(20) | 实际单位 |
| quantity | DECIMAL(10,2) | 下单数量 |
| unit_price | DECIMAL(10,2) | 下单单价 |
| subtotal | DECIMAL(12,2) | 小计 |
| item_status | TINYINT | 0正常 1已退单 2已换货 3部分退单 |
| remark | VARCHAR(500) | 备注 |
| created_at | DATETIME | |

> **关键设计**：item_name/spec/unit 直接保存下单时数据，不依赖SKU主数据，兼容非标品。

#### `temp_product` 临时商品表（客户维度）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| customer_id | BIGINT | 归属客户 |
| name | VARCHAR(200) | 临时品名 |
| spec | VARCHAR(100) | |
| unit | VARCHAR(20) | |
| price | DECIMAL(10,2) | 参考单价 |
| status | TINYINT | 0临时 1已转正式SKU |
| formal_sku_id | BIGINT | 转正后关联SKU |
| created_at | DATETIME | |

#### `order_adjustment` 加单/退单/换货记录表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| order_id | BIGINT | 归属订单 |
| origin_item_id | BIGINT | 原订单行ID |
| adjust_type | TINYINT | 0加单 1退单 2换货 |
| adjust_date | DATE | 操作日期 |
| order_date | DATE | 归属订单日期（D天） |
| item_name | VARCHAR(200) | |
| item_spec / item_unit | VARCHAR(100) / VARCHAR(20) | |
| sku_id | BIGINT | 可为NULL |
| quantity | DECIMAL(10,2) | |
| unit_price | DECIMAL(10,2) | |
| remark | VARCHAR(500) | 原因/备注 |
| created_by / created_at | VARCHAR(50) / DATETIME | |

---

### 3.7 采购表

#### `supplier` 供应商表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| name | VARCHAR(100) | |
| contact_person | VARCHAR(50) | |
| phone | VARCHAR(20) | |
| address | VARCHAR(200) | |
| is_default | TINYINT | 0否 1默认（自采） |
| status | TINYINT | |

#### `product_supplier` 商品-供应商关联
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| product_id / sku_id | BIGINT / BIGINT | |
| supplier_id | BIGINT | |
| is_primary | TINYINT | 主供应商 |

#### `purchase_order` 采购单
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| order_no | VARCHAR(30) | 采购单号，格式 `PC20260812001` |
| order_date | DATE | |
| supplier_id | BIGINT | |
| source_type | TINYINT | 0手动 1自动生成 |
| source_order_ids | JSON | 来源订单ID列表 |
| total_amount | DECIMAL(12,2) | 采购总额 |
| status | TINYINT | 0草稿 1已确认 2已入库 |
| created_by / created_at | VARCHAR(50) / DATETIME | |

#### `purchase_item` 采购明细
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| order_id | BIGINT | |
| sku_id | BIGINT | |
| item_name | VARCHAR(200) | |
| item_spec / item_unit | VARCHAR(100) / VARCHAR(20) | |
| quantity | DECIMAL(10,2) | |
| unit_price | DECIMAL(10,2) | 采购成本价 |
| subtotal | DECIMAL(12,2) | |
| remark | VARCHAR(500) | |
| created_at | DATETIME | |

---

### 3.8 配送表

#### `delivery_order` 送货单
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| order_no | VARCHAR(30) | 送货单号，格式 `HS20260812001` |
| delivery_date | DATE | 送货日期（D+1天） |
| order_date | DATE | 归属订单日期（D天） |
| customer_id | BIGINT | |
| point_id | BIGINT | |
| total_quantity | DECIMAL(10,2) | 总件数 |
| total_amount | DECIMAL(12,2) | 送货金额（非实收） |
| template_id | BIGINT | 使用的模板 |
| print_count | INT | 打印次数 |
| status | TINYINT | 0已打印 1已送达 2已验收 |
| created_by / created_at | VARCHAR(50) / DATETIME | |

#### `delivery_item` 送货单明细
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| delivery_order_id | BIGINT | |
| origin_order_id | BIGINT | 来源订单ID（追溯用） |
| origin_item_id | BIGINT | 来源订单行ID |
| item_name | VARCHAR(200) | 客户维度品名 |
| item_spec / item_unit | VARCHAR(100) / VARCHAR(20) | |
| quantity | DECIMAL(10,2) | 送货数量 |
| unit_price | DECIMAL(10,2) | |
| subtotal | DECIMAL(12,2) | |
| adjust_status | TINYINT | 0正常 1部分退 2全部退 3换货 4加单 |
| remark | VARCHAR(200) | |

#### `acceptance` 验收单
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| order_no | VARCHAR(30) | 验收单号，格式 `YS20260812001` |
| delivery_order_id | BIGINT | 对应送货单（一单一验） |
| delivery_date | DATE | |
| customer_id / point_id | BIGINT / BIGINT | |
| total_amount | DECIMAL(12,2) | **实收总金额（结算依据）** |
| total_loss_amount | DECIMAL(12,2) | 损耗总金额 |
| created_by / created_at | VARCHAR(50) / DATETIME | |

#### `acceptance_item` 验收单明细
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| acceptance_id | BIGINT | |
| delivery_item_id | BIGINT | 对应送货行 |
| item_name | VARCHAR(200) | |
| item_spec / item_unit | VARCHAR(100) / VARCHAR(20) | |
| delivered_quantity | DECIMAL(10,2) | 送货数量 |
| actual_quantity | DECIMAL(10,2) | **实收数量** |
| loss_quantity | DECIMAL(10,2) | 损耗数量（可正可负） |
| unit_price | DECIMAL(10,2) | |
| actual_amount | DECIMAL(12,2) | 实收金额 |
| remark | VARCHAR(500) | |

---

### 3.9 报表表

#### `delivery_template` 送货单模板
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| name | VARCHAR(100) | 模板名称 |
| paper_size | VARCHAR(20) | A4/A5/自定义 |
| orientation | VARCHAR(10) | portrait/landscape |
| copies | INT | 联数 |
| template_json | LONGTEXT | VReport模板JSON |
| is_default | TINYINT | 0否 1默认（A4默认） |
| status | TINYINT | |
| created_by / created_at | VARCHAR(50) / DATETIME | |

#### `template_customer` 模板-客户绑定
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| template_id | BIGINT | |
| customer_id | BIGINT | 客户（NULL=全局默认） |
| priority | INT | 优先级 |

---

### 3.10 索引建议

| 表 | 索引 |
|------|------|
| `product` | (name), (en_short) |
| `product_alias` | (alias), (en_short) |
| `customer_sku_mapping` | (customer_id), (sku_id), (customer_name), (en_short) |
| `customer_price` | (customer_id, sku_id), (start_date, end_date) |
| `delivery_point_price` | (point_id, sku_id) |
| `order` | (order_date), (customer_id), (status) |
| `order_item` | (order_id) |
| `order_adjustment` | (order_id), (order_date) |
| `purchase_order` | (order_date), (supplier_id) |
| `delivery_order` | (delivery_date), (order_date), (customer_id) |
| `acceptance` | (delivery_date), (customer_id) |
| `sys_operation_log` | (operate_time), (user_id) |

---

## 4. 核心业务流转

### 4.1 订单状态流转

```
DRAFT → CONFIRMED → DELIVERED → ACCEPTED → SETTLED
(草稿)   (已确认)    (已送货)    (已验收)    (已结算)
```

- 采购单独立，不在此流内
- 加退单/换货不改变订单主状态，独立记录
- SETTLED 由文员月度结账时手动确认

### 4.2 取价流程（下单时）

```
用户选择客户/配送点/SKU
    ↓
后端 getPrice(customerId, pointId, skuId, orderDate)
    ↓
返回价格（有报价→自动填充；无报价→文员手动填）
    ↓
文员确认/调整价格 → 提交订单
    ↓
下单行保存 item_name/spec/unit/price（快照，不依赖后续主数据变更）
```

### 4.3 采购单生成

```
选择当天订单 → 按商品汇总需求量
    ↓
生成采购单草稿（PC单号）
    ↓
选择供应商（默认自采/指定供应商）
    ↓
确认 → 状态=已确认
```

> 也可手动创建采购单（补货场景）。

### 4.4 送货单生成

```
D+1天清晨 → 选择日期
    ↓
按客户/配送点分组 → 读取D天该客户的订单数据
    ↓
读取客户绑定的送货单模板（无则用全局默认）
    ↓
VReport 渲染 HTML → Print.js 打印
    ↓
生成 delivery_order + delivery_item
```

### 4.5 验收单录入

```
选择送货单 → 录入每行实收数量
    ↓
自动计算损耗 = 实收 − 送货
    ↓
自动计算实收金额 = 实收数量 × 单价
    ↓
提交 → 生成 acceptance + acceptance_item
    ↓
更新订单状态 → ACCEPTED
```

---

## 5. 报表模块

### 5.1 送货单模板

- **默认模板**：A4 纸，1联，标准格式
- **客户定制**：通过 `template_customer` 绑定
- **设计器**：VReport，支持条件显示、循环、公式
- **存储**：`template_json` 存 LONGTEXT

### 5.2 VReport 模板JSON结构

```json
{
  "options": { "paperSize": "A4", "orientation": "portrait" },
  "components": [
    {
      "type": "text",
      "text": "送货单",
      "style": { "fontSize": "24px", "textAlign": "center" },
      "position": { "x": 0, "y": 10, "width": 800, "height": 40 }
    },
    {
      "type": "table",
      "columns": [
        { "key": "itemName", "title": "品名", "width": 200 },
        { "key": "quantity", "title": "数量", "width": 80 },
        { "key": "unit", "title": "单位", "width": 60 },
        { "key": "unitPrice", "title": "单价", "width": 80 },
        { "key": "subtotal", "title": "小计", "width": 100 }
      ],
      "dataSource": "items",
      "condition": { "show": "totalAmount > 0" }
    }
  ]
}
```

### 5.3 报表清单

| 报表 | 说明 | 数据来源 |
|------|------|----------|
| 销售日报 | 某天所有客户实收汇总 | acceptance |
| 月结单 | 客户当月实收明细 | acceptance + order_item |
| 利润报表 | 销售总额 − 采购总额 | acceptance − purchase_order |
| 销售明细 | 按客户/时间/商品筛选 | order_item |
| 损耗报表 | 送货与验收差额 | delivery_item − acceptance_item |
| 采购报表 | 采购明细与成本 | purchase_order + purchase_item |
| 客户对账单 | 月结用，含账期 | customer + acceptance |

### 5.4 模板中心（批量导入）

| 模块 | 导入模板 |
|------|----------|
| 商品管理 | 品名、英文缩写、状态 |
| 别名管理 | 商品ID、别名、英文缩写 |
| SKU管理 | 商品ID、规格、单位 |
| 客户管理 | 分类、名称、联系人、电话、地址 |
| 配送点管理 | 客户ID、配送点名称、地址、联系人 |
| 报价管理 | 客户ID、SKU ID、价格、起止日期 |
| 供应商管理 | 名称、联系人、电话、地址 |
| 客户SKU映射 | 客户ID、SKU ID、客户品名、别名、英文缩写 |

---

## 6. 权限与日志

### 6.1 角色权限矩阵

| 功能 | 文员 | 审批人 |
|------|:----:|:------:|
| 商品/SKU管理 | ✅增删改查 | ✅查看 |
| 客户/配送点管理 | ✅增删改查 | ✅查看 |
| 报价方案管理 | ✅增删改查 | ✅查看 |
| 报价审批 | ❌ | ✅审批 |
| 代客下单 | ✅ | ❌ |
| 采购单录入 | ✅ | ✅查看 |
| 送货单打印 | ✅ | ✅打印 |
| 验收单录入 | ✅ | ✅查看 |
| 报表中心 | ✅查看/导出 | ✅查看/导出 |
| 模板中心导入 | ✅ | ❌ |
| 送货单模板设计 | ✅ | ❌ |
| 用户管理 | ❌ | ✅ |
| 操作日志 | ✅查看 | ✅查看 |

### 6.2 权限标识格式

```
product:add / product:edit / product:delete / product:view
customer:add / customer:edit / ...
price:add / price:edit / price:approve / price:view
order:add / order:edit / order:view
delivery:print / delivery:view
acceptance:add / acceptance:view
purchase:add / purchase:view
report:view / report:export
template:design / template:view
user:manage
log:view
```

### 6.3 操作日志记录场景

| 场景 | 记录内容 |
|------|----------|
| 报价新增/修改 | 旧→新价格、修改人、时间 |
| 报价审批 | 提交人、审批人、结果、意见 |
| 订单创建/修改 | 订单号、修改内容 |
| 加退单/换货 | 类型、原行、新行 |
| 验收单录入 | 实收数量、与送货差异 |
| 采购单录入 | 采购人、金额 |
| 模板设计/修改 | 模板ID、修改内容 |
| 批量导入 | 文件名、成功/失败行数 |
| 用户登录 | 登录人、IP、时间 |

### 6.4 后端实现

```
Spring Security + JWT
├── 登录 → 签发JWT
├── 请求拦截 → 校验JWT
├── 权限校验 → permission_key
└── AOP操作日志 → @OperationLog注解自动记录
```

---

## 7. 部署建议

### 7.1 服务器配置

| 配置 | 推荐 |
|------|------|
| CPU | 2核 |
| 内存 | 4GB |
| 硬盘 | 50GB SSD |
| 带宽 | 5Mbps |
| 月费参考 | ¥200-400 |

### 7.2 架构

```
用户浏览器 → nginx(80/443) → Spring Boot Jar(8080) → MySQL(3306)
             静态资源(Vue)       REST API              数据
```

### 7.3 环境版本

| 组件 | 版本 |
|------|------|
| JDK | 17+ |
| MySQL | 8.0+ |
| Node.js | 18+ |
| Maven | 3.8+ |
| Nginx | 1.24+ |

### 7.4 备份策略

| 项目 | 策略 |
|------|------|
| 数据库 | 每日凌晨全量备份，保留7天；月备份保留1年 |
| 代码 | Git版本管理 |
| 操作日志 | 保留5年，季度归档 |

### 7.5 安全措施

| 措施 | 说明 |
|------|------|
| HTTPS | Let's Encrypt免费证书 |
| 登录限制 | 错误5次锁定30分钟 |
| 操作日志 | 全量记录，不可删除 |
| 定期备份 | 自动+异地 |
| 权限控制 | 最小权限原则 |

---

## 附录：待确认/待定事项

| # | 事项 | 状态 |
|---|------|------|
| 1 | 小程序客户自服务 | 后续版本 |
| 2 | 配送员/采购员功能 | 后续版本 |
| 3 | 库存管理 | 模拟库存（采购−验收） |
| 4 | 供应商在线下单 | 暂无计划 |
| 5 | 与财务系统对接 | 暂无计划 |
