import './assets/main.css'
import { createApp } from 'vue'

//导入ElementPlus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
//导入axios
//import axios from "axios";
//import VueAxios from 'vue-axios'

import App from './App.vue'
import router from './router'
const app = createApp(App)

//app.use(VueAxios,axios);
app.use(router)
app.use(ElementPlus)

app.mount('#app')

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}