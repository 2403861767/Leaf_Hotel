-- ============================================================================
-- 酒店前台管理系统 - 数据库初始化脚本
-- 版本: v1.0 MVP
-- 数据库: MySQL 8.4 LTS
-- 字符集: utf8mb4
-- ============================================================================

-- ==================== Section 1: 建库 ====================

CREATE DATABASE IF NOT EXISTS leaf_hotel
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE leaf_hotel;

-- ==================== Section 2: 建表 ====================

-- 关闭外键检查，允许重复执行时安全删除旧表
SET FOREIGN_KEY_CHECKS = 0;

-- 2.1 系统角色表
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    role_id     INT          NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    role_name   VARCHAR(50)  NOT NULL COMMENT '角色名称',
    role_key    VARCHAR(50)  NOT NULL COMMENT '角色标识',
    description VARCHAR(255) DEFAULT NULL COMMENT '角色描述',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (role_id),
    UNIQUE KEY uk_role_key (role_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统角色表';

-- 2.2 系统用户表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    username      VARCHAR(50)  NOT NULL COMMENT '登录用户名',
    password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希',
    real_name     VARCHAR(50)  NOT NULL COMMENT '真实姓名',
    role_id       INT          NOT NULL COMMENT '角色ID',
    status        TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1-启用 0-停用',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username),
    KEY idx_sys_user_role (role_id),
    CONSTRAINT fk_sys_user_role FOREIGN KEY (role_id) REFERENCES sys_role (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';

-- 2.3 角色权限关联表
DROP TABLE IF EXISTS sys_role_permission;
CREATE TABLE sys_role_permission (
    id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '自增ID',
    role_id         INT          NOT NULL COMMENT '角色ID',
    permission_code VARCHAR(100) NOT NULL COMMENT '权限代码',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_srp_role_id (role_id),
    KEY idx_srp_permission_code (permission_code),
    CONSTRAINT fk_srp_role FOREIGN KEY (role_id) REFERENCES sys_role (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- 2.4 房型表
DROP TABLE IF EXISTS room_type;
CREATE TABLE room_type (
    id          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '房型ID',
    type_name   VARCHAR(50)   NOT NULL COMMENT '房型名称',
    base_price  DECIMAL(10,2) NOT NULL COMMENT '标准价格',
    max_guests  INT           NOT NULL COMMENT '最大入住人数',
    bed_type    VARCHAR(50)   NOT NULL COMMENT '床型',
    area        DECIMAL(6,2)  NOT NULL COMMENT '房间面积(m²)',
    description TEXT          DEFAULT NULL COMMENT '房型描述',
    status      TINYINT       NOT NULL DEFAULT 1 COMMENT '状态：1-在售 0-停售',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    created_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='房型表';

-- 2.5 客房表
DROP TABLE IF EXISTS room;
CREATE TABLE room (
    id          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '房间ID',
    room_number VARCHAR(10) NOT NULL COMMENT '房间号',
    floor       INT         NOT NULL COMMENT '楼层',
    room_type_id BIGINT     NOT NULL COMMENT '关联房型',
    status      VARCHAR(20) NOT NULL DEFAULT 'available' COMMENT '房态：available/occupied/dirty/maintenance',
    version     INT         NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    created_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_room_number (room_number),
    KEY idx_room_status (status),
    KEY idx_room_type (room_type_id),
    CONSTRAINT fk_room_type FOREIGN KEY (room_type_id) REFERENCES room_type (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客房表';

-- 2.6 客人表
DROP TABLE IF EXISTS guest;
CREATE TABLE guest (
    id         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '客人ID',
    name       VARCHAR(50)  NOT NULL COMMENT '客人姓名',
    id_type    VARCHAR(20)  NOT NULL COMMENT '证件类型：identity/passport/other',
    id_number  VARCHAR(50)  NOT NULL COMMENT '证件号',
    phone      VARCHAR(20)  NOT NULL COMMENT '联系电话',
    address    VARCHAR(255) DEFAULT NULL COMMENT '地址',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_guest_name (name),
    KEY idx_guest_id_number (id_number),
    KEY idx_guest_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客人表';

-- 2.7 订单表
DROP TABLE IF EXISTS reservation;
CREATE TABLE reservation (
    id              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    booking_number  VARCHAR(50)   NOT NULL COMMENT '预订编号',
    guest_id        BIGINT        NOT NULL COMMENT '客人ID',
    room_type_id    BIGINT        NOT NULL COMMENT '预订房型',
    check_in_date   DATE          NOT NULL COMMENT '预订入住日期',
    check_out_date  DATE          NOT NULL COMMENT '预订离店日期',
    status          VARCHAR(20)   NOT NULL COMMENT '状态：pending/confirmed/checked_in/checked_out/cancelled',
    source          VARCHAR(20)   NOT NULL COMMENT '订单来源：walk_in/phone/front_desk/manager/online_direct/online_ota/contract',
    deposit_amount  DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '押金金额',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    created_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_booking_number (booking_number),
    KEY idx_reservation_guest (guest_id),
    KEY idx_reservation_status (status),
    KEY idx_reservation_dates (check_in_date, check_out_date),
    CONSTRAINT fk_reservation_guest FOREIGN KEY (guest_id) REFERENCES guest (id),
    CONSTRAINT fk_reservation_room_type FOREIGN KEY (room_type_id) REFERENCES room_type (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- 2.8 入住记录表
DROP TABLE IF EXISTS registration;
CREATE TABLE registration (
    id                       BIGINT        NOT NULL AUTO_INCREMENT COMMENT '入住单号',
    reservation_id           BIGINT        DEFAULT NULL COMMENT '关联订单ID（散客为NULL）',
    guest_id                 BIGINT        NOT NULL COMMENT '客人ID',
    room_id                  BIGINT        NOT NULL COMMENT '实际入住房间ID',
    check_in_time            DATETIME      NOT NULL COMMENT '实际入住时间',
    expected_check_out_time  DATETIME      NOT NULL COMMENT '预计离店时间',
    actual_check_out_time    DATETIME      DEFAULT NULL COMMENT '实际离店时间',
    guest_count              INT           NOT NULL COMMENT '同住人数',
    deposit_amount           DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '押金总金额',
    operator_id              BIGINT        NOT NULL COMMENT '办理人（关联sys_user）',
    source                   VARCHAR(20)   NOT NULL COMMENT '入住来源',
    status                   VARCHAR(20)   NOT NULL COMMENT '状态：in_house/checked_out',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    created_at               DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_reg_status (status),
    KEY idx_reg_guest (guest_id),
    KEY idx_reg_room (room_id),
    KEY idx_reg_operator (operator_id),
    KEY idx_reg_reservation (reservation_id),
    CONSTRAINT fk_reg_reservation FOREIGN KEY (reservation_id) REFERENCES reservation (id),
    CONSTRAINT fk_reg_guest FOREIGN KEY (guest_id) REFERENCES guest (id),
    CONSTRAINT fk_reg_room FOREIGN KEY (room_id) REFERENCES room (id),
    CONSTRAINT fk_reg_operator FOREIGN KEY (operator_id) REFERENCES sys_user (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='入住记录表';

-- 2.9 押金记录表
DROP TABLE IF EXISTS deposit;
CREATE TABLE deposit (
    id               BIGINT        NOT NULL AUTO_INCREMENT COMMENT '押金单ID',
    registration_id  BIGINT        NOT NULL COMMENT '关联入住记录',
    payment_method   VARCHAR(20)   NOT NULL COMMENT '支付方式：cash/wechat/alipay/bank_card',
    amount           DECIMAL(10,2) NOT NULL COMMENT '押金金额',
    slip_number      VARCHAR(100)  DEFAULT NULL COMMENT '押金单号',
    transaction_no   VARCHAR(100)  DEFAULT NULL COMMENT '交易流水号',
    auth_code        VARCHAR(100)  DEFAULT NULL COMMENT '授权码',
    status           VARCHAR(20)   NOT NULL COMMENT '状态：active/refunded',
    operator_id      BIGINT        NOT NULL COMMENT '操作人（关联sys_user）',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    created_at       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_deposit_registration (registration_id),
    KEY idx_deposit_status (status),
    KEY idx_deposit_operator (operator_id),
    CONSTRAINT fk_deposit_registration FOREIGN KEY (registration_id) REFERENCES registration (id),
    CONSTRAINT fk_deposit_operator FOREIGN KEY (operator_id) REFERENCES sys_user (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='押金记录表';

-- 2.10 账务流水表
DROP TABLE IF EXISTS transaction_log;
CREATE TABLE transaction_log (
    id               BIGINT        NOT NULL AUTO_INCREMENT COMMENT '流水ID',
    registration_id  BIGINT        NOT NULL COMMENT '关联入住记录',
    transaction_type VARCHAR(50)   NOT NULL COMMENT '类型：room_charge/deposit/refund/additional/reversal',
    amount           DECIMAL(10,2) NOT NULL COMMENT '金额（正为收入，负为支出）',
    balance_before   DECIMAL(10,2) NOT NULL COMMENT '操作前余额',
    balance_after    DECIMAL(10,2) NOT NULL COMMENT '操作后余额',
    description      VARCHAR(255)  DEFAULT NULL COMMENT '描述',
    operator_id      BIGINT        NOT NULL COMMENT '操作人',
    reversal_of      BIGINT        DEFAULT NULL COMMENT '被冲销的流水ID',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    created_at       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_tlog_registration (registration_id),
    KEY idx_tlog_type (transaction_type),
    KEY idx_tlog_operator (operator_id),
    KEY idx_tlog_reversal (reversal_of),
    CONSTRAINT fk_tlog_registration FOREIGN KEY (registration_id) REFERENCES registration (id),
    CONSTRAINT fk_tlog_operator FOREIGN KEY (operator_id) REFERENCES sys_user (id),
    CONSTRAINT fk_tlog_reversal FOREIGN KEY (reversal_of) REFERENCES transaction_log (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账务流水表';

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- ==================== Section 3: Mock 数据 ====================

-- 3.1 sys_role（4条角色数据）
INSERT INTO sys_role (role_id, role_name, role_key, description) VALUES
(1, '前台接待',   'receptionist', '一线操作人员，负责日常接待工作'),
(2, '前台领班',   'supervisor',   '在接待权限基础上，增加账务管理能力'),
(3, '前台经理',   'manager',      '在领班权限基础上，增加定价与人员管理能力'),
(4, '系统管理员', 'admin',        '系统最高权限，可操作用户与日志');

-- 3.2 sys_user（10条用户数据，密码均为 "123456" 的BCrypt哈希）
INSERT INTO sys_user (id, username, password_hash, real_name, role_id, status) VALUES
(1,  'zhangsan', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '张三', 1, 1),
(2,  'lisi',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '李四', 1, 1),
(3,  'wangwu',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '王五', 2, 1),
(4,  'zhaoliu',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '赵六', 2, 1),
(5,  'sunqi',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '孙七', 3, 1),
(6,  'zhouba',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '周八', 3, 1),
(7,  'wujiu',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '吴九', 4, 1),
(8,  'zhengshi', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '郑十', 4, 1),
(9,  'chenyi',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '陈一', 1, 1),
(10, 'liner',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '林二', 1, 0);

-- 3.3 sys_role_permission（10条权限分配）
INSERT INTO sys_role_permission (id, role_id, permission_code) VALUES
(1,  1, 'checkin:create'),
(2,  1, 'checkin:query'),
(3,  1, 'checkout:create'),
(4,  1, 'guest:create'),
(5,  1, 'room:view'),
(6,  1, 'deposit:create'),
(7,  2, 'deposit:refund'),
(8,  2, 'finance:view'),
(9,  3, 'pricing:adjust'),
(10, 4, 'admin:user_mgmt');

-- 3.4 room_type（10种房型）
INSERT INTO room_type (id, type_name, base_price, max_guests, bed_type, area, description, status) VALUES
(1,  '标准大床房', 288.00,  2, '大床', 25.00, '舒适的标准大床房，配备1.8米大床', 1),
(2,  '标准双床房', 328.00,  2, '双床', 30.00, '舒适的标准双床房，配备两张1.2米单人床', 1),
(3,  '豪华大床房', 458.00,  2, '大床', 35.00, '宽敞的豪华大床房，配备2米大床及会客区', 1),
(4,  '豪华套房',   688.00,  3, '大床', 50.00, '豪华套房，配备独立客厅和卧室', 1),
(5,  '家庭房',     528.00,  4, '双床', 45.00, '适合家庭入住，配备1.8米大床和1.2米单人床', 1),
(6,  '商务大床房', 398.00,  2, '大床', 32.00, '商务人士首选，配备办公桌和高速网络', 1),
(7,  '商务双床房', 428.00,  2, '双床', 35.00, '商务双床房，配备两张1.35米单人床和办公区', 1),
(8,  '精品大床房', 368.00,  2, '大床', 28.00, '精品装修大床房，配备1.8米大床和智能设施', 1),
(9,  '无障碍房',   328.00,  2, '大床', 35.00, '无障碍设施房，配备专用洗浴设施', 1),
(10, '总统套房',  1288.00, 3, '大床', 80.00, '总统级豪华套房，配备独立客厅、餐厅和书房', 1);

-- 3.5 room（10间客房，5 occupied + 5 dirty）
INSERT INTO room (id, room_number, floor, room_type_id, status, version) VALUES
(1,  '801', 8, 1,  'occupied',    2),
(2,  '802', 8, 2,  'occupied',    1),
(3,  '803', 8, 2,  'occupied',    1),
(4,  '805', 8, 3,  'occupied',    1),
(5,  '806', 8, 3,  'occupied',    1),
(6,  '807', 8, 4,  'dirty',       2),
(7,  '808', 8, 5,  'dirty',       2),
(8,  '901', 9, 1,  'dirty',       2),
(9,  '902', 9, 2,  'dirty',       2),
(10, '903', 9, 6,  'dirty',       2);

-- 3.6 guest（10位客人）
INSERT INTO guest (id, name, id_type, id_number, phone, address) VALUES
(1,  '王建国', 'identity', '110101199001011234', '13800138001', '北京市朝阳区建国路88号'),
(2,  '李秀英', 'identity', '110101198505152345', '13900139002', '上海市浦东新区陆家嘴环路100号'),
(3,  '张伟',   'identity', '320101198803014567', '13600136003', '广东省广州市天河区天河路385号'),
(4,  '刘芳',   'identity', '440101199212126789', '13700137004', '深圳市南山区科技园南路1号'),
(5,  '陈明',   'passport', 'E12345678',           '13500135005', '北京市海淀区中关村大街1号'),
(6,  '赵丽',   'identity', '330101199506308901', '15800158006', '浙江省杭州市西湖区文三路478号'),
(7,  '黄强',   'identity', '510101198712112345', '15900159007', '四川省成都市锦江区红星路三段1号'),
(8,  '周敏',   'passport', 'G98765432',           '18600186008', '江苏省南京市鼓楼区汉中路2号'),
(9,  '吴涛',   'identity', '420101199008014567', '18700187009', '湖北省武汉市江汉区解放大道686号'),
(10, '郑雪',   'other',    'H12345678',           '18800188010', '福建省厦门市思明区鹭江道8号');

-- 3.7 reservation（10条订单：3 checked_out, 3 checked_in, 2 confirmed, 1 cancelled, 1 pending）
INSERT INTO reservation (id, booking_number, guest_id, room_type_id, check_in_date, check_out_date, status, source, deposit_amount) VALUES
(1,  'BK202604280001', 1,  1, '2026-04-28', '2026-04-30', 'checked_out', 'walk_in',       200.00),
(2,  'BK202604280002', 3,  2, '2026-04-29', '2026-05-01', 'checked_in',  'phone',         500.00),
(3,  'BK202604280003', 4,  3, '2026-04-30', '2026-05-02', 'confirmed',   'front_desk',    458.00),
(4,  'BK202604280004', 5,  4, '2026-05-01', '2026-05-03', 'confirmed',   'manager',      1000.00),
(5,  'BK202604270001', 6,  5, '2026-04-27', '2026-04-29', 'checked_out', 'walk_in',       800.00),
(6,  'BK202604280005', 7,  2, '2026-04-28', '2026-04-29', 'cancelled',   'phone',           0.00),
(7,  'BK202604250001', 8,  3, '2026-04-25', '2026-04-28', 'checked_out', 'online_ota',    600.00),
(8,  'BK202604280006', 9,  5, '2026-05-02', '2026-05-05', 'pending',     'contract',       0.00),
(9,  'BK202604290001', 10, 4, '2026-04-29', '2026-04-30', 'checked_in',  'walk_in',       500.00),
(10, 'BK202604290002', 2,  1, '2026-05-03', '2026-05-05', 'confirmed',   'online_direct', 288.00);

-- 3.8 registration（10条入住记录：5 in_house + 5 checked_out）
INSERT INTO registration (id, reservation_id, guest_id, room_id, check_in_time, expected_check_out_time, actual_check_out_time, guest_count, deposit_amount, operator_id, source, status) VALUES
(1,  1,     1,  6,  '2026-04-28 15:00:00', '2026-04-30 12:00:00', '2026-04-30 10:30:00', 1, 200.00,  1, 'walk_in',    'checked_out'),
(2,  2,     3,  1,  '2026-04-29 14:00:00', '2026-05-01 12:00:00', NULL,                   2, 500.00,  2, 'phone',      'in_house'),
(3,  5,     6,  7,  '2026-04-27 14:00:00', '2026-04-29 12:00:00', '2026-04-29 11:00:00', 3, 800.00,  1, 'walk_in',    'checked_out'),
(4,  7,     8,  8,  '2026-04-25 16:00:00', '2026-04-28 12:00:00', '2026-04-28 09:00:00', 1, 600.00,  2, 'online_ota', 'checked_out'),
(5,  9,     10, 2,  '2026-04-29 14:30:00', '2026-04-30 12:00:00', NULL,                   1, 500.00,  3, 'walk_in',    'in_house'),
(6,  NULL,  2,  3,  '2026-04-29 11:00:00', '2026-05-01 12:00:00', NULL,                   2, 400.00,  1, 'walk_in',    'in_house'),
(7,  NULL,  4,  4,  '2026-04-29 15:00:00', '2026-05-02 12:00:00', NULL,                   2, 600.00,  2, 'front_desk', 'in_house'),
(8,  NULL,  7,  9,  '2026-04-26 12:00:00', '2026-04-28 12:00:00', '2026-04-28 11:00:00', 1, 300.00,  3, 'phone',      'checked_out'),
(9,  NULL,  9,  10, '2026-04-27 18:00:00', '2026-04-29 12:00:00', '2026-04-29 09:00:00', 4,   0.00,  1, 'contract',   'checked_out'),
(10, NULL,  5,  5,  '2026-04-28 20:00:00', '2026-04-30 12:00:00', NULL,                   1,1000.00,  4, 'manager',    'in_house');

-- 3.9 deposit（10条押金：6 active + 4 refunded，覆盖4种支付方式）
INSERT INTO deposit (id, registration_id, payment_method, amount, slip_number, transaction_no, auth_code, status, operator_id) VALUES
(1,  2,  'cash',      500.00, NULL,                    NULL,                   NULL,               'active',  2),
(2,  5,  'wechat',    300.00, 'WX2026042912345678',    'TXN202604290001',      NULL,               'active',  3),
(3,  5,  'alipay',    200.00, 'AL2026042956789012',    'TXN202604290002',      NULL,               'active',  3),
(4,  6,  'bank_card', 400.00, '6222021234567890',      'TXN202604290003',      'AUTH20260429001',  'active',  1),
(5,  7,  'cash',      600.00, NULL,                    NULL,                   NULL,               'active',  2),
(6,  10, 'wechat',   1000.00, 'WX2026043012345678',    'TXN202604300001',      NULL,               'active',  4),
(7,  1,  'cash',      200.00, NULL,                    NULL,                   NULL,               'refunded', 1),
(8,  3,  'bank_card', 800.00, '6222029876543210',      'TXN202604270001',      'AUTH20260427001',  'refunded', 1),
(9,  4,  'alipay',    600.00, 'AL2026042567890123',    'TXN202604250001',      NULL,               'refunded', 2),
(10, 8,  'cash',      300.00, NULL,                    NULL,                   NULL,               'refunded', 3);

-- 3.10 transaction_log（10条账务流水）
INSERT INTO transaction_log (id, registration_id, transaction_type, amount, balance_before, balance_after, description, operator_id, reversal_of) VALUES
(1,  2,  'deposit',      500.00,    0.00,  500.00,  '现金押金 - 办理入住',              2,  NULL),
(2,  5,  'deposit',      300.00,    0.00,  300.00,  '微信押金 - 办理入住',              3,  NULL),
(3,  5,  'deposit',      200.00,  300.00,  500.00,  '支付宝押金 - 补交押金',            3,  NULL),
(4,  6,  'deposit',      400.00,    0.00,  400.00,  '银行卡押金 - 办理入住',            1,  NULL),
(5,  7,  'deposit',      600.00,    0.00,  600.00,  '现金押金 - 办理入住',              2,  NULL),
(6,  10, 'deposit',     1000.00,    0.00, 1000.00,  '微信押金 - 办理入住',              4,  NULL),
(7,  1,  'deposit',      200.00,    0.00,  200.00,  '现金押金 - 办理入住',              1,  NULL),
(8,  1,  'room_charge', -150.00,  200.00,   50.00,  '房费扣减（2晚 × 标准大床房）',     1,  NULL),
(9,  1,  'refund',       -50.00,   50.00,    0.00,  '退押金余额 - 办理退房',            1,  NULL),
(10, 3,  'deposit',      800.00,    0.00,  800.00,  '银行卡押金 - 办理入住',            1,  NULL);

-- ==================== Section 4: 数据验证 ====================

SELECT 'sys_role' AS table_name, COUNT(*) AS row_count FROM sys_role
UNION ALL
SELECT 'sys_user', COUNT(*) FROM sys_user
UNION ALL
SELECT 'sys_role_permission', COUNT(*) FROM sys_role_permission
UNION ALL
SELECT 'room_type', COUNT(*) FROM room_type
UNION ALL
SELECT 'room', COUNT(*) FROM room
UNION ALL
SELECT 'guest', COUNT(*) FROM guest
UNION ALL
SELECT 'reservation', COUNT(*) FROM reservation
UNION ALL
SELECT 'registration', COUNT(*) FROM registration
UNION ALL
SELECT 'deposit', COUNT(*) FROM deposit
UNION ALL
SELECT 'transaction_log', COUNT(*) FROM transaction_log
ORDER BY table_name;
