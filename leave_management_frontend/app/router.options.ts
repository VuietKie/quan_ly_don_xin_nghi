import type { RouterConfig } from "nuxt/schema";

export default <RouterConfig>{
    routes: (_routes) => [
        {
            name: 'login',
            path: '/login',
            component: () => import('~/pages/auth/Login.vue'),
        },
        {
            name: 'index',
            path: '/',
            component: () => import('~/pages/dashboard/Dashboard.vue'),
        },
    ]
};