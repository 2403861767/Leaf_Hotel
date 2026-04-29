# 酒店前台管理系统 — 产品需求文档 (PRD)

## 版本历史

| 版本 | 日期 | 作者 | 变更说明 |
|------|------|------|----------|
| v2.0 | 2026-04-29 | SeeLeaf | 全面修订：改为 RESTful + 前后端分离架构，Java + Vue3 技术栈 |
| v3.0 | 2026-04-29 | SeeLeaf | 重构版本规划：聚焦四角色核心功能，新增订单来源字段，重排功能优先级 |

> 注：本文档版本号（v3.0）代表 PRD 文档的修订版本，与后文产品路线图中的产品版本（v1.0 ~ v6.0+）相互独立。

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
| 前端构建 | **Vite** | 8.x | 原生 ESM，开发热更新快，构建体积小（Vite 8 为当前稳定版） |
| 前端 UI 组件 | **Element Plus** | 2.x | 成熟的企业级组件库，当前稳定版 2.13+ |
| 前端状态管理 | **Pinia** | 2.x | Vue3 官方推荐状态管理，轻量且支持 TypeScript |
| HTTP 客户端 | **Axios** | 1.x | 请求/响应拦截器，方便注入 Token 与统一错误处理 |
| 后端框架 | **Spring Boot** | 3.5.x | 核心技术栈，生态完善，RESTful 原生支持（3.5.x 为当前活跃维护版本） |
| ORM | **MyBatis-Plus** | 3.5.x | 灵活对接 SQL，当前稳定版 3.5.16+，代码生成器提升效率 |
| API 文档 | **Swagger (SpringDoc)** | OpenAPI 3.0 | 自动生成接口文档，前后端联调契约 |
| 数据库 | **MySQL** | 8.4 LTS | 关系型存储，事务支持好；8.4 LTS 长期支持版（8.0 已于 2026-04 终止支持） |
| 缓存/锁 | **Redis** | 7.x | 房态缓存加速 + 分布式锁保证并发安全（当前稳定版 7.4+） |
| 认证机制 | **JWT (access token)** | — | 无状态认证，适合前后端分离架构 |
| 权限框架 | **Spring Security** | 6.x | 内置 RBAC 支持，Filter Chain 可灵活配置拦截规则 |
| API 网关 (可选) | **Spring Cloud Gateway** / **Nginx** | — | 统一入口、路由转发、限流熔断，v3.0+ 按需引入 |
| 消息队列 (可选) | **RabbitMQ** | — | OTA 订单异步处理、夜审任务调度、系统解耦，v3.0+ 按需引入 |

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

### 3.4 OTA 订单接入流程

> ⚠️ 该流程为 **v5.0** 规划内容，MVP 阶段不实现。

```
[客人在 OTA 平台下单 (携程 / 美团 / 飞猪等)]
     │
     ▼
[OTA 平台通过 API / E-Booking 推送订单到 PMS]
     │
     ▼
[系统自动接收并解析 OTA 订单]
     │
     ├── 校验通过 → 生成内部预订记录 (reservation)
     │                 │
     │                 ▼
     │          [房态自动锁定：标记该房型在对应时段不可售]
     │                 │
     │                 ▼
     │          [前台确认订单 → 分配具体房间]
     │
     └── 校验失败 (信息不全/房型不存在) → 人工介入处理
```

- **前置条件**：OTA 平台已配置对接参数（AppKey、Secret、推送地址）。
- **后置条件**：系统自动生成预订记录，OTA 渠道房态同步更新。
- **异常流程**：OTA 推送重复订单 → 按 booking_number 幂等去重；OTA 接口超时 → 自动重试队列 + 人工告警。

### 3.5 夜审流程 (Night Audit)

> ⚠️ 该流程为 **v5.0** 规划内容，MVP 阶段不实现。

```
[每日凌晨 02:00-04:00 定时触发]
     │
     ▼
[系统自动执行夜审前检查]
  ├── 检查未办入住(No-show)的预订 → 自动取消
  ├── 检查在住客人房费 → 按过夜费率自动过账
  └── 检查账务流水平衡 → 如有异常标记待处理
     │
     ▼
[生成前日营业汇总报表]
  ├── 入住数 / 退房数 / 在住数
  ├── 房费收入 / 杂费收入 / 押金收入
  ├── 各渠道 (OTA / 散客) 订单量统计
  └── 挂账 / 冲销明细
     │
     ▼
[系统执行日切 (Business Date 切换)]
  ├── 营业日期自动推进一天
  ├── 房费开始按新日期计费
  └── 报表归档，不可修改
     │
     ▼
[夜审完成，系统进入新营业日]
```

- **前置条件**：当前时间处于夜审窗口期，无进行中的入住/退房操作。
- **后置条件**：营业日期切换，房费自动过账，日结报表归档。
- **异常流程**：夜审中断 → 支持断点续审；检查不通过 → 标记异常项并通知管理员处理。

### 3.6 自助入住流程 (Kiosk / Self Check-in)

> ⚠️ 该流程为 **v6.0+** 规划内容，MVP 阶段不实现。

```
[客人到达自助入住机]
     │
     ▼
[客人选择：有预订 / 无预订(散客)]
     │
     ├── 有预订 → 输入预订号 / 扫描身份证 / 手机号查询
     │              │
     │              ▼
     │       [系统调取预订信息]
     │
     └── 无预订 → 选择房型 → 系统筛选可售房间
                    │
                    ▼
             [确认房价与入住天数]
     │
     ▼
[身份验证：读取身份证信息 → 人证比对]
     │
     ▼
[支付押金/房费：微信 / 支付宝 / 银联]
     │
     ▼
[系统分配房间，生成入住单]
     │
     ▼
[自助发卡机写卡 / 二维码电子钥匙]
     │
     ▼
[入住完成]
```

- **前置条件**：自助机已联网且运行正常，房间库存可售。
- **后置条件**：生成入住记录，房态变更为"入住中"。
- **异常流程**：支付失败 → 引导转前台人工办理；证照识别失败 → 人工介入核实。

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
  "source": "walk_in",
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

### 4.7 认证与权限 API

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

### 4.8 系统管理 API

> ⚠️ 该模块为 v4.0 范围，仅 Admin 角色可访问。

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| `POST` | `/api/v1/users` | 创建用户 | Admin |
| `PUT` | `/api/v1/users/{id}` | 更新用户 | Admin |
| `DELETE` | `/api/v1/users/{id}` | 停用用户 | Admin |
| `GET` | `/api/v1/roles` | 获取角色列表 | Admin |
| `PUT` | `/api/v1/roles/permissions` | 更新角色权限 | Admin |
| `GET` | `/api/v1/logs` | 查看系统日志 | Admin |

### 4.9 通用参数说明

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
| source | VARCHAR(20) | 订单来源：walk_in / phone / front_desk / manager / online_direct / online_ota / contract |
| deposit_amount | DECIMAL(10,2) | 押金金额 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

> **字段说明 — `source`（订单来源）**：`walk_in` 散客到店、`phone` 电话预订、`front_desk` 前台代订、`manager` 经理预订、`online_direct` 官方直营预订、`online_ota` OTA 平台预订、`contract` 协议单位/公司预订。该字段用于后续营收分析中按渠道统计订单量。

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

### 5.5 账务流水

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

### 5.6 ER 关系简图

```
sys_user ──N:1── sys_role ──1:N── sys_role_permission
                                    │
  room_type ──1:N── room ──1:N── registration ──N:1── guest
                  │              │
                  │              │
                  │              └──1:N── deposit
                  │              │
                  │              └──1:N── transaction_log
                  │
             reservation ──N:1── guest
```

### 5.7 拓展表结构

**OTA 订单映射表 — `ota_order_mapping`**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT PK | 自增 ID |
| ota_platform | VARCHAR(50) | OTA 平台标识 (ctrip/meituan/fliggy) |
| ota_order_id | VARCHAR(100) | OTA 平台订单号 |
| booking_number | VARCHAR(50) | 内部预订编号，关联 reservation |
| ota_room_type_id | VARCHAR(100) | OTA 平台房型 ID |
| raw_data | JSON | OTA 推送的原始订单数据 |
| status | VARCHAR(20) | 状态：synced / confirmed / cancelled |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |
> UNIQUE KEY: (ota_platform, ota_order_id)

**会员表 — `member`**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT PK | 会员 ID |
| guest_id | BIGINT FK | 关联客人表 |
| level | VARCHAR(20) | 会员等级 (regular/silver/gold/platinum) |
| points | INT | 累计积分 |
| total_consumption | DECIMAL(12,2) | 累计消费金额 |
| total_stays | INT | 累计入住次数 |
| phone | VARCHAR(20) | 绑定手机号 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

**操作日志表 — `operation_log`**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT PK | 日志 ID |
| operator_id | BIGINT FK | 操作人 |
| operation_type | VARCHAR(50) | 操作类型 (checkin/checkout/reverse/etc.) |
| target_type | VARCHAR(50) | 操作对象类型 (registration/room/user/etc.) |
| target_id | BIGINT | 操作对象 ID |
| detail | JSON | 操作详情 (变更前后对比) |
| ip_address | VARCHAR(50) | 操作来源 IP |
| created_at | DATETIME | 创建时间 |

**设备集成配置表 — `device_config`**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT PK | 配置 ID |
| device_type | VARCHAR(50) | 设备类型 (id_reader/door_lock/pos/printer) |
| device_name | VARCHAR(100) | 设备名称 |
| interface_type | VARCHAR(50) | 接口类型 (serial/usb/network/api) |
| connection_params | JSON | 连接参数 (IP/端口/串口号等) |
| status | TINYINT | 状态：1-启用，0-停用 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

---

## 六、MVP 范围与迭代路线图

### 6.1 版本规划

| 版本 | 代号 | 目标 |
|------|------|------|
| **v1.0 MVP** | Check-in | 实现前台核心操作闭环：入住 → 房态 → 退房 |
| **v2.0** | Billing | 增加账务管理与报表能力 |
| **v3.0** | Management | 增加管理决策功能（定价、排班、VIP 审批） |
| **v4.0** | Admin | 增加系统管理功能（用户、日志、角色配置） |
| **v5.0** | Connectivity | OTA 平台对接、支付通道集成、消息通知、智能报表 |
| **v6.0+** | Intelligence | AI 智能定价、硬件集成（自助机/门锁/读卡器）、多门店集团化管理 |

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

### 6.6 v5.0 范围（对外连接）

- OTA 平台对接（携程 / 美团 / 飞猪订单同步与房态推送）
- 支付通道集成（微信 / 支付宝 / 银联）
- 短信 / 邮件通知服务接入
- 智能报表：夜审 (Night Audit)、渠道营收分析
- 多渠道订单统一管理与自动同步

### 6.7 v6.0+ 范围（智能化与扩展）

- AI 智能定价与动态调价（基于入住率、季节、节假日）
- 自助入住机 / 身份证读卡器 / 门锁系统集成
- 会员 / 常客计划管理
- POS 收银对接
- 多门店 / 集团化管理
- 开放 API 生态

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
| 押金收取 | P0 | 核心功能 | 支持现金/微信/支付宝/银行卡等多种支付方式录入 |
| 押金记录管理 | P1 | 核心功能 | 查看押金明细、按支付方式筛选、退押金操作 |
| 入住单生成与打印 | P1 | 核心功能 | 生成登记卡，支持打印 |
| 在住客人列表查询 | P0 | 核心功能 | 按房号/姓名搜索在住记录 |
| 入住信息修改 | P1 | 核心功能 | 换房、延住、修改同住人 |
| 批量入住 | P2 | Todo (v5.0) | 团队/旅行团批量办理 |
| 历史入住记录查询 | P1 | 核心功能 | 按日期/客人/房间维度检索 |
| 自助入住机对接 | P3 | Todo (v6.0+) | 引导客人通过自助机办理入住 |
| 团队批量入住优化 | P2 | Todo (v5.0) | 旅行社/会议团体一键分配房间 |

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
| 批量退房 | P2 | Todo (v5.0) | 团队批量退房 |
| 快速退房（免查房） | P2 | Todo (v5.0) | 信用住客人直接退房，无需等待查房 |
| 离店后账单补发 | P2 | Todo (v5.0) | 支持邮件/微信发送电子账单 |

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
| 维修计划排期 | P2 | Todo (v3.0) | 按房间/时间段安排维修计划 |
| 清洁工单管理 | P3 | Todo (v3.0) | 退房后自动生成清洁工单并分配 |

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
| 黑名单管理 | P3 | Todo (v5.0) | 标记不良住客 |
| 会员/常客计划 | P2 | Todo (v4.0) | 会员等级、积分累计与兑换 |
| 会员消费统计 | P2 | Todo (v4.0) | 累计消费金额、入住次数、偏好分析 |
| 客人标签管理 | P3 | Todo (v4.0) | 自定义标签分类客群 |

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
| 夜审 (Night Audit) | P2 | Todo (v5.0) | 日终自动过账、日切、报表归档 |
| 渠道营收分析 | P2 | Todo (v3.0) | 按 OTA / 散客 / 协议单位统计收入 |

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
| 多因子认证 | P3 | Todo (v6.0+) | 二步验证 |

### 7.7 系统管理模块 (Admin)

**功能清单**：

| 功能 | 优先级 | 分类 | 说明 |
|------|--------|------|------|
| 用户创建/编辑/停用 | P2 | 核心功能 (v4.0) | 管理前台员工账号 |
| 角色权限配置 | P2 | 核心功能 (v4.0) | 分配角色及其权限集合 |
| 系统操作日志 | P2 | 核心功能 (v4.0) | 查看关键操作记录 |
| 系统配置管理 | P3 | Todo (v4.0) | 全局参数配置 |
| 数据备份与恢复 | P3 | Todo (v4.0) | 数据库备份功能 |

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

> **启动顺序建议**：认证 →（入住 + 房态 + 客人）→ 退房 →（账务 + OTA 直连 + 系统管理）

### 7.9 OTA 直连与第三方集成模块 (OTA & Third-party Integration)

**子模块划分**：

| 子模块 | 说明 |
|--------|------|
| OTA 平台对接 | 携程、美团、飞猪等主流 OTA 订单同步与房态管理 |
| 支付通道集成 | 微信支付、支付宝、银联等支付渠道对接 |
| 硬件设备集成 | 身份证读卡器、门锁系统、POS 收银、打印设备 |

**功能清单**：

| 功能 | 优先级 | 分类 | 说明 |
|------|--------|------|------|
| OTA 订单自动同步 | P2 | 核心功能 (v5.0) | 接收 OTA 平台推送的预订订单 |
| OTA 渠道房态同步 | P2 | 核心功能 (v5.0) | 实时同步可售房量至各 OTA 平台 |
| OTA 订单幂等去重 | P2 | 核心功能 (v5.0) | 按 booking_number 防止重复创建预订 |
| 多渠道订单统一管理 | P2 | 核心功能 (v5.0) | 集中展示所有渠道订单 |
| 微信支付 / 支付宝接入 | P2 | 核心功能 (v5.0) | 押金在线支付与退款 |
| 银联 / 对公转账 | P3 | Todo (v6.0+) | 对公账户支付场景 |
| 身份证读卡器对接 | P2 | 核心功能 (v6.0+) | 自动读取证件信息，减少录入错误 |
| 门锁系统写卡对接 | P2 | 核心功能 (v6.0+) | 入住后自动制作房卡 |
| POS 收银对接 | P3 | Todo (v6.0+) | 刷卡 / 扫码 POS 机收款 |
| 短信 / 邮件通知 | P2 | 核心功能 (v5.0) | 入住确认、账单发送、营销通知 |
| 开放 API 网关 | P2 | Todo (v6.0+) | 为未来第三方系统对接预留开放接口 |

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

## 九、竞品分析

### 9.1 主流 PMS 系统概览

| 产品 | 开发商 | 核心定位 | 适用场景 | 技术特点 |
|------|--------|----------|----------|----------|
| **Shiji Daylight / Cambridge** | 石基信息 (Shiji) | 全球高端酒店数字化领航者 | 国际品牌高星级酒店 | 云原生微服务、"API-first" 架构、SaaS 订阅 |
| **西软 XMS / 千里马 iPMS** | 石基信息 (Shiji) | 中端及中低端酒店覆盖 | 中端连锁 / 单体酒店 | 功能深度 + 高性价比 |
| **绿云 PMS** | 绿云软件 | 本土"云链"实践者 | 中高端酒店及连锁品牌 | OpenCloudOS 底座、"智慧住"自助方案 |
| **盟广信息** | 华住集团生态 | 连锁标准化标杆 | 快速扩张的连锁品牌 | 模块化"酒店操作系统"、开盒即用 |
| **Oracle Opera** | Oracle | 全球奢华市场标准 | 国际高端酒店 | 功能全面但实施周期长、成本高 |
| **别样红** | 美团生态 | 中小型酒店 + OTA 集成 | 中小型酒店 / 民宿 | 美团渠道深度集成 |
| **订单来了** | — | 民宿移动端体验 | 民宿 / 精品酒店 | 移动端优先、AI 智能定价 |

### 9.2 核心功能覆盖对比

| 功能模块 | 石基 Shiji | 绿云 | 盟广信息 | Oracle Opera | 本项目 (目标) |
|----------|:-----------:|:----:|:--------:|:------------:|:-------------:|
| 房态管理 | ✅ | ✅ | ✅ | ✅ | ✅ |
| 预订/入住/退房 | ✅ | ✅ | ✅ | ✅ | ✅ |
| 账务管理 | ✅ | ✅ | ✅ | ✅ | ✅ |
| OTA 直连 | ✅ | ✅ | ✅ | ✅ | 🚧 v5.0 |
| 移动端管理 | ✅ | ✅ | ✅ | ✅ | 🚧 v6.0+ |
| 自助入住 Kiosk | ✅ | ✅ | ❌ | ❌ | 🚧 v6.0+ |
| 会员 / 常客计划 | ✅ | ✅ | ✅ | ✅ | 🚧 v6.0+ |
| 多门店 / 集团管理 | ✅ | ✅ | ✅ | ✅ | 🚧 v6.0+ |
| AI 智能能力 | 🚧 | 🚧 | ❌ | ❌ | 🚧 v6.0+ |
| 开放 API 生态 | ✅ | ✅ | ❌ | ✅ | 🚧 v6.0+ |

> ✅ = 已具备  🚧 = 规划/开发中  ❌ = 不具备

### 9.3 差异化定位

与上述竞品相比，本项目的核心差异化优势：

1. **轻量化 + 现代化**：聚焦中小型酒店的"前台核心"需求，不追求大而全。基于 Vue3 + Vite + Element Plus 构建现代 Web 界面，交互体验优于传统 PMS。
2. **全开源技术栈**：基于 Spring Boot + MyBatis-Plus 等开源框架构建，无许可证费用，适合预算有限的中小酒店。
3. **渐进式迭代**：从核心入住流程出发（v1.0 MVP），按管理决策（v3.0）→ 系统管理（v4.0）→ 对外连接（v5.0）→ 智能化（v6.0+）分阶段演进，降低初始投入。
4. **API 优先设计**：从设计之初即遵循 RESTful API 规范，前端/后端/第三方集成解耦，便于未来扩展。
5. **专注中国市场**：深度适配国内酒店运营习惯（身份证读卡、微信/支付宝支付、美团/携程 OTA 直连），无需额外定制。

---

## 十、硬件与第三方集成

> ⚠️ 本章节为 **v5.0 ~ v6.0+** 规划内容，MVP 阶段不实现。

### 10.1 硬件兼容性需求

| 设备类型 | 接口方式 | 集成用途 | 优先级 | 支持版本 |
|----------|----------|----------|--------|----------|
| 身份证读卡器 | USB / 串口 | 自动读取客人身份信息，减少手工录入 | P1 | v4.0 |
| 门锁系统 | API / 串口 | 入住后自动写卡制卡，退房自动销卡 | P2 | v4.0 |
| POS 收银机 | API / 串口 | 刷卡 / 扫码收款，自动对账 | P2 | v5.0 |
| 打印机 (小票/单据) | USB / 网络 | 入住单、账单、报表打印 | P1 | v1.0 |
| 自助入住机 (Kiosk) | HTTP API | 客人自助办理入住、支付、取卡 | P2 | v4.0 |
| 扫码枪 | USB | 快速扫描证件、条形码 | P2 | v2.0 |

### 10.2 第三方服务集成

| 服务类型 | 服务商 | 集成方式 | 用途 | 版本 |
|----------|--------|----------|------|------|
| OTA 平台 | 携程 / 美团 / 飞猪 | HTTP API / E-Booking | 订单同步、房态推送 | v3.0 |
| 支付网关 | 微信支付 / 支付宝 | SDK / HTTP API | 在线收款、退款 | v3.0 |
| 银联支付 | 银联 | SDK | 对公账户收款 | v4.0+ |
| 短信通知 | 阿里云 SMS / 腾讯云 SMS | HTTP API | 入住确认、活动通知 | v3.0 |
| 邮件通知 | SMTP / SendCloud | SMTP / API | 电子账单、报表发送 | v3.0 |

### 10.3 集成架构设计

```
 ┌─────────────────────────────────────────────┐
 │              统一集成层 (Integration Layer)    │
 │  ┌──────────┐ ┌──────────┐ ┌──────────────┐  │
 │  │ OTA 适配器 │ │ 支付适配器 │ │ 硬件设备适配器 │  │
 │  └──────────┘ └──────────┘ └──────────────┘  │
 │         │           │               │         │
 │         ▼           ▼               ▼         │
 │  ┌──────────┐ ┌──────────┐ ┌──────────────┐  │
 │  │ 携程/美团 │ │ 微信/支付宝 │ │ 读卡器/门锁  │  │
 │  └──────────┘ └──────────┘ └──────────────┘  │
 └─────────────────────────────────────────────┘
```

- **适配器模式 (Adapter Pattern)**：每种集成均抽象为独立适配器，替换或新增供应商时不影响核心业务逻辑。
- **异步解耦**：第三方集成（如 OTA 订单同步、短信通知）优先通过消息队列异步处理，避免同步阻塞影响前台操作响应速度。
- **降级与熔断**：单点集成故障时自动降级（如 OTA 推送超时 → 转为手动同步），不连锁影响核心业务流程。

---

## 十一、AI 能力规划

> ⚠️ 本章节为 **v6.0+** 规划内容，属于前瞻性设计，MVP 阶段不实现。

### 11.1 AI 智能定价

| 功能 | 说明 | 输入数据 | 预期效果 |
|------|------|----------|----------|
| 动态调价 | 基于入住率、季节、节假日、竞品价格自动调整房价 | 历史入住率、OTA 竞品价格、日历事件 | 营收提升 5%~15% |
| 早鸟/连住优惠 | 自动计算提前预订折扣和连住优惠方案 | 预订提前天数、入住时长、当前入住率 | 提升非高峰期入住率 |
| 实时收益建议 | 前台办理入住时智能推荐最优房价方案 | 当前入住率、在住客单价、房型库存 | 辅助前台决策 |

### 11.2 智能推荐

| 功能 | 说明 | 数据来源 |
|------|------|----------|
| 房型推荐 | 根据客人历史偏好推荐最合适的房型 | 客人历史入住记录 |
| 服务推荐 | 入住时主动推荐早餐、加床、接送机等增值服务 | 消费习惯分析 |
| 升级销售 (Upsell) | 引导客人付费升级房型或服务 | 当前空房分布、客人消费能力 |

### 11.3 智能报表与分析

| 功能 | 说明 |
|------|------|
| 自然语言查询 | 输入"上周入住率最高的房型"等自然语言，自动生成报表 |
| 异常检测 | 自动识别账务异常、入住率异常波动并告警 |
| 预测分析 | 基于历史数据预测未来 7/30/90 天入住率与营收 |

### 11.4 评价分析

| 功能 | 说明 | 数据来源 |
|------|------|----------|
| 自动抓取评价 | 定期抓取 OTA 平台住客评价 | 携程 / 美团 / 飞猪评价接口 |
| 情感分析 | 自动识别评价的情感倾向（正面/负面/中性） | NLP 模型 |
| 关键词聚合 | 提炼评价中的高频关键词（如"服务好"、"房间旧"） | NLP 文本分析 |
| 整改建议 | 根据负面评价自动生成服务质量改进建议 | 情感 + 关键词分析 |

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
| `deposit:create` | 创建押金记录 | Receptionist |
| `deposit:refund` | 退押金 | Receptionist |
| `ota:order_sync` | OTA 订单同步管理 (v5.0+) | Receptionist |
| `ota:channel_config` | OTA 渠道参数配置 (v5.0+) | Manager |
| `finance:night_audit` | 执行夜审 (v5.0+) | Supervisor |
| `device:config` | 硬件设备配置 (v6.0+) | Admin |
| `report:ai_analysis` | AI 智能报表查看 (v6.0+) | Manager |
| `member:manage` | 会员管理（等级/积分调整）(v6.0+) | Manager |
