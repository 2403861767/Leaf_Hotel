# 酒店前台管理系统 — 产品需求文档 (PRD)

## 版本历史

| 版本 | 日期 | 作者 | 变更说明 |
|------|------|------|----------|
| v2.0 | 2026-04-29 | SeeLeaf | 全面修订：改为 RESTful + 前后端分离架构，Java + Vue3 技术栈 |

> 注：本文档版本号（v2.0）代表 PRD 文档的修订版本，与后文产品路线图中的产品版本（v1.0 ~ v5.0+）相互独立。

---

## 一、产品概述

### 1.1 项目背景

传统酒店前台系统多采用单体架构，各模块耦合严重，难以适应酒店业务快速变化的需求。本系统旨在通过 **RESTful API + 前后端分离**架构，实现酒店前台模块的服务化拆分，为 Web 端提供统一的 HTTP 接口。

### 1.2 核心定位

- **范围限定**：仅关注"酒店前台"业务域，暂不覆盖客房部的清扫排班、财务部的总账核算、工程部的维修管理等部门职能。
- **目标用户**：酒店前台工作人员，包括前台接待员、领班、经理及系统管理员。
- **系统边界**：负责从客人抵店办理入住到离店结账的全生命周期管理，涵盖客房预订、入住登记、房态管理、账务处理等前台核心环节。

### 1.3 架构模式 — RESTful + 前后端分离

系统采用前后端分离的 **B/S 架构**，前端通过 HTTP 请求调用后端 RESTful API：

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

- **前端 (Frontend)**：基于 Vue3 构建的单页应用 (SPA)，负责界面渲染与用户交互，不含业务逻辑。通过 Axios 等 HTTP 客户端调用后端 API。
- **后端 (Backend)**：基于 Java Spring Boot 构建的服务端，按业务域拆分为多个 Controller / Service 模块。负责核心业务逻辑、数据持久化及权限校验。
- **通信协议**：HTTP/1.1 + JSON。接口设计遵循 RESTful 风格，使用名词复数作为资源路径（如 `/api/checkins`），通过 HTTP 方法表达操作语义。
- **接口文档**：使用 Swagger / OpenAPI 3.0 自动生成接口文档，作为前后端协作的契约。

### 1.4 技术选型

#### 1.4.1 总体选型

| 层级 | 选型方案 | 版本 | 选型理由 |
|------|----------|------|----------|
| 前端框架 | **Vue3** | 3.5+ | 核心技术栈，组件化开发，Composition API 灵活组合 |
| 前端构建 | **Vite** | 6.x | 原生 ESM，开发热更新快，构建体积小 |
| 前端 UI 组件 | **Element Plus** | 4.x | 成熟的企业级组件库，表格/表单/弹窗等开箱即用 |
| 前端状态管理 | **Pinia** | 2.x | Vue3 官方推荐状态管理，轻量且支持 TypeScript |
| HTTP 客户端 | **Axios** | 1.x | 请求/响应拦截器，方便注入 Token 与统一错误处理 |
| 后端框架 | **Spring Boot** | 3.x | 核心技术栈，生态完善，RESTful 原生支持 |
| ORM | **MyBatis-Plus** | 3.x | 灵活对接 SQL，代码生成器提升效率 |
| API 文档 | **Swagger (SpringDoc)** | OpenAPI 3.0 | 自动生成接口文档，前后端联调契约 |
| 数据库 | **MySQL** | 8.0+ | 关系型存储，事务支持好，适合订单/账务场景 |
| 缓存/锁 | **Redis** | 7.x | 房态缓存加速 + 分布式锁保证并发安全 |
| 认证机制 | **JWT (access token)** | — | 无状态认证，适合前后端分离架构 |
| 权限框架 | **Spring Security** | 6.x | 内置 RBAC 支持，Filter Chain 可灵活配置拦截规则 |

#### 1.4.2 为什么选 Spring Boot + MyBatis-Plus 而非其他

| 对比项 | Spring Boot + MyBatis-Plus | Golang Gin + GORM |
|--------|---------------------------|-------------------|
| 团队熟悉度 | ✅ 核心技术栈，上手快 | ❌ 需要学习新语言 |
| RESTful 生态 | Spring Web 原生支持 REST | Gin 生态相对轻量 |
| 权限框架 | Spring Security 开箱即用 | 需自行实现或选第三方 |
| 事务管理 | `@Transactional` 声明式事务 | 需手动管理 |
| 社区文档 | 中文文档、社区资源极为丰富 | 文档相对较少 |

> 基于核心技术栈的延续性和团队熟悉度，选择 Java Spring Boot + MyBatis-Plus 作为后端技术方案。

#### 1.4.3 为什么选 Vue3 + Element Plus 而非其他

| 对比项 | Vue3 + Element Plus | React + Ant Design |
|--------|---------------------|--------------------|
| 团队熟悉度 | ✅ 核心技术栈 | ❌ 需切换技术栈 |
| 组件库 | Element Plus 适配 Vue3，成熟稳定 | Ant Design 同样成熟但生态不同 |
| 学习曲线 | Composition API 直观易学 | Hooks 规则较多 |
| 构建工具 | Vite 原生支持 Vue3，开发体验好 | Vite + React 同样可用但生态重心不同 |

> 延续团队在 Vue 生态的技术积累，选择 Vue3 + Element Plus。

#### 1.4.4 接口设计风格

本系统全部接口遵循 **RESTful** 设计规范：

| 方法 | 资源操作 | 示例 |
|------|----------|------|
| `GET` | 查询资源 | `GET /api/rooms?status=available` |
| `POST` | 创建资源 | `POST /api/checkins` |
| `PUT` | 更新资源 | `PUT /api/checkins/{id}` |
| `DELETE` | 删除资源 | `DELETE /api/users/{id}` |

- 统一响应格式：`{ "code": 200, "message": "success", "data": {...} }`
- 分页请求：`/api/checkins?page=1&pageSize=20`
- 认证方式：请求头 `Authorization: Bearer <token>`

---

## 二、角色权限定义矩阵

### 2.1 角色定义

| 角色 | 英文标识 | 描述 |
|------|----------|------|
| 前台接待 | `Receptionist` | 一线操作人员，负责日常接待工作 |
| 前台领班 | `Supervisor` | 在接待权限基础上，增加账务管理能力 |
| 前台经理 | `Manager` | 在领班权限基础上，增加定价与人员管理能力 |
| 系统管理员 | `Admin` | 系统最高权限，可操作用户与日志 |

### 2.2 权限矩阵

| 业务模块 | 操作 | Receptionist | Supervisor | Manager | Admin |
|----------|------|:------------:|:----------:|:-------:|:-----:|
| **入住管理** | 办理入住 (Check-in) | ✅ | ✅ | ✅ | ✅ |
| | 查询入住记录 | ✅ | ✅ | ✅ | ✅ |
| | 修改入住信息 | ✅ | ✅ | ✅ | ✅ |
| **退房管理** | 办理退房 (Check-out) | ✅ | ✅ | ✅ | ✅ |
| | 退费操作 | ✅ | ✅ | ✅ | ✅ |
| | 退房历史查询 | ✅ | ✅ | ✅ | ✅ |
| **客人管理** | 查询客人信息 | ✅ | ✅ | ✅ | ✅ |
| | 导入客人信息 | ✅ | ✅ | ✅ | ✅ |
| | 修改客人信息 | ✅ | ✅ | ✅ | ✅ |
| **房态管理** | 查看房态图 | ✅ | ✅ | ✅ | ✅ |
| | 查看房间详情 | ✅ | ✅ | ✅ | ✅ |
| | 手动修改房态 | — | ✅ | ✅ | ✅ |
| **账务管理** | 查看账单明细 | — | ✅ | ✅ | ✅ |
| | 账务冲销 | — | ✅ | ✅ | ✅ |
| | 折扣授权 | — | ✅ | ✅ | ✅ |
| | 报表查看 | — | ✅ | ✅ | ✅ |
| **房型价格** | 查看价格 | — | ✅ | ✅ | ✅ |
| | 调整价格 | — | — | ✅ | ✅ |
| **员工管理** | 查看排班 | — | — | ✅ | ✅ |
| | 编辑排班 | — | — | ✅ | ✅ |
| **VIP 审批** | VIP 权限审批 | — | — | ✅ | ✅ |
| **系统管理** | 用户管理 | — | — | — | ✅ |
| | 角色权限配置 | — | — | — | ✅ |
| | 系统日志查看 | — | — | — | ✅ |

> 说明：✅ 表示具有该操作权限；— 表示不具有。

---

## 三、核心业务流程图（文字描述）

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

## 四、RESTful API 接口定义预览

以下接口遵循 RESTful 风格设计，使用 JSON 作为数据交换格式。

- **基础路径**：`/api/v1`
- **认证方式**：`Authorization: Bearer <token>`
- **统一响应格式**：

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

- **分页响应格式**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [...],
    "total": 100,
    "page": 1,
    "pageSize": 20
  }
}
```

### 4.1 入住管理 API

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| `POST` | `/api/v1/checkins` | 办理入住 | Receptionist+ |
| `GET` | `/api/v1/checkins` | 查询入住记录列表 | Receptionist+ |
| `GET` | `/api/v1/checkins/{id}` | 获取入住详情 | Receptionist+ |
| `PUT` | `/api/v1/checkins/{id}` | 修改入住信息（换房/延住） | Receptionist+ |

**请求示例 — 办理入住**：

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
  "bookingNumber": "BK202604280001"
}
```

> 说明：`operatorId` 由后端从 JWT Token 中自动解析，客户端无需传入。

**响应示例**：

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

### 4.2 退房管理 API

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| `POST` | `/api/v1/checkouts` | 办理退房 | Receptionist+ |
| `GET` | `/api/v1/checkouts` | 查询退房历史 | Receptionist+ |
| `POST` | `/api/v1/checkouts/calculate-refund` | 退费试算 | Receptionist+ |

**请求示例 — 办理退房**：

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

### 4.3 房态管理 API

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| `GET` | `/api/v1/rooms/status-map` | 获取所有房间实时房态图 | Receptionist+ |
| `GET` | `/api/v1/rooms/{id}` | 获取单个房间详情 | Receptionist+ |
| `GET` | `/api/v1/rooms/available` | 批量获取可售房间 | Receptionist+ |
| `PUT` | `/api/v1/rooms/{id}/status` | 手动修改房态 | Supervisor+ |

### 4.4 客人管理 API

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| `GET` | `/api/v1/guests` | 查询客人信息 | Receptionist+ |
| `GET` | `/api/v1/guests/{id}` | 获取客人详情 | Receptionist+ |
| `POST` | `/api/v1/guests` | 新建客人档案 | Receptionist+ |
| `PUT` | `/api/v1/guests/{id}` | 更新客人信息 | Receptionist+ |
| `POST` | `/api/v1/guests/import-identity` | 导入客人身份信息 | Receptionist+ |

### 4.5 账务管理 API

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| `GET` | `/api/v1/finance/bills/{registrationId}` | 查看账单明细 | Supervisor+ |
| `POST` | `/api/v1/finance/reverse` | 账务冲销 | Supervisor+ |
| `POST` | `/api/v1/finance/discount-approval` | 折扣授权审批 | Manager+ |
| `GET` | `/api/v1/finance/reports` | 报表查看 | Supervisor+ |

### 4.6 认证与权限 API

> 认证 API 属于系统基础能力，在 v1.0 MVP 阶段即需完成。

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| `POST` | `/api/v1/auth/login` | 用户登录，返回 JWT Token | 公开 |
| `POST` | `/api/v1/auth/logout` | 用户登出，失效 Token | 已认证用户 |
| `GET` | `/api/v1/auth/me` | 获取当前登录用户信息及权限 | 已认证用户 |

**请求示例 — 登录**：

```json
POST /api/v1/auth/login
{
  "username": "zhangsan",
  "password": "******"
}
```

**响应示例**：

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

### 4.7 系统管理 API

> ⚠️ 该模块为 v4.0 范围，仅 Admin 角色可访问。

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| `POST` | `/api/v1/users` | 创建用户 | Admin |
| `PUT` | `/api/v1/users/{id}` | 更新用户 | Admin |
| `DELETE` | `/api/v1/users/{id}` | 停用用户 | Admin |
| `GET` | `/api/v1/roles` | 获取角色列表 | Admin |
| `PUT` | `/api/v1/roles/permissions` | 更新角色权限 | Admin |
| `GET` | `/api/v1/logs` | 查看系统日志 | Admin |

### 4.8 通用参数说明

| 参数 | 位置 | 格式 | 说明 |
|------|------|------|------|
| `page` | Query | 整数 | 页码，从 1 开始 |
| `pageSize` | Query | 整数 | 每页条数 |
| `Authorization` | Header | `Bearer <token>` | JWT 认证令牌 |
| 时间字段 | Body | ISO 8601 (`yyyy-MM-dd'T'HH:mm:ss`) | 统一时间格式 |

---

## 五、数据库核心表结构设计

### 5.1 用户与权限体系

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

> **命名规范说明**：数据库表字段使用 `snake_case`（如 `check_in_date`），API 请求/响应使用 `camelCase`（如 `checkInDate`），由 MyBatis-Plus 配置自动映射或通过 Spring Jackson 配置进行转换。

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
| deposit_amount | DECIMAL(10,2) | 押金金额 |
| operator_id | BIGINT FK | 办理人 (关联 sys_user) |
| status | VARCHAR(20) | 状态：in_house / checked_out |
| created_at | DATETIME | 创建时间 |

### 5.4 账务流水

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

### 5.5 ER 关系简图

```
sys_user ──N:1── sys_role ──1:N── sys_role_permission
                                    │
  room_type ──1:N── room ──1:N── registration ──N:1── guest
                  │              │
                  │              └──1:N── transaction_log
                  │
             reservation ──N:1── guest
```

---

## 六、MVP 范围与迭代路线图

### 6.1 版本规划

| 版本 | 代号 | 目标 |
|------|------|------|
| **v1.0 MVP** | Check-in | 实现前台核心操作闭环：入住 → 房态 → 退房 |
| **v2.0** | Billing | 增加账务管理与报表能力 |
| **v3.0** | Management | 增加管理决策功能（定价、排班、VIP 审批） |
| **v4.0** | Admin | 增加系统管理功能（用户、日志、角色配置） |
| **v5.0+** | — | 扩展其他部门模块、与 PMS 第三方系统对接 |

### 6.2 v1.0 MVP 功能范围

| 模块 | MVP 包含功能 | 说明 |
|------|-------------|------|
| 入住管理 | 办理入住、查询入住记录、获取入住详情 | 散客入住 + 预订入住 |
| 退房管理 | 办理退房、退费、查询退房历史 | 不含账务冲销和折扣审批 |
| 客人管理 | 查询客人、新建客人档案、导入身份信息 | 支持身份证识别导入 |
| 房态管理 | 查看房态图、查看房间详情、批量查看可售房 | 不含手动修改房态 |
| 认证与权限 | 登录/登出、基于角色的菜单路由 | 硬编码 4 角色，不开放权限配置界面 |
| 基础框架 | RESTful API 基础设施、JWT 认证、Spring Security 权限链路 | 服务端 + 前端基础建设 |

> v1.0 只覆盖 Receptionist 角色的全流程操作，Supervisor / Manager / Admin 的高级功能延后到后续版本。

### 6.3 v2.0 范围（账务管理）

- 账务冲销（Supervisor+）
- 折扣授权审批流程（Manager+）
- 日结报表 / 营业报表查看（Supervisor+）
- 手动修改房态（Supervisor+）

### 6.4 v3.0 范围（管理决策）

- 房型价格调整（Manager+）
- 员工排班查看与编辑（Manager+）
- VIP 权限审批流程（Manager+）

### 6.5 v4.0 范围（系统管理）

- 用户创建/编辑/停用（Admin）
- 角色权限配置管理界面（Admin）
- 系统操作日志查看（Admin）

---

## 七、模块划分与功能清单

以下按业务模块进行拆分，每个模块标注**核心功能（MVP）**与**后续迭代（Todo）**。

### 7.1 入住管理模块 (Check-in Management)

**子模块划分**：

| 子模块 | 说明 |
|--------|------|
| 预订入住 | 通过预订号查找订单，快速转为入住 |
| 散客开房 | 无预订直接选房办理入住 |
| 入住记录管理 | 查询、修改、在住客人列表 |

**功能清单**：

| 功能 | 优先级 | 分类 | 说明 |
|------|--------|------|------|
| 预订号查询并办理入住 | P0 | 核心功能 | 通过预订关联快速入住 |
| 散客直接选房入住 | P0 | 核心功能 | 选择房型 → 筛选可售房间 → 办理 |
| 入住人信息录入 | P0 | 核心功能 | 姓名、证件号、联系方式等 |
| 押金收取 | P0 | 核心功能 | 录入押金金额 |
| 入住单生成与打印 | P1 | 核心功能 | 生成登记卡，支持打印 |
| 在住客人列表查询 | P0 | 核心功能 | 按房号/姓名搜索在住记录 |
| 入住信息修改 | P1 | 核心功能 | 换房、延住、修改同住人 |
| 批量入住 | P2 | Todo | 团队/旅行团批量办理 |
| 历史入住记录查询 | P1 | 核心功能 | 按日期/客人/房间维度检索 |

### 7.2 退房管理模块 (Check-out Management)

**子模块划分**：

| 子模块 | 说明 |
|--------|------|
| 退房结账 | 消费核对、结账、退押金 |
| 退房历史 | 历史退房记录查询 |

**功能清单**：

| 功能 | 优先级 | 分类 | 说明 |
|------|--------|------|------|
| 在住记录查询并办理退房 | P0 | 核心功能 | 按房号/姓名检索 |
| 房费自动计算 | P0 | 核心功能 | 根据入住天数 × 房价自动计算 |
| 杂费录入（迷你吧/洗衣等） | P1 | 核心功能 | 退房时补充消费项 |
| 押金退还计算 | P0 | 核心功能 | 应付 - 实付 = 应退/应补 |
| 退房账单打印 | P1 | 核心功能 | 打印明细账单 |
| 提前退房/续住处理 | P1 | 核心功能 | 按实际入住天数重新计费 |
| 批量退房 | P2 | Todo | 团队批量退房 |

### 7.3 房态管理模块 (Room Status Management)

**子模块划分**：

| 子模块 | 说明 |
|--------|------|
| 房态总览 | 图形化房态分布 |
| 房间详情 | 单个房间的完整信息 |
| 可售查询 | 筛选可用房间 |

**功能清单**：

| 功能 | 优先级 | 分类 | 说明 |
|------|--------|------|------|
| 房态图展示 | P0 | 核心功能 | 可视化看板，按颜色标识房态 |
| 按楼层/房型筛选房态 | P0 | 核心功能 | 多维度过滤 |
| 单个房间详情查看 | P0 | 核心功能 | 当前住客、入住时间、历史记录 |
| 可售房间检索 | P0 | 核心功能 | 按房型/时间段筛选可用房间 |
| 房态手动修改 | P2 | Todo (v2.0) | Supervisor 以上权限手动改房态 |
| 维修房间标记 | P2 | Todo (v2.0) | 标记为维修中 → 不再出现在可售列表 |
| 房态自动刷新 | P1 | 核心功能 | 状态变更后自动刷新视图 |
| 房间清洁进度看板 | P3 | Todo (v2.0) | 与客房部联动 |

### 7.4 客人管理模块 (Guest Management)

**子模块划分**：

| 子模块 | 说明 |
|--------|------|
| 客人档案 | 客人的完整信息管理 |
| 身份导入 | 身份证/护照信息自动识别 |

**功能清单**：

| 功能 | 优先级 | 分类 | 说明 |
|------|--------|------|------|
| 客人信息查询 | P0 | 核心功能 | 按姓名/证件号/手机号搜索 |
| 新建客人档案 | P0 | 核心功能 | 录入基本信息 |
| 身份证信息导入 | P1 | 核心功能 | 通过身份证读卡器或手动录入 |
| 客人历史入住记录 | P1 | 核心功能 | 查看该客人的所有住店记录 |
| 客人信息修改 | P1 | 核心功能 | 更新联系方式、证件等 |
| 黑名单管理 | P3 | Todo | 标记不良住客 |
| 会员/常客计划 | P3 | Todo | 积分累计与兑换 |

### 7.5 账务管理模块 (Finance Management)

**功能清单**：

| 功能 | 优先级 | 分类 | 说明 |
|------|--------|------|------|
| 账单明细查看 | P1 | 核心功能 (v2.0) | 查看入住期间完整消费流水 |
| 账务冲销 | P1 | 核心功能 (v2.0) | 领班取消错误交易 |
| 折扣授权审批 | P2 | 核心功能 (v2.0) | 经理审核折扣申请 |
| 日结报表生成 | P2 | 核心功能 (v2.0) | 当日营业汇总 |
| 营收报表 | P2 | Todo (v2.0) | 按日/周/月维度统计 |
| 押金管理报表 | P2 | Todo (v2.0) | 押金收支明细统计 |

> ❗ 该模块为 v2.0 范围，MVP 阶段不实现。

### 7.6 认证与权限模块 (Auth & RBAC)

> 该模块为系统基础能力，MVP 阶段需完成但按最小可用实现。

**功能清单**：

| 功能 | 优先级 | 分类 | 说明 |
|------|--------|------|------|
| 用户名密码登录 | P0 | 核心功能 | JWT token 发放 |
| Token 校验与自动续期 | P0 | 核心功能 | Spring Security Filter Chain 验证 |
| 登出 | P0 | 核心功能 | Token 失效处理 |
| 基于角色的菜单路由 | P0 | 核心功能 | 客户端根据角色展示不同菜单 |
| 服务端 RBAC 拦截器 | P0 | 核心功能 | 每个 API 端点强制校验权限 |
| 角色权限管理界面 | P3 | Todo (v4.0) | Admin 管理权限分配 |
| 多因子认证 | P3 | Todo | 二步验证 |

### 7.7 系统管理模块 (Admin)

**功能清单**：

| 功能 | 优先级 | 分类 | 说明 |
|------|--------|------|------|
| 用户创建/编辑/停用 | P2 | 核心功能 (v4.0) | 管理前台员工账号 |
| 角色权限配置 | P2 | 核心功能 (v4.0) | 分配角色及其权限集合 |
| 系统操作日志 | P2 | 核心功能 (v4.0) | 查看关键操作记录 |
| 系统配置管理 | P3 | Todo | 全局参数配置 |
| 数据备份与恢复 | P3 | Todo | 数据库备份功能 |

> ❗ 该模块为 v4.0 范围，MVP 阶段不实现。

### 7.8 模块间依赖关系

```
认证与权限 ──── 所有模块依赖，必须最先完成
     │
     ├──→ 入住管理 ──→ 退房管理
     │       │              │
     │       └──→ 房态管理 ──┘
     │       │
     │       └──→ 客人管理
     │
     ├──→ 账务管理 (v2.0, 依赖退房管理)
     │
     └──→ 系统管理 (v4.0, 独立)
```

> **启动顺序建议**：认证 →（入住 + 房态 + 客人）→ 退房 →（账务 + 系统管理）

---

## 八、技术难点分析

### 8.1 RESTful API 接口版本管理

**难点**：前后端分离架构下，前端和后端独立部署迭代。一旦接口返回结构或参数发生变化，已上线的前端页面可能出现数据解析失败或功能异常。

**应对思路**：
- **URL 路径版本化**：在基础路径中嵌入版本号（如 `/api/v1/`、`/api/v2/`），大版本变更时新建路径。
- **字段兼容策略**：响应体中新增字段不删除旧字段，避免前端解析报错；前端按需消费字段，不依赖全部返回结构。
- **Swagger/OpenAPI 文档同步**：接口变更时及时更新文档，前后端围绕文档对齐。
- **废弃通知机制**：接口废弃前提前一个版本周期标记 `deprecated`，并在响应头中添加 `X-API-Deprecated: true` 警告。

### 8.2 房态并发控制

**难点**：同一时间段内，多位接待员可能同时尝试为不同客人办理入住，若选择同一间可售房间，会引发"超卖"问题。

**应对思路**：
- **Redis 分布式锁**：在办理入住的关键路径上，以 `room:{room_id}:lock` 为 key 获取分布式锁，确保同一时刻只有一个线程能操作该房间的入住流程。
- **乐观锁**：在 `room` 表的 status 字段上使用版本号 (version) 或 CAS (Compare And Set) 更新，`UPDATE room SET status='occupied', version=version+1 WHERE id=? AND version=?`。
- **双重校验**：在业务逻辑层先检查房态，在持久化层再次通过数据库约束兜底。

### 8.3 RBAC 权限拦截器实现位置

**难点**：权限校验应在后端执行还是前端执行？

**设计决策**：权限校验必须在 **后端** 拦截，前端的权限控制仅作为 UI 层面的辅助（如隐藏无权限的按钮）。

**后端实现方案**：
- 在 Spring Security 的 Filter Chain 中注册自定义权限过滤器，实现统一的 RBAC 校验。
- 在每个 Controller 的 API 端点通过注解（如 `@PreAuthorize`）声明所需的最低权限级别。
- 过滤器从请求头中提取 JWT → 解析用户身份 → 查询角色 → 比对权限 → 允许或拒绝。

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

### 8.4 账务冲销与事务一致性

**难点**：账务冲销操作涉及创建一条"反向流水"并关联原流水，同时可能影响房间账单总金额。若在冲销过程中系统崩溃，容易出现"只记录了冲销但未更新账单"或"只更新了账单但未记录冲销"等数据不一致。

**应对思路**：
- 采用**本地事务**包裹冲销操作：`INSERT transaction_log (反向流水)` 与 `UPDATE registration (押金/金额)` 在同一个数据库事务中执行。
- 预留冲销字段 `reversal_of` 形成流水关联链，支持冲销的冲销（反冲销）。
- 关键操作记录操作日志 (audit log)，便于事后审计和对账。

### 8.5 房态与订单的状态机设计

**难点**：房间状态、订单状态、入住记录状态三者之间存在复杂的联动关系，非法状态转换会导致数据逻辑混乱（如已退房的订单被再次结账）。

**应对思路**：

**房态有限状态机**：
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

- 状态转换采用**硬编码状态机**，禁止任意跳转。
- 每次状态变更记录 `status_change_log` 表，追踪完整的变化链路。
- 服务端做最终校验，客户端请求时必须附带当前状态，服务端校验当前状态是否符合预期转换路径。

---

## 附录 A：权限代码字典

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
| `room:update_status` | 手动修改房态 | Supervisor |
| `finance:view_bill` | 查看账单明细 | Supervisor |
| `finance:reverse` | 账务冲销 | Supervisor |
| `finance:discount` | 折扣授权 | Supervisor |
| `finance:report` | 查看报表 | Supervisor |
| `pricing:adjust` | 调整房型价格 | Manager |
| `schedule:view` | 查看排班 | Manager |
| `schedule:edit` | 编辑排班 | Manager |
| `vip:approve` | VIP 权限审批 | Manager |
| `admin:user_mgmt` | 用户管理 | Admin |
| `admin:role_config` | 角色权限配置 | Admin |
| `admin:logs` | 系统日志查看 | Admin |
