-- ============================================================
-- 生鲜配送管理系统 — 数据库建表脚本
-- MySQL 8.0+
-- ============================================================

CREATE DATABASE IF NOT EXISTS fresh_delivery DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE fresh_delivery;

-- ============================================================
-- 1. 系统管理表
-- ============================================================

DROP TABLE IF EXISTS sys_user;
DROP TABLE IF EXISTS sys_user_role;
DROP TABLE IF EXISTS sys_role_permission;
DROP TABLE IF EXISTS sys_role;
DROP TABLE IF EXISTS sys_permission;
DROP TABLE IF EXISTS sys_operation_log;
DROP TABLE IF EXISTS sys_approval;

CREATE TABLE sys_permission (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100)  NOT NULL COMMENT '权限名称',
    permission_key VARCHAR(50) NOT NULL COMMENT '权限标识',
    module      VARCHAR(50)  COMMENT '所属模块',
    description VARCHAR(200) COMMENT '说明',
    UNIQUE KEY uk_permission_key (permission_key)
) COMMENT = '权限表';

CREATE TABLE sys_role (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_name   VARCHAR(100) NOT NULL COMMENT '角色名称',
    role_key    VARCHAR(50)  NOT NULL COMMENT '角色标识',
    description VARCHAR(200) COMMENT '说明',
    UNIQUE KEY uk_role_key (role_key)
) COMMENT = '角色表';

CREATE TABLE sys_user (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL COMMENT '登录名',
    password    VARCHAR(100) NOT NULL COMMENT '加密密码',
    real_name   VARCHAR(50)  COMMENT '真实姓名',
    role_id     BIGINT       COMMENT '关联角色(单角色)',
    status      TINYINT DEFAULT 1 COMMENT '0禁用 1启用',
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_username (username)
) COMMENT = '用户表';

CREATE TABLE sys_user_role (
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    UNIQUE KEY uk_user_role (user_id, role_id)
) COMMENT = '用户角色关联表';

CREATE TABLE sys_role_permission (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id       BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    UNIQUE KEY uk_role_permission (role_id, permission_id)
) COMMENT = '角色权限关联表';

CREATE TABLE sys_operation_log (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT       COMMENT '操作人ID',
    user_name    VARCHAR(50)  COMMENT '操作人(冗余)',
    module       VARCHAR(50)  COMMENT '模块标识',
    action       VARCHAR(50)  COMMENT '操作类型',
    target_type  VARCHAR(50)  COMMENT '目标类型',
    target_id    BIGINT       COMMENT '目标ID',
    old_value    JSON         COMMENT '修改前',
    new_value    JSON         COMMENT '修改后',
    operate_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    ip_address   VARCHAR(50)  COMMENT 'IP地址',
    INDEX idx_operate_time (operate_time),
    INDEX idx_user_id (user_id)
) COMMENT = '操作日志表';

CREATE TABLE sys_approval (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    biz_type     VARCHAR(50)  COMMENT '业务类型(PRICE)',
    biz_id       BIGINT       COMMENT '关联业务ID',
    submit_user  VARCHAR(50)  COMMENT '提交人',
    approver     VARCHAR(50)  COMMENT '审批人',
    status       TINYINT DEFAULT 0 COMMENT '0待审批 1已批准 2已拒绝',
    remark       VARCHAR(500) COMMENT '审批意见',
    submit_time  DATETIME,
    approve_time DATETIME
) COMMENT = '审批流表';

-- ============================================================
-- 2. 商品与SKU表
-- ============================================================

CREATE TABLE product (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100) NOT NULL COMMENT '标准品名',
    en_short   VARCHAR(20)  COMMENT '英文缩写',
    status     TINYINT DEFAULT 1 COMMENT '0停用 1启用',
    created_at DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (name),
    INDEX idx_en_short (en_short)
) COMMENT = '商品表';

CREATE TABLE product_alias (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    alias     VARCHAR(100) COMMENT '别名',
    en_short  VARCHAR(20)  COMMENT '英文缩写',
    INDEX idx_alias (alias),
    INDEX idx_en_short (en_short)
) COMMENT = '全局别名表';

CREATE TABLE sku (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    spec_name  VARCHAR(100) COMMENT '规格名称',
    spec_value VARCHAR(50)  COMMENT '规格值',
    unit       VARCHAR(20)  COMMENT '单位',
    status     TINYINT DEFAULT 1 COMMENT '0停用 1启用',
    created_at DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT = 'SKU表';

-- ============================================================
-- 3. 客户与配送点表
-- ============================================================

CREATE TABLE customer_category (
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    name    VARCHAR(50) NOT NULL COMMENT '分类名',
    status  TINYINT DEFAULT 1
) COMMENT = '客户分类表';

CREATE TABLE customer (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id      BIGINT       COMMENT '客户分类',
    name             VARCHAR(100) NOT NULL COMMENT '客户名称',
    contact_person   VARCHAR(50)  COMMENT '联系人',
    phone            VARCHAR(20)  COMMENT '联系电话',
    address          VARCHAR(200) COMMENT '默认地址',
    settlement_cycle TINYINT      COMMENT '结算周期 1周 2月',
    status           TINYINT DEFAULT 1 COMMENT '0停用 1启用',
    created_at       DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT = '客户表';

CREATE TABLE delivery_point (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id    BIGINT       NOT NULL,
    name           VARCHAR(100) NOT NULL COMMENT '配送点名称',
    address        VARCHAR(200) COMMENT '配送地址',
    contact_person VARCHAR(50)  COMMENT '联系人',
    phone          VARCHAR(20)  COMMENT '联系电话',
    status         TINYINT DEFAULT 1,
    created_at     DATETIME     DEFAULT CURRENT_TIMESTAMP
) COMMENT = '配送点表';

CREATE TABLE customer_sku_mapping (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id     BIGINT       NOT NULL,
    sku_id          BIGINT       COMMENT '标准SKU',
    customer_name   VARCHAR(100) COMMENT '客户维度品名',
    customer_alias  VARCHAR(200) COMMENT '客户维度别名',
    en_short        VARCHAR(20)  COMMENT '客户维度英文缩写',
    status          TINYINT DEFAULT 1,
    INDEX idx_customer_id (customer_id),
    INDEX idx_sku_id (sku_id),
    INDEX idx_customer_name (customer_name),
    INDEX idx_en_short (en_short)
) COMMENT = '客户SKU映射表';

CREATE TABLE temp_product (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id   BIGINT       NOT NULL,
    name          VARCHAR(200) NOT NULL,
    spec          VARCHAR(100),
    unit          VARCHAR(20),
    price         DECIMAL(10,2) COMMENT '参考单价',
    status        TINYINT DEFAULT 0 COMMENT '0临时 1已转正式SKU',
    formal_sku_id BIGINT       COMMENT '转正后关联SKU',
    created_at    DATETIME     DEFAULT CURRENT_TIMESTAMP
) COMMENT = '临时商品表(客户维度)';

-- ============================================================
-- 4. 报价表
-- ============================================================

CREATE TABLE price_template (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    status      TINYINT DEFAULT 1,
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP
) COMMENT = '报价方案(模板)';

CREATE TABLE price_template_sku (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    template_id BIGINT       NOT NULL,
    sku_id     BIGINT       NOT NULL,
    price      DECIMAL(10,2) NOT NULL,
    start_date DATE         COMMENT '有效期起',
    end_date   DATE         COMMENT '有效期止'
) COMMENT = '报价方案明细';

CREATE TABLE customer_price (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT       NOT NULL,
    sku_id      BIGINT       NOT NULL,
    price       DECIMAL(10,2) NOT NULL,
    start_date  DATE         COMMENT '有效期起',
    end_date    DATE         COMMENT '有效期止',
    source_type TINYINT DEFAULT 0 COMMENT '0手动 1模板导入',
    source_id   BIGINT       COMMENT '来源模板ID',
    status      TINYINT DEFAULT 0 COMMENT '0草稿 1生效 2已拒绝',
    INDEX idx_customer_sku (customer_id, sku_id),
    INDEX idx_date_range (start_date, end_date)
) COMMENT = '客户报价';

CREATE TABLE delivery_point_price (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    point_id   BIGINT       NOT NULL,
    sku_id     BIGINT       NOT NULL,
    price      DECIMAL(10,2) NOT NULL,
    start_date DATE         COMMENT '有效期起',
    end_date   DATE         COMMENT '有效期止',
    status     TINYINT DEFAULT 1,
    INDEX idx_point_sku (point_id, sku_id)
) COMMENT = '配送点报价';

-- ============================================================
-- 5. 订单表
-- ============================================================

CREATE TABLE order (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no    VARCHAR(30)  NOT NULL COMMENT '订单编号',
    order_date  DATE         NOT NULL COMMENT '订单日期(D天)',
    customer_id BIGINT       NOT NULL,
    point_id    BIGINT,
    status      TINYINT DEFAULT 0 COMMENT '0草稿 1已确认 2已送货 3已验收 4已结算',
    remark      VARCHAR(500),
    created_by  VARCHAR(50),
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_order_no (order_no),
    INDEX idx_order_date (order_date),
    INDEX idx_customer_id (customer_id),
    INDEX idx_status (status)
) COMMENT = '订单总表';

CREATE TABLE order_item (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id    BIGINT       NOT NULL,
    sku_id      BIGINT       COMMENT '标准SKU(可为NULL)',
    product_id  BIGINT       COMMENT '商品(可为临时商品ID)',
    item_name   VARCHAR(200) NOT NULL COMMENT '下单时实际品名',
    item_spec   VARCHAR(100),
    item_unit   VARCHAR(20),
    quantity    DECIMAL(10,2) NOT NULL,
    unit_price  DECIMAL(10,2) NOT NULL,
    subtotal    DECIMAL(12,2) NOT NULL,
    item_status TINYINT DEFAULT 0 COMMENT '0正常 1已退单 2已换货 3部分退单',
    remark      VARCHAR(500),
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_order_id (order_id)
) COMMENT = '订单明细';

CREATE TABLE order_adjustment (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id       BIGINT       NOT NULL,
    origin_item_id BIGINT       COMMENT '原订单行ID',
    adjust_type    TINYINT      NOT NULL COMMENT '0加单 1退单 2换货',
    adjust_date    DATE         NOT NULL,
    order_date     DATE         COMMENT '归属订单日期(D天)',
    item_name      VARCHAR(200),
    item_spec      VARCHAR(100),
    item_unit      VARCHAR(20),
    sku_id         BIGINT,
    quantity       DECIMAL(10,2),
    unit_price     DECIMAL(10,2),
    remark         VARCHAR(500),
    created_by     VARCHAR(50),
    created_at     DATETIME     DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_order_id (order_id),
    INDEX idx_order_date (order_date)
) COMMENT = '加单/退单/换货记录';

-- ============================================================
-- 6. 采购表
-- ============================================================

CREATE TABLE supplier (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(100) NOT NULL,
    contact_person VARCHAR(50),
    phone          VARCHAR(20),
    address        VARCHAR(200),
    is_default     TINYINT DEFAULT 0 COMMENT '0否 1默认(自采)',
    status         TINYINT DEFAULT 1
) COMMENT = '供应商表';

CREATE TABLE product_supplier (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id   BIGINT,
    sku_id       BIGINT,
    supplier_id  BIGINT       NOT NULL,
    is_primary   TINYINT DEFAULT 0
) COMMENT = '商品-供应商关联';

CREATE TABLE purchase_order (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no        VARCHAR(30)  NOT NULL COMMENT '采购单号',
    order_date      DATE         NOT NULL,
    supplier_id     BIGINT       NOT NULL,
    source_type     TINYINT DEFAULT 0 COMMENT '0手动 1自动生成',
    source_order_ids JSON        COMMENT '来源订单ID列表',
    total_amount    DECIMAL(12,2) DEFAULT 0,
    status          TINYINT DEFAULT 0 COMMENT '0草稿 1已确认 2已入库',
    created_by      VARCHAR(50),
    created_at      DATETIME     DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_purchase_order_no (order_no),
    INDEX idx_order_date (order_date),
    INDEX idx_supplier_id (supplier_id)
) COMMENT = '采购单';

CREATE TABLE purchase_item (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id    BIGINT       NOT NULL,
    sku_id      BIGINT       COMMENT '标准SKU',
    item_name   VARCHAR(200) NOT NULL,
    item_spec   VARCHAR(100),
    item_unit   VARCHAR(20),
    quantity    DECIMAL(10,2) NOT NULL,
    unit_price  DECIMAL(10,2) COMMENT '采购成本价',
    subtotal    DECIMAL(12,2) DEFAULT 0,
    remark      VARCHAR(500),
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP
) COMMENT = '采购明细';

-- ============================================================
-- 7. 配送表
-- ============================================================

CREATE TABLE delivery_order (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no       VARCHAR(30)  NOT NULL COMMENT '送货单号',
    delivery_date  DATE         NOT NULL COMMENT '送货日期(D+1天)',
    order_date     DATE         NOT NULL COMMENT '归属订单日期(D天)',
    customer_id    BIGINT       NOT NULL,
    point_id       BIGINT,
    total_quantity DECIMAL(10,2) DEFAULT 0,
    total_amount   DECIMAL(12,2) DEFAULT 0,
    template_id    BIGINT       COMMENT '使用的模板',
    print_count    INT DEFAULT 0,
    status         TINYINT DEFAULT 0 COMMENT '0已打印 1已送达 2已验收',
    created_by     VARCHAR(50),
    created_at     DATETIME     DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_delivery_order_no (order_no),
    INDEX idx_delivery_date (delivery_date),
    INDEX idx_order_date (order_date),
    INDEX idx_customer_id (customer_id)
) COMMENT = '送货单';

CREATE TABLE delivery_item (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    delivery_order_id BIGINT       NOT NULL,
    origin_order_id   BIGINT       COMMENT '来源订单ID',
    origin_item_id    BIGINT       COMMENT '来源订单行ID',
    item_name         VARCHAR(200) NOT NULL,
    item_spec         VARCHAR(100),
    item_unit         VARCHAR(20),
    quantity          DECIMAL(10,2) NOT NULL,
    unit_price        DECIMAL(10,2),
    subtotal          DECIMAL(12,2) DEFAULT 0,
    adjust_status     TINYINT DEFAULT 0 COMMENT '0正常 1部分退 2全部退 3换货 4加单',
    remark            VARCHAR(200)
) COMMENT = '送货单明细';

CREATE TABLE acceptance (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no         VARCHAR(30)  NOT NULL COMMENT '验收单号',
    delivery_order_id BIGINT      NOT NULL,
    delivery_date    DATE         NOT NULL,
    customer_id      BIGINT       NOT NULL,
    point_id         BIGINT,
    total_amount     DECIMAL(12,2) DEFAULT 0 COMMENT '实收总金额',
    total_loss_amount DECIMAL(12,2) DEFAULT 0 COMMENT '损耗总金额',
    created_by       VARCHAR(50),
    created_at       DATETIME     DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_acceptance_order_no (order_no),
    INDEX idx_delivery_date (delivery_date),
    INDEX idx_customer_id (customer_id)
) COMMENT = '验收单';

CREATE TABLE acceptance_item (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    acceptance_id      BIGINT       NOT NULL,
    delivery_item_id   BIGINT       COMMENT '对应送货行',
    item_name          VARCHAR(200),
    item_spec          VARCHAR(100),
    item_unit          VARCHAR(20),
    delivered_quantity DECIMAL(10,2) COMMENT '送货数量',
    actual_quantity    DECIMAL(10,2) COMMENT '实收数量',
    loss_quantity      DECIMAL(10,2) COMMENT '损耗数量',
    unit_price         DECIMAL(10,2),
    actual_amount      DECIMAL(12,2) COMMENT '实收金额',
    remark             VARCHAR(500)
) COMMENT = '验收单明细';

-- ============================================================
-- 8. 报表表
-- ============================================================

CREATE TABLE delivery_template (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    paper_size    VARCHAR(20)  DEFAULT 'A4',
    orientation   VARCHAR(10)  DEFAULT 'portrait',
    copies        INT          DEFAULT 1,
    template_json LONGTEXT,
    is_default    TINYINT DEFAULT 0,
    status        TINYINT DEFAULT 1,
    created_by    VARCHAR(50),
    created_at    DATETIME     DEFAULT CURRENT_TIMESTAMP
) COMMENT = '送货单模板';

CREATE TABLE template_customer (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    template_id BIGINT NOT NULL,
    customer_id BIGINT COMMENT '客户(NULL=全局默认)',
    priority    INT    DEFAULT 0
) COMMENT = '模板-客户绑定';

-- ============================================================
-- 9. 初始数据
-- ============================================================

-- 初始角色
INSERT INTO sys_role (id, role_name, role_key, description) VALUES
(1, '文员', 'clerk', '代客下单、录入验收单、录入采购单'),
(2, '审批人', 'approver', '审批报价单、查看报表、用户管理');

-- 初始权限
INSERT INTO sys_permission (name, permission_key, module, description) VALUES
-- 系统管理
('用户管理', 'user:manage', 'user', '用户增删改查'),
('操作日志查看', 'log:view', 'log', '查看操作日志'),
-- 商品
('商品新增', 'product:add', 'product', ''),
('商品修改', 'product:edit', 'product', ''),
('商品删除', 'product:delete', 'product', ''),
('商品查看', 'product:view', 'product', ''),
-- 客户
('客户新增', 'customer:add', 'customer', ''),
('客户修改', 'customer:edit', 'customer', ''),
('客户删除', 'customer:delete', 'customer', ''),
('客户查看', 'customer:view', 'customer', ''),
-- 报价
('报价新增', 'price:add', 'price', ''),
('报价修改', 'price:edit', 'price', ''),
('报价审批', 'price:approve', 'price', ''),
('报价查看', 'price:view', 'price', ''),
-- 订单
('订单新增', 'order:add', 'order', ''),
('订单修改', 'order:edit', 'order', ''),
('订单查看', 'order:view', 'order', ''),
-- 配送
('送货单打印', 'delivery:print', 'delivery', ''),
('送货单查看', 'delivery:view', 'delivery', ''),
('验收单新增', 'acceptance:add', 'acceptance', ''),
('验收单查看', 'acceptance:view', 'acceptance', ''),
-- 采购
('采购单新增', 'purchase:add', 'purchase', ''),
('采购单查看', 'purchase:view', 'purchase', ''),
-- 报表
('报表查看', 'report:view', 'report', ''),
('报表导出', 'report:export', 'report', ''),
-- 模板
('模板设计', 'template:design', 'template', ''),
('模板查看', 'template:view', 'template', '');

-- 文员角色权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(1, 2),  -- log:view
(1, 3),  (1, 4), (1, 5), (1, 6),   -- product:*
(1, 7),  (1, 8), (1, 9), (1, 10),  -- customer:*
(1, 11), (1, 12), (1, 14),          -- price:add/edit/view
(1, 15), (1, 16), (1, 17),          -- order:*
(1, 18), (1, 19),                    -- delivery:*
(1, 20), (1, 21),                    -- acceptance:*
(1, 22), (1, 23),                    -- purchase:*
(1, 24), (1, 25),                    -- report:*
(1, 26), (1, 27);                    -- template:*

-- 审批人角色权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(2, 1),  -- user:manage
(2, 2),  -- log:view
(2, 6),  -- product:view
(2, 10), -- customer:view
(2, 13), -- price:approve
(2, 14), -- price:view
(2, 17), -- order:view
(2, 18), -- delivery:print
(2, 19), -- delivery:view
(2, 21), -- acceptance:view
(2, 23), -- purchase:view
(2, 24), -- report:view
(2, 25), -- report:export
(2, 27); -- template:view

-- 初始用户 (密码: admin123 → BCrypt)
INSERT INTO sys_user (id, username, password, real_name, role_id, status) VALUES
(1, 'admin',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '管理员', 2, 1),
(2, 'clerk01',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '文员一',   1, 1),
(3, 'clerk02',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '文员二',   1, 1);

-- 初始用户角色关联
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 2),
(2, 1),
(3, 1);

-- 初始送货单模板(A4默认)
INSERT INTO delivery_template (id, name, paper_size, orientation, copies, is_default, status) VALUES
(1, 'A4标准送货单', 'A4', 'portrait', 1, 1, 1);

-- 默认供应商
INSERT INTO supplier (id, name, is_default, status) VALUES
(1, '自采', 1, 1);