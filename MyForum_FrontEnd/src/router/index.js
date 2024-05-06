import { createRouter, createWebHistory } from 'vue-router'
const base = ''
const router = createRouter({

  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: base + "/",
      redirect: base + "/summary"
    },
    {
      path: base + '/summary',
      name: 'summary',
      component: () => import('../views/SummaryView.vue')
    },
    {
      path: base + '/login',
      name: 'login',
      component: () => import('../views/LoginAndRegisterView.vue')
    },
    {
      path: base + '/info',
      name: 'info',
      component: () => import('../views/UserView.vue')
    },
    {
      path: base + '/contact',
      name: 'contact',
      component: () => import('../views/ContactView.vue')
    },
    {
      path: base + '/detail',
      name: 'detail',
      component: () => import('../views/DetailView.vue')
    }
  ]
})

export default router

