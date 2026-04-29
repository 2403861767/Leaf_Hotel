# Leaf-Hotel 酒店前台管理系统

基于 Spring Boot + Vue3 的前后端分离酒店前台管理系统，覆盖客房预订、入住登记、房态管理、账务处理等前台核心业务流程。

## 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| 前端框架 | Vue3 + Vite | 3.5+ / 8.x |
| UI 组件库 | Element Plus | 2.x |
| 状态管理 | Pinia | 2.x |
| 后端框架 | Spring Boot | 3.5.x |
| ORM | MyBatis-Plus | 3.5.x |
| 数据库 | MySQL | 8.4 LTS |
| 缓存/锁 | Redis | 7.x |
| 认证 | JWT (Bearer Token) | — |
| 权限 | Spring Security | 6.x |

## 快速开始

### 1. 初始化数据库

```bash
mysql -u root -p < sql/init.sql
```

或在 MySQL 客户端中执行：

```sql
source sql/init.sql;
```

脚本自动完成：
- 创建 `leaf_hotel` 数据库（utf8mb4）
- 创建 10 张业务表及索引
- 插入 94 条 Mock 数据（各表 10 条，sys_role 4 条）

### 2. 数据库默认账户

| 用户名 | 密码 | 角色 |
|--------|------|------|
| zhangsan | 123456 | 前台接待 |
| lisi | 123456 | 前台接待 |
| wangwu | 123456 | 前台领班 |
| zhaoliu | 123456 | 前台领班 |
| sunqi | 123456 | 前台经理 |
| zhouba | 123456 | 前台经理 |
| wujiu | 123456 | 系统管理员 |
| zhengshi | 123456 | 系统管理员 |
| chenyi | 123456 | 前台接待 |
| liner | 123456 | 前台接待（已停用） |

### 3. 启动后端

```bash
# 待实现
```

### 4. 启动前端

```bash
# 待实现
```

## 数据库表结构

| 表名 | 说明 | 核心字段 |
|------|------|----------|
| sys_role | 系统角色 | role_id, role_name, role_key |
| sys_user | 系统用户 | username, password_hash, real_name, role_id, status |
| sys_role_permission | 角色权限关联 | role_id, permission_code |
| room_type | 房型 | type_name, base_price, max_guests, bed_type, area |
| room | 客房 | room_number, floor, room_type_id, status, version |
| guest | 客人 | name, id_type, id_number, phone, address |
| reservation | 订单 | booking_number, guest_id, room_type_id, dates, status, source |
| registration | 入住记录 | guest_id, room_id, check_in_time, deposit_amount, status |
| deposit | 押金记录 | registration_id, payment_method, amount, status |
| transaction_log | 账务流水 | registration_id, transaction_type, amount, balance |

## 项目结构

```
Hotel/
├── sql/
│   └── init.sql          # 数据库初始化脚本
├── docs/
│   ├── PRD-V1.md          # v1.0 MVP 需求文档
│   └── PRD.md             # 完整产品需求文档
└── README.md
```
