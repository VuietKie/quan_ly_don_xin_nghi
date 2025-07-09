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
        {
            name: 'leave-request-create',
            path: '/leave-request/create',
            component: () => import('~/pages/LeaveRequest/Create.vue'),
        },
        {
            name: 'leave-request-process',
            path: '/leave-request/process',
            component: () => import('~/pages/LeaveRequest/List.vue'),
        },
        {
            name: 'leave-request-my-request',
            path: '/leave-request/my-request',
            component: () => import('~/pages/LeaveRequest/MyRequest.vue'),
        },
        {
            name: 'leave-request-detail',
            path: '/leave-request/:id',
            component: () => import('~/pages/LeaveRequest/[id].vue'),
        },
    ]
};