import { createApp } from 'vue'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'

import App from './App'
import router from './router'
import naive from 'naive-ui'
import { Setting } from './config'
import './assets/tailMain.css'

const app = createApp(App)

const pinia = createPinia()
app.use(pinia)
pinia.use(piniaPluginPersistedstate)
app.use(router)
app.use(naive)

app.mount('#app')


// 项目信息全局console
console.clear();

// 家人们谁懂啊, \/\/\/ 天依蓝、镜音连、巡音粉、初音绿 /\/\/
console.log(
  `%c© Msi%c(Integration)\n%c${Setting.version}`,
  `padding:24px 0px 12px 0px;
  font-size:24px;
  color:#66CCFF;`,
  'font-size:24px;color:#FFE212;',
  'font-size:12px;color:#FFBFCB;padding-bottom:24px;',
);

console.group('%cConsole', 'font-size:12px;color:#39C5BB;font-weight:300;');