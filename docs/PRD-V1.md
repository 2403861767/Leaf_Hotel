# 酒店前台管理系统 — v1.0 MVP 开发需求文档

> 本文档从完整 PRD 中提取 v1.0 MVP 范围，仅包含第一版开发所需内容。完整版见 [PRD.md](PRD.md)。

---

## 一、产品概述

### 1.1 项目背景

传统酒店前台系统多采用单体架构，各模块耦合严重，难以适应酒店业务快速变化的需求。本系统旨在通过 **RESTful API + 前后端分离**架构，实现酒店前台模块的服务化拆分，为 Web 端提供统一的 HTTP 接口。

### 1.2 核心定位

- **范围限定**：仅关注"酒店前台"业务域，暂不覆盖客房部、财务部等部门职能。
- **目标用户**：酒店前台工作人员，包括前台接待员、领班、经理及系统管理员。
- **系统边界**：负责从客人抵店办理入住到离店结账的全生命周期管理，涵盖客房预订、入住登记、房态管理、账务处理等前台核心环节。

### 1.3 架构模式 — RESTful + 前后端分离

```
┌─────────────────────────────────────────────────┐
│                 前端层 (Frontend)                  │
│  ┌──────────────────────────────────────┐       │
│  │           Vue3 Web 端                 │       │
│  └────────────────┬─────────────────────┘       │
│                   │                              │
├───────────────────┴──────────────────────────────┤
│              HTTP / RESTful API                   │
│           JSON 数据交换 / JWT 认证                 │
├───────────────────┬──────────────────────────────┤
│                 后端层 (Backend)                   │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐          │
│  │  入住服务  │ │  房态服务  │ │  账务服务  │  ...    │
│  └──────────┘ └──────────┘ └──────────┘          │
│  ┌──────────────────────────────────────────┐     │
│  │        Spring Boot 权限拦截器链              │     │
│  └──────────────────────────────────────────┘     │
│  ┌──────────────────────────────────────────┐     │
│  │           数据持久层 (MySQL + Redis)        │     │
│  └──────────────────────────────────────────┘     │
└─────────────────────────────────────────────────┘
```

### 1.4 技术选型

| 层级 | 选型方案 | 版本 | 说明 |
|------|----------|------|------|
| 前端框架 | **Vue3** + **Vite** | 3.5+ / 8.x | Vue3 稳定版，Vite 8.x 为最新构建 |
| 前端 UI | **Element Plus** | 2.x | 当前稳定版 2.13+ |
| 状态管理 | **Pinia** | 2.x | Vue3 官方推荐 |
| HTTP 客户端 | **Axios** | 1.x | 当前稳定版 1.15+ |
| 后端框架 | **Spring Boot** | 3.5.x | 3.5.x 为当前活跃维护版本 |
| ORM | **MyBatis-Plus** | 3.5.x | 当前稳定版 3.5.16+ |
| API 文档 | **Swagger (SpringDoc)** | OpenAPI 3.0 | 自动生成接口文档 |
| 数据库 | **MySQL** | 8.4 LTS | 长期支持版（8.0 已于 2026-04 EOL） |
| 缓存/锁 | **Redis** | 7.x | 当前稳定版 7.4+ |
| 认证 | **JWT (Bearer token)** | — | 无状态认证 |
| 权限 | **Spring Security** | 6.x | 对应 Spring Boot 3.x 系列 |

### 1.5 接口设计规范

- 全部接口遵循 **RESTful** 风格
- 统一响应格式：`{ "code": 200, "message": "success", "data": {...} }`
- 分页请求：`/api/checkins?page=1&pageSize=20`
- 认证方式：请求头 `Authorization: Bearer <token>`
- 基础路径：`/api/v1`

---

## 二、角色权限定义

### 2.1 角色定义

| 角色 | 英文标识 | 描述 |
|------|----------|------|
| 前台接待 | `Receptionist` | 一线操作人员，负责日常接待工作 |
| 前台领班 | `Supervisor` | 在接待权限基础上，增加账务管理能力 |
| 前台经理 | `Manager` | 在领班权限基础上，增加定价与人员管理能力 |
| 系统管理员 | `Admin` | 系统最高权限，可操作用户与日志 |

### 2.2 v1.0 权限矩阵

v1.0 实现 4 角色全链路权限体系（硬编码），功能上只覆盖 **Receptionist** 的核心操作流程，Supervisor / Manager / Admin 的高阶功能后续版本开启。

| 业务模块 | 操作 | Receptionist | Supervisor | Manager | Admin |
|----------|------|:------------:|:----------:|:-------:|:-----:|
| **入住管理** | 办理入住 (Check-in) | ✅ | ✅ | ✅ | ✅ |
| | 查询入住记录 | ✅ | ✅ | ✅ | ✅ |
| | 修改入住信息 | ✅ | ✅ | ✅ | ✅ |
| **退房管理** | 办理退房 (Check-out) | ✅ | ✅ | ✅ | ✅ |
| | 退费操作 | ✅ | ✅ | ✅ | ✅ |
| | 退房历史查询 | ✅ | ✅ | ✅ | ✅ |
| **客人管理** | 查询客人信息 | ✅ | ✅ | ✅ | ✅ |
| | 新建/修改客人档案 | ✅ | ✅ | ✅ | ✅ |
| | 导入客人身份信息 | ✅ | ✅ | ✅ | ✅ |
| **房态管理** | 查看房态图 | ✅ | ✅ | ✅ | ✅ |
| | 查看房间详情 | ✅ | ✅ | ✅ | ✅ |
| **认证权限** | 登录/登出 | ✅ | ✅ | ✅ | ✅ |
| | 获取当前用户信息 | ✅ | ✅ | ✅ | ✅ |

> ✅ v1.0 实现；— 后续版本实现。

---

## 三、核心业务流程图

### 3.1 入住流程 (Check-in)

```
[客人到店]
     │
     ▼
[接待员核实预订信息] ──── 无预订 ──→ [散客开房：选择房型/房号]
     │
     ▼
[录入/核实入住人身份信息] ←── [可选：导入身份证 / 护照信息]
     │
     ▼
[收取押金 / 预授权]
     │
     ▼
[分配房间，系统校验房间可售状态]
     │
     ▼ (房态冲突时提示错误，需重新选房)
     │
     ▼
[系统生成入住单 (Registration Card)]
     │
     ▼
[房态更新：可售 → 入住中]
     │
     ▼
[制作房卡，完成入住]
```

- **前置条件**：客人已到达前台，接待员已登录系统。
- **后置条件**：系统生成有效入住记录，房态变更为"入住中"。
- **异常流程**：所选房间在提交瞬间被其他接待员占用 → 系统提示冲突并回滚，要求重新选择房间。

### 3.2 退房流程 (Check-out)

```
[客人离店]
     │
     ▼
[接待员根据房号/姓名查询在住记录]
     │
     ▼
[核对消费明细：房费 + 杂费 (迷你吧、洗衣等)]
     │
     ▼
[处理账务]
   ├── 正常结账 → 打印账单，收取余款/退还押金
   ├── 账务冲销 → 领班及以上权限审核后冲销 (Supervisor+)
   └── 折扣处理 → 经理审批折扣率 (Manager+)
     │
     ▼
[系统生成退房账单]
     │
     ▼
[房态更新：入住中 → 脏房 (待清洁)]
     │
     ▼
[完成退房]
```

- **前置条件**：该房间状态为"入住中"。
- **后置条件**：订单状态变更为"已离店"，房态变更为"脏房"。
- **异常流程**：消费争议 → 进入账务冲销审批流程。

### 3.3 房态变更流程

```
[触发事件]
   ├── 入住完成    → 可售      → 入住中
   ├── 退房完成    → 入住中    → 脏房
   ├── 清洁完成    → 脏房      → 可售
   ├── 维修申请    → 可售/脏房 → 维修中
   ├── 维修完成    → 维修中    → 脏房
   └── 手动变更    → (领班+权限手工修改)
```

- **房态流转约束**：不允许跳跃式变更（如从"入住中"直接变为"可售"），必须经由"脏房"状态。
- **并发安全**：房态变更为高并发操作，需通过分布式锁或乐观锁保证同一房间不会被重复入住。

---

## 四、RESTful API 接口定义（v1.0）

### 4.1 认证与权限 API

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| `POST` | `/api/v1/auth/login` | 用户登录，返回 JWT Token | 公开 |
| `POST` | `/api/v1/auth/logout` | 用户登出，失效 Token | 已认证用户 |
| `GET` | `/api/v1/auth/me` | 获取当前登录用户信息及权限 | 已认证用户 |

**登录请求**：
```json
POST /api/v1/auth/login
{
  "username": "zhangsan",
  "password": "******"
}
```

**登录响应**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "tokenType": "Bearer",
    "expiresIn": 7200,
    "user": {
      "id": 10,
      "username": "zhangsan",
      "realName": "张三",
      "role": "receptionist"
    }
  }
}
```

### 4.2 入住管理 API

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| `POST` | `/api/v1/checkins` | 办理入住 | Receptionist+ |
| `GET` | `/api/v1/checkins` | 查询入住记录列表 | Receptionist+ |
| `GET` | `/api/v1/checkins/{id}` | 获取入住详情 | Receptionist+ |
| `PUT` | `/api/v1/checkins/{id}` | 修改入住信息（换房/延住） | Receptionist+ |

**办理入住请求**：
```json
POST /api/v1/checkins
{
  "guestId": 1001,
  "roomId": 201,
  "roomTypeId": 1,
  "checkInDate": "2026-04-29",
  "expectedCheckOutDate": "2026-05-01",
  "guestCount": 2,
  "depositAmount": 500.00,
  "source": "walk_in",
  "bookingNumber": "BK202604280001"
}
```

> `operatorId` 由后端从 JWT Token 中自动解析，客户端无需传入。`source` 表示订单来源，可选值：`walk_in` / `phone` / `front_desk` / `manager` / `online_direct` / `online_ota` / `contract`。

**响应**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "registrationId": 50001,
    "roomNumber": "1208",
    "status": "in_house",
    "createdAt": "2026-04-29T14:30:00"
  }
}
```

### 4.3 退房管理 API

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| `POST` | `/api/v1/checkouts` | 办理退房 | Receptionist+ |
| `GET` | `/api/v1/checkouts` | 查询退房历史 | Receptionist+ |
| `POST` | `/api/v1/checkouts/calculate-refund` | 退费试算 | Receptionist+ |

**退房请求**：
```json
POST /api/v1/checkouts
{
  "registrationId": 50001,
  "additionalCharges": [
    { "item": "mini_bar", "amount": 88.00 },
    { "item": "laundry", "amount": 50.00 }
  ]
}
```

### 4.4 房态管理 API

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| `GET` | `/api/v1/rooms/status-map` | 获取所有房间实时房态图 | Receptionist+ |
| `GET` | `/api/v1/rooms/{id}` | 获取单个房间详情 | Receptionist+ |
| `GET` | `/api/v1/rooms/available` | 批量获取可售房间 | Receptionist+ |

### 4.5 客人管理 API

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| `GET` | `/api/v1/guests` | 查询客人信息 | Receptionist+ |
| `GET` | `/api/v1/guests/{id}` | 获取客人详情 | Receptionist+ |
| `POST` | `/api/v1/guests` | 新建客人档案 | Receptionist+ |
| `PUT` | `/api/v1/guests/{id}` | 更新客人信息 | Receptionist+ |
| `POST` | `/api/v1/guests/import-identity` | 导入客人身份信息 | Receptionist+ |

### 4.6 押金管理 API

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| `POST` | `/api/v1/deposits` | 创建押金记录 | Receptionist+ |
| `GET` | `/api/v1/deposits?registrationId={id}` | 查询押金记录列表 | Receptionist+ |
| `POST` | `/api/v1/deposits/{id}/refund` | 退押金 | Receptionist+ |

**创建押金记录请求**：
```json
POST /api/v1/deposits
{
  "registrationId": 50001,
  "paymentMethod": "bank_card",
  "amount": 500.00,
  "slipNumber": "6222********1234",
  "transactionNo": "TXN202604290001",
  "authCode": "AUTH20260429001"
}
```

> 不同支付方式提交的字段不同：`cash` 仅传 `amount`；`wechat` / `alipay` 传 `slipNumber` + `transactionNo`；`bank_card` 传 `slipNumber` + `transactionNo` + `authCode`。

**查询押金记录响应**：
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "paymentMethod": "cash",
      "amount": 200.00,
      "status": "active",
      "createdAt": "2026-04-29T14:30:00"
    },
    {
      "id": 2,
      "paymentMethod": "bank_card",
      "amount": 300.00,
      "slipNumber": "6222********1234",
      "transactionNo": "TXN202604290001",
      "authCode": "AUTH20260429001",
      "status": "active",
      "createdAt": "2026-04-29T14:35:00"
    }
  ]
}
```

---

## 五、数据库核心表结构（v1.0）

### 5.1 用户与权限体系

**客人表 — `guest`**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT PK | 客人 ID |
| name | VARCHAR(50) | 客人姓名 |
| id_type | VARCHAR(20) | 证件类型：identity / passport / other |
| id_number | VARCHAR(50) | 证件号 |
| phone | VARCHAR(20) | 联系电话 |
| address | VARCHAR(255) | 地址 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

**用户表 — `sys_user`**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT PK | 用户 ID |
| username | VARCHAR(50) UNIQUE | 登录用户名 |
| password_hash | VARCHAR(255) | 密码哈希 |
| real_name | VARCHAR(50) | 真实姓名 |
| role_id | INT FK | 关联角色表 |
| status | TINYINT | 状态：1-启用，0-停用 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

**角色表 — `sys_role`**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | INT PK | 角色 ID |
| role_name | VARCHAR(50) | 角色名称 |
| role_key | VARCHAR(50) UNIQUE | 角色标识 (receptionist/supervisor/manager/admin) |
| description | VARCHAR(255) | 角色描述 |
| created_at | DATETIME | 创建时间 |

**角色-权限关联表 — `sys_role_permission`**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT PK | 自增 ID |
| role_id | INT FK | 角色 ID |
| permission_code | VARCHAR(100) | 权限代码 (如 `checkin:create`) |
| created_at | DATETIME | 创建时间 |

> **命名规范**：数据库字段使用 `snake_case`（如 `check_in_date`），API 请求/响应使用 `camelCase`（如 `checkInDate`），由 MyBatis-Plus 配置自动映射。

### 5.2 客房体系

**房型表 — `room_type`**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT PK | 房型 ID |
| type_name | VARCHAR(50) | 房型名称 (如标准大床房、豪华套房) |
| base_price | DECIMAL(10,2) | 标准价格 |
| max_guests | INT | 最大入住人数 |
| bed_type | VARCHAR(50) | 床型 (大床/双床) |
| area | DECIMAL(6,2) | 房间面积 (m²) |
| description | TEXT | 房型描述 |
| status | TINYINT | 状态：1-在售，0-停售 |
| created_at | DATETIME | 创建时间 |

**客房表 — `room`**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT PK | 房间 ID |
| room_number | VARCHAR(10) UNIQUE | 房间号 (如 1208) |
| floor | INT | 楼层 |
| room_type_id | BIGINT FK | 关联房型 |
| status | VARCHAR(20) | 房态：available / occupied / dirty / maintenance |
| version | INT | 乐观锁版本号，用于并发控制 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

### 5.3 订单与入住

**订单表 — `reservation`**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT PK | 订单 ID |
| booking_number | VARCHAR(50) UNIQUE | 预订编号 |
| guest_id | BIGINT FK | 客人 ID |
| room_type_id | BIGINT FK | 预订房型 |
| check_in_date | DATE | 预订入住日期 |
| check_out_date | DATE | 预订离店日期 |
| status | VARCHAR(20) | 状态：pending / confirmed / checked_in / checked_out / cancelled |
| source | VARCHAR(20) | 订单来源：walk_in / phone / front_desk / manager / online_direct / online_ota / contract |
| deposit_amount | DECIMAL(10,2) | 押金金额 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

**入住记录表 — `registration`**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT PK | 入住单号 |
| reservation_id | BIGINT FK | 关联订单 ID (散客可为 NULL) |
| guest_id | BIGINT FK | 客人 ID |
| room_id | BIGINT FK | 实际入住房间 ID |
| check_in_time | DATETIME | 实际入住时间 |
| expected_check_out_time | DATETIME | 预计离店时间 |
| actual_check_out_time | DATETIME | 实际离店时间 |
| guest_count | INT | 同住人数 |
| deposit_amount | DECIMAL(10,2) | 押金总金额（汇总 deposit 表，冗余字段便于快速展示） |
| operator_id | BIGINT FK | 办理人 (关联 sys_user) |
| source | VARCHAR(20) | 入住来源：关联 reservation.source；散客入住直接录入 |
| status | VARCHAR(20) | 状态：in_house / checked_out |
| created_at | DATETIME | 创建时间 |

### 5.4 押金记录

**账务流水表 — `transaction_log`**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT PK | 流水 ID |
| registration_id | BIGINT FK | 关联入住记录 |
| transaction_type | VARCHAR(50) | 类型：room_charge / deposit / refund / additional / reversal |
| amount | DECIMAL(10,2) | 金额 (正为收入，负为支出) |
| balance_before | DECIMAL(10,2) | 操作前余额 |
| balance_after | DECIMAL(10,2) | 操作后余额 |
| description | VARCHAR(255) | 描述 |
| operator_id | BIGINT FK | 操作人 |
| reversal_of | BIGINT FK | 被冲销的流水 ID (冲销时关联) |
| created_at | DATETIME | 创建时间 |

**押金记录表 — `deposit`**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT PK | 押金单 ID |
| registration_id | BIGINT FK | 关联入住记录 |
| payment_method | VARCHAR(20) | 支付方式：cash / wechat / alipay / bank_card |
| amount | DECIMAL(10,2) | 押金金额 |
| slip_number | VARCHAR(100) | 押金单号：微信/支付宝为支付平台单号，银行卡为银行卡号 |
| transaction_no | VARCHAR(100) | 交易流水号 |
| auth_code | VARCHAR(100) | 授权码（仅银行卡预授权时填写） |
| status | VARCHAR(20) | 状态：active / refunded |
| operator_id | BIGINT FK | 操作人（关联 sys_user） |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

> **支付方式字段说明**：`cash` 现金支付仅需录入金额；`wechat` / `alipay` 需填写押金单号（平台单号）和交易流水号；`bank_card` 需填写银行卡号（作为押金单号）、交易流水号和授权码。前端根据 `payment_method` 动态展示对应输入字段。

### 5.6 ER 关系简图

```
sys_user ──N:1── sys_role ──1:N── sys_role_permission
                                    │
  room_type ──1:N── room ──1:N── registration ──N:1── guest
                  │              │        │
                  │              │        └──1:N── deposit
                  │              │
                  │              └──1:N── transaction_log
                  │
             reservation ──N:1── guest
```

---

## 六、v1.0 MVP 范围与功能清单

### 6.1 MVP 目标

| 版本 | 代号 | 目标 |
|------|------|------|
| **v1.0 MVP** | Check-in | 实现前台核心操作闭环：入住 → 房态 → 退房 |

### 6.2 MVP 功能范围

| 模块 | MVP 包含功能 | 说明 |
|------|-------------|------|
| 入住管理 | 办理入住、查询入住记录、获取入住详情 | 散客入住 + 预订入住 |
| 退房管理 | 办理退房、退费、查询退房历史 | 不含账务冲销和折扣审批 |
| 客人管理 | 查询客人、新建客人档案、导入身份信息 | 支持身份证识别导入 |
| 房态管理 | 查看房态图、查看房间详情、批量查看可售房 | 不含手动修改房态 |
| 认证与权限 | 登录/登出、基于角色的菜单路由 | 硬编码 4 角色，不开放权限配置界面 |
| 基础框架 | RESTful API 基础设施、JWT 认证、Spring Security 权限链路 | 服务端 + 前端基础建设 |

> v1.0 只覆盖 **Receptionist** 角色的全流程闭环操作，Supervisor / Manager / Admin 的高级功能延后到后续版本。

### 6.3 模块功能清单

#### 入住管理模块

| 功能 | 优先级 | 说明 |
|------|--------|------|
| 预订号查询并办理入住 | **P0** | 通过预订关联快速入住 |
| 散客直接选房入住 | **P0** | 选择房型 → 筛选可售房间 → 办理 |
| 入住人信息录入 | **P0** | 姓名、证件号、联系方式等 |
| 押金收取 | **P0** | 支持现金/微信/支付宝/银行卡等多种支付方式录入 |
| 押金记录管理 | **P1** | 查看押金明细、按支付方式筛选、退押金操作 |
| 入住单生成与打印 | **P1** | 生成登记卡，支持打印 |
| 在住客人列表查询 | **P0** | 按房号/姓名搜索在住记录 |
| 入住信息修改 | **P1** | 换房、延住、修改同住人 |
| 历史入住记录查询 | **P1** | 按日期/客人/房间维度检索 |

#### 退房管理模块

| 功能 | 优先级 | 说明 |
|------|--------|------|
| 在住记录查询并办理退房 | **P0** | 按房号/姓名检索 |
| 房费自动计算 | **P0** | 根据入住天数 × 房价自动计算 |
| 杂费录入（迷你吧/洗衣等） | **P1** | 退房时补充消费项 |
| 押金退还计算 | **P0** | 应付 - 实付 = 应退/应补 |
| 退房账单打印 | **P1** | 打印明细账单 |
| 提前退房/续住处理 | **P1** | 按实际入住天数重新计费 |

#### 房态管理模块

| 功能 | 优先级 | 说明 |
|------|--------|------|
| 房态图展示 | **P0** | 可视化看板，按颜色标识房态 |
| 按楼层/房型筛选房态 | **P0** | 多维度过滤 |
| 单个房间详情查看 | **P0** | 当前住客、入住时间、历史记录 |
| 可售房间检索 | **P0** | 按房型/时间段筛选可用房间 |
| 房态自动刷新 | **P1** | 状态变更后自动刷新视图 |

#### 客人管理模块

| 功能 | 优先级 | 说明 |
|------|--------|------|
| 客人信息查询 | **P0** | 按姓名/证件号/手机号搜索 |
| 新建客人档案 | **P0** | 录入基本信息 |
| 身份证信息导入 | **P1** | 通过身份证读卡器或手动录入 |
| 客人历史入住记录 | **P1** | 查看该客人的所有住店记录 |
| 客人信息修改 | **P1** | 更新联系方式、证件等 |

#### 认证与权限模块

| 功能 | 优先级 | 说明 |
|------|--------|------|
| 用户名密码登录 | **P0** | JWT token 发放 |
| Token 校验与自动续期 | **P0** | Spring Security Filter Chain 验证 |
| 登出 | **P0** | Token 失效处理 |
| 基于角色的菜单路由 | **P0** | 客户端根据角色展示不同菜单 |
| 服务端 RBAC 拦截器 | **P0** | 每个 API 端点强制校验权限 |

---

## 七、技术难点分析（v1.0 相关）

### 7.1 RESTful API 接口版本管理

**难点**：前后端独立部署迭代，接口变更可能导致前端解析失败。

**应对**：
- URL 路径版本化（`/api/v1/`、`/api/v2/`）
- 字段兼容策略：新增不删除旧字段
- Swagger/OpenAPI 文档同步
- 废弃通知：标记 `deprecated` + 响应头 `X-API-Deprecated: true`

### 7.2 房态并发控制

**难点**：多位接待员同时操作可能导致同一房间"超卖"。

**应对**：
- **Redis 分布式锁**：`room:{room_id}:lock` key 确保同一时刻只有一个线程操作该房间
- **乐观锁**：`UPDATE room SET status='occupied', version=version+1 WHERE id=? AND version=?`
- **双重校验**：业务层先检查，持久化层通过数据库约束兜底

### 7.3 RBAC 权限拦截器

**设计决策**：权限校验必须在 **后端** 拦截，前端仅做 UI 辅助。

```
前端请求 → HTTP 网络传输 → 后端收到
                             │
                             ▼
                    [Spring Security Filter Chain]
                      ├── JWT 认证过滤器 (校验 Token)
                      ├── RBAC 权限过滤器 (校验角色)
                      └── 异常处理过滤器 (403 响应)
                             │
                             ▼
                      [Controller 业务处理]
```

### 7.4 房态与订单的状态机设计

**难点**：房间状态、订单状态、入住记录状态三者联动复杂。

**房态状态机**：
```
available ──(入住)──→ occupied ──(退房)──→ dirty ──(清洁)──→ available
     │                                        │
     └──(维修)──→ maintenance ──(修复)──→ dirty
```

**订单状态机**：
```
pending ──(确认)──→ confirmed ──(入住)──→ checked_in ──(退房)──→ checked_out
     │                   │
     └──(取消)──→ cancelled     └──(取消)──→ cancelled
```

- 状态转换采用**硬编码状态机**，禁止任意跳转
- 每次状态变更记录 `status_change_log` 表
- 客户端请求必须附带当前状态，服务端校验转换路径合法性

---

## 附录 A：v1.0 权限代码字典

| 权限代码 | 说明 | 最低角色 |
|----------|------|----------|
| `checkin:create` | 办理入住 | Receptionist |
| `checkin:query` | 查询入住记录 | Receptionist |
| `checkin:update` | 修改入住信息 | Receptionist |
| `checkout:create` | 办理退房 | Receptionist |
| `checkout:refund` | 退费操作 | Receptionist |
| `guest:query` | 查询客人信息 | Receptionist |
| `guest:create` | 新建客人档案 | Receptionist |
| `guest:import` | 导入客人身份信息 | Receptionist |
| `room:view` | 查看房态 | Receptionist |
| `deposit:create` | 创建押金记录 | Receptionist |
| `deposit:refund` | 退押金 | Receptionist |
