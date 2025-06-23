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
            meta: { middleware: 'admin' },
        },
        {
            name: 'profile',
            path: '/profile',
            component: () => import('~/pages/auth/Profile.vue'),
            meta: { middleware: 'admin' },
        },
        {
            name: 'user-management',
            path: '/user-management',
            component: () => import('~/pages/SysAuth/UserManagement/List.vue'),
            meta: { middleware: 'admin' },
        },
        {
            name: 'user-management-create',
            path: '/user-management/create',
            component: () => import('~/pages/SysAuth/UserManagement/Create.vue'),
            meta: { middleware: 'admin' },
        },
        {
            name: 'user-management-id',
            path: '/user-management/:id',
            component: () => import('~/pages/SysAuth/UserManagement/[id].vue'),
            meta: { middleware: 'admin' },
        },
        {
            name: 'product-management',
            path: '/product-management',
            component: () => import('~/pages/ProductManagement/List.vue'),
            meta: { middleware: 'admin' },
        },
        {
            name: 'product-management-create',
            path: '/product-management/create',
            component: () => import('~/pages/ProductManagement/Create.vue'),
            meta: { middleware: 'admin' },
        },
        {
            name: 'product-management-id',
            path: '/product-management/:id',
            component: () => import('~/pages/ProductManagement/[id].vue'),
            meta: { middleware: 'admin' },
        },
        {
            name: 'category-management',
            path: '/category-management',
            component: () => import('~/pages/CategoryManagement/List.vue'),
            meta: { middleware: 'admin' },
        },
        {
            name: 'category-management-create',
            path: '/category-management/create',
            component: () => import('~/pages/CategoryManagement/Create.vue'),
            meta: { middleware: 'admin' },
        },
        {
            name: 'category-management-id',
            path: '/category-management/:id',
            component: () => import('~/pages/CategoryManagement/[id].vue'),
            meta: { middleware: 'admin' },
        },
        {
            name: 'contact',
            path: '/contact',
            component: () => import('~/pages/ContactUs/Test.vue'),
            meta: { middleware: 'admin' },
        },
        {
            name: 'order-management',
            path: '/order-management',
            component: () => import('~/pages/OrderManagement/List.vue'),
            meta: { middleware: 'admin' },
        },
        {
            name: 'order-management-id',
            path: '/order-management/:id',
            component: () => import('~/pages/OrderManagement/[id].vue'),
            meta: { middleware: 'admin' },
        },
        {
            name: 'combo-management',
            path: '/combo-management',
            component: () => import('~/pages/ComboManagement/List.vue'),
            meta: { middleware: 'admin' },
        },
        {
            name: 'combo-management-create',
            path: '/combo-management/create',
            component: () => import('~/pages/ComboManagement/Create.vue'),
            meta: { middleware: 'admin' },
        },
        {
            name: 'combo-management-id',
            path: '/combo-management/:id',
            component: () => import('~/pages/ComboManagement/[id].vue'),
            meta: { middleware: 'admin' },
        },
    ]
};