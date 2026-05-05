/**
 * 应用入口 —— 挂载 Vue 实例，注册全局插件和图标。
 *
 * 注册顺序：Element Plus（中文 locale）→ Pinia → Vue Router → 挂载 #app
 * 所有 Element Plus 图标通过循环全局注册，模板中可直接使用 <el-icon><IconName /></el-icon>
 */
import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
import './style.css'

const app = createApp(App)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(ElementPlus, { locale: zhCn })
app.use(createPinia())
app.use(router)
app.mount('#app')
