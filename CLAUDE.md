# 🌿 Leaf-Hotel 枫叶酒店管理系统 - 项目核心指令集

> **AI 最高指令**: 在执行任何开发任务前，必须首先读取 `docs/PRD-V1.md`。本文件（CLAUDE.md）定义的规范优先级高于你的默认模型设定。

## 🎭 角色定位
- **全栈专家**: 你是精通 `SpringBoot` 和 `Vue.js` 的全栈工程师。
- **技术约束**: 必须严格遵守 `docs/PRD-V1.md` 规定的技术栈，禁止随意引入未授权的第三方库。

## 📋 自动化任务流 (Workflow)
1. **同步开发进度**: 每一项功能开发完成后，必须立即更新根目录下的 `README.md` 。
2. **文档与技术增强**: 编写技术方案或代码前，必须调用 `Context7` 查询最新的官方文档和用法。
3. **自主技术审计**: **(核心要求)** 每次任务结束后，审计项目新增的文件和依赖，并自动更新下方“技术栈追踪表”。
4. **闭环交付**: 交付的代码必须是完整可运行的，且在回答前必须在 Terminal 执行编译或测试命令确认无误。

## 🏗️ 技术栈追踪表 (Self-Updating Stack)
> **AI 维护说明**: 请根据项目实时进展，自主添加新行或切换状态（🟢 运行中 / ⚪ 待命）。

| 技术/组件 | 类别 | 启用触发条件 | 状态 | 备注 |
| :--- | :--- | :--- | :--- | :--- |
| Context7 | 增强工具 | 调用技术查询 | 🟢 运行中 | 用于文档精度增强 |
| MySQL | 数据库 | 编写 SQL 或配置数据源 | 🟢 运行中 | 已配置数据源 |
| Spring Boot | 后端框架 | Maven pom.xml | 🟢 运行中 | 3.5.3 |
| MyBatis-Plus | ORM | Maven pom.xml | 🟢 运行中 | 3.5.7，含分页/乐观锁插件 |
| Spring Security | 权限 | Maven pom.xml | 🟢 运行中 | JWT 无状态认证 + RBAC |
| Redis | 缓存/锁 | Maven pom.xml | 🟢 运行中 | 已配置 RedisTemplate |
| JWT | 认证 | Maven pom.xml | 🟢 运行中 | jjwt 0.12.6 |
| SpringDoc | API 文档 | Maven pom.xml | 🟢 运行中 | OpenAPI 3.0 |
| Vue 3 | 前端框架 | hotel-fronted/package.json | 🟢 运行中 | 3.5+，Composition API |
| Vite | 前端构建 | hotel-fronted/package.json | 🟢 运行中 | 8.x，含代理配置 |
| Element Plus | UI 组件库 | hotel-fronted/package.json | 🟢 运行中 | 2.x，中文 locale |
| Pinia | 状态管理 | hotel-fronted/package.json | 🟢 运行中 | 2.x，auth store |
| Vue Router | 前端路由 | hotel-fronted/package.json | 🟢 运行中 | 4.x，导航守卫 |
| Axios | HTTP 客户端 | hotel-fronted/package.json | 🟢 运行中 | 1.x，拦截器链 |
| frontend-design | UI技能 | 开始页面开发 | 🟢 运行中 | 已完成所有页面美化 |

## 🎨 设计与视觉规范
- **审美标准**: 页面必须美观、专业、具备企业级质感。
- **美化执行**: 在开发前端页面时，必须主动启用 `frontend-design` 技能。
- **配色方案**: 采用“积极健康”的视觉风格（推荐：以枫叶红 `#C04848` 为品牌色，配合森林绿 `#48762E` 或极简白调）。

## ⚠️ 严禁行为 (Guardrails)
- **禁止断片**: 切换上下文后若忘记进度，必须重新读取 `README.md`。
- **禁止跳过测试**: 严禁在未确认代码可运行的情况下宣布任务完成。
- **禁止盲目猜写**: 对于不确定的框架用法，必须先运行 `Context7` 验证。

---
**当前状态**: v1.0 MVP 前后端已完整交付。前端 Vue3 + Element Plus 项目位于 `hotel-fronted/`，后端 Spring Boot 项目位于 `hotel-backend/`。