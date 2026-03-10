import { createApp } from 'vue'
import './style.css'
import './assets/common.css' // Global flex classes
import '@vue-flow/core/dist/style.css';
import '@vue-flow/controls/dist/style.css';
import '@vue-flow/minimap/dist/style.css';
import App from './App.vue'
import { createPinia } from 'pinia'
import 'vant/lib/index.css';

// Import Vant Components
import { Button, Field, CellGroup, Icon, Tab, Tabs, NavBar, Sidebar, SidebarItem } from 'vant';

const app = createApp(App)

app.use(createPinia())
app.use(Button)
app.use(Field)
app.use(CellGroup)
app.use(Icon)
app.use(Tab)
app.use(Tabs)
app.use(NavBar)
app.use(Sidebar)
app.use(SidebarItem)

app.mount('#app')
