import Vue from 'vue'
import VueRouter from 'vue-router'
import lanhu_xuqiuchouquhewentikongjianjianmoxuqiuwentikongjianjianmoqingjingtujianmo1kaobeifuben from '../views/lanhu_xuqiuchouquhewentikongjianjianmoxuqiuwentikongjianjianmoqingjingtujianmo1kaobeifuben/index.vue'
import lanhu_xuqiuchouquhewentikongjianjianmoxuqiuwentikongjianjianmoqingjingtujianmofuben from '../views/lanhu_xuqiuchouquhewentikongjianjianmoxuqiuwentikongjianjianmoqingjingtujianmofuben/index.vue'

Vue.use(VueRouter)

const routes = [
    {
    path: '/',
    redirect: "/lanhu_xuqiuchouquhewentikongjianjianmoxuqiuwentikongjianjianmoqingjingtujianmo1kaobeifuben"
  },
  {
    path: '/lanhu_xuqiuchouquhewentikongjianjianmoxuqiuwentikongjianjianmoqingjingtujianmo1kaobeifuben',
    name: 'lanhu_xuqiuchouquhewentikongjianjianmoxuqiuwentikongjianjianmoqingjingtujianmo1kaobeifuben',
    component: lanhu_xuqiuchouquhewentikongjianjianmoxuqiuwentikongjianjianmoqingjingtujianmo1kaobeifuben
  },
  {
    path: '/lanhu_xuqiuchouquhewentikongjianjianmoxuqiuwentikongjianjianmoqingjingtujianmofuben',
    name: 'lanhu_xuqiuchouquhewentikongjianjianmoxuqiuwentikongjianjianmoqingjingtujianmofuben',
    component: lanhu_xuqiuchouquhewentikongjianjianmoxuqiuwentikongjianjianmoqingjingtujianmofuben
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
