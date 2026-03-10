import { createApp } from 'vue'
import { createPinia } from 'pinia'
import 'jointjs/dist/joint.css'
import './assets/common.css'
import './assets/theme.css'
import './style.css'
import App from './App.vue'

const app = createApp(App)
app.use(createPinia())
app.mount('#app')
