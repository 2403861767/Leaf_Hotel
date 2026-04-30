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

**前置条件**：确保 MySQL 和 Redis 服务已启动，并已执行过 `sql/init.sql` 初始化数据库。

#### 3.1 修改数据库配置（可选）

如果 MySQL 用户/密码不是默认的 `root/root`，编辑配置文件：

```bash
hotel-backend/src/main/resources/application-dev.yml
```

修改 `spring.datasource.username` 和 `spring.datasource.password` 为你本地的账号密码。

#### 3.2 启动服务

```bash
cd hotel-backend
mvn spring-boot:run
```

或使用 Maven Wrapper（无需本地安装 Maven）：

```bash
cd hotel-backend
./mvnw spring-boot:run     # Linux/macOS
mvnw.cmd spring-boot:run   # Windows
```

#### 3.3 验证启动

启动成功后，控制台输出类似：

```
Started HotelApplication in 3.5 seconds
```

#### 3.4 访问地址

| 项目 | 地址 |
|------|------|
| API 基础路径 | `http://localhost:8080/api/v1` |
| Swagger 接口文档 | `http://localhost:8080/swagger-ui.html` |
| OpenAPI JSON | `http://localhost:8080/api-docs` |

#### 3.5 接口测试（Swagger UI）

项目集成了 SpringDoc OpenAPI，启动后端后可直接在 Swagger UI 页面中交互式测试所有接口：

1. **打开 Swagger UI**：浏览器访问 [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

2. **登录获取 Token**：
   - 找到 `auth-controller` → `POST /api/v1/auth/login`
   - 点击 **Try it out**
   - 请求体输入：
     ```json
     {
       "username": "zhangsan",
       "password": "123456"
     }
     ```
   - 点击 **Execute**，在 Response Body 中复制 `data.token` 的值

3. **授权（Authorize）**：
   - 点击页面右上角的 **Authorize** 按钮
   - 在 Value 输入框中粘贴 token（注意不要带 `Bearer` 前缀，Swagger 会自动添加）
   - 点击 **Authorize** 关闭对话框

4. **调用需认证的接口**（如房态查询）：
   - 找到 `room-controller` → `GET /api/v1/rooms/status-map`
   - 点击 **Try it out** → **Execute**
   - 已认证的请求会自动携带 Bearer Token，无需手动处理

> **提示**：Token 有效期为 7200 秒（2 小时），过期后需重新登录获取。

### 4. 启动前端

**前置条件**: Node.js 18+ 已安装，后端服务已启动。

#### 4.1 安装依赖

```bash
cd hotel-fronted
npm install
```

#### 4.2 启动开发服务器

```bash
cd hotel-fronted
npm run dev
```

前端默认在 `http://localhost:5173` 启动，Vite 已配置代理将 `/api` 请求转发到 `http://localhost:8080`。

#### 4.3 生产构建

```bash
cd hotel-fronted
npm run build
```

构建产物输出到 `hotel-fronted/dist/` 目录。

#### 4.4 访问地址

| 项目 | 地址 |
|------|------|
| 前端页面 | `http://localhost:5173` |
| API 基础路径 | `http://localhost:5173/api/v1`（开发模式通过 Vite 代理） |

#### 4.5 默认登录账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| zhangsan | 123456 | 前台接待 |
| wangwu | 123456 | 前台领班 |
| sunqi | 123456 | 前台经理 |
| wujiu | 123456 | 系统管理员 |

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
│   └── init.sql                  # 数据库初始化脚本（含 94 条 Mock 数据）
├── docs/
│   ├── PRD-V1.md                 # v1.0 MVP 需求文档
│   └── PRD.md                    # 完整产品需求文档
├── hotel-backend/
│   ├── pom.xml                   # Maven 构建配置
│   └── src/
│       ├── main/
│       │   ├── java/com/seeleaf/hotel/
│       │   │   ├── common/            # 公共基础设施（响应封装、异常处理、常量）
│       │   │   ├── config/            # 配置类（MyBatis-Plus、Redis、Swagger、CORS）
│       │   │   ├── security/          # 安全层（JWT 认证、Spring Security）
│       │   │   ├── entity/            # 数据库实体（10 个实体对应 10 张表）
│       │   │   ├── mapper/            # MyBatis-Plus Mapper 接口
│       │   │   ├── service/           # 业务逻辑接口与实现
│       │   │   ├── controller/        # RESTful API 控制器
│       │   │   ├── dto/               # 请求/响应数据传输对象
│       │   │   └── HotelApplication.java  # 启动类
│       │   └── resources/
│       │       ├── application.yml        # 主配置
│       │       ├── application-dev.yml    # 开发环境配置
│       │       └── mapper/                # MyBatis XML 映射（预留）
│       └── test/
│           └── java/com/seeleaf/hotel/
│               └── HotelApplicationTests.java
├── hotel-fronted/
│   ├── index.html                 # 入口 HTML
│   ├── package.json               # NPM 依赖配置
│   ├── vite.config.js             # Vite 配置（含开发代理）
│   └── src/
│       ├── main.js                # 应用入口（注册 Element Plus/Pinia/Router）
│       ├── App.vue                # 根组件
│       ├── style.css              # 全局样式
│       ├── api/                   # Axios 请求层
│       │   ├── index.js           # Axios 实例 + 拦截器
│       │   ├── auth.js            # 认证 API
│       │   ├── checkin.js         # 入住管理 API
│       │   ├── checkout.js        # 退房管理 API
│       │   ├── room.js            # 房态管理 API
│       │   ├── guest.js           # 客人管理 API
│       │   └── deposit.js         # 押金管理 API
│       ├── router/
│       │   └── index.js           # 路由配置（含导航守卫）
│       ├── stores/
│       │   └── auth.js            # Pinia 认证状态管理
│       ├── layout/
│       │   └── MainLayout.vue     # 主布局（侧边栏 + 顶栏 + 内容区）
│       └── views/
│           ├── Login.vue              # 登录页
│           ├── Dashboard.vue          # 工作台
│           ├── RoomStatus.vue         # 房态图
│           ├── CheckinManagement.vue  # 入住管理列表
│           ├── CheckinCreate.vue      # 办理入住
│           ├── CheckinDetail.vue      # 入住详情
│           ├── CheckoutManagement.vue # 退房管理
│           ├── GuestManagement.vue    # 客人管理
│           └── DepositManagement.vue  # 押金管理
└── README.md
```
