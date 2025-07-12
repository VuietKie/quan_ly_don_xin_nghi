import type { RouterConfig } from "nuxt/schema";

export default <RouterConfig>{
    routes: (_routes) => [
        {
            name: 'login',
            path: '/login',
            component: () => import('~/pages/auth/Login.vue'),
            // Không cần featureId cho trang login
        },
        {
            name: 'index',
            path: '/',
            component: () => import('~/pages/dashboard/Dashboard.vue'),
            // Không cần featureId cho dashboard nếu không phân quyền
        },
        {
            name: 'unauthorized',
            path: '/unauthorized',
            component: () => import('~/pages/Unauthorized.vue'),
        },
        {
            name: 'leave-request-create',
            path: '/leave-request/create',
            component: () => import('~/pages/LeaveRequest/Create.vue'),
            meta: { featureId: 1 }
        },
        {
            name: 'leave-request-my-request',
            path: '/leave-request/my-request',
            component: () => import('~/pages/LeaveRequest/MyRequest.vue'),
            meta: { featureId: 2 }
        },
        {
            name: 'leave-request-process',
            path: '/leave-request/process',
            component: () => import('~/pages/LeaveRequest/List.vue'),
            meta: { featureId: 3 }
        },
        {
            name: 'leave-request-detail',
            path: '/leave-request/:id',
            component: () => import('~/pages/LeaveRequest/[id].vue'),
            meta: { featureId: 3 } // Giả sử xem chi tiết cũng là quyền process
        },
        {
            name: 'agenda',
            path: '/agenda',
            component: () => import('~/pages/Agenda/Agenda.vue'),
            meta: { featureId: 4 }
        },
        {
            name: 'permissions',
            path: '/permissions',
            component: () => import('~/pages/Permission/Permission.vue'),
            meta: { featureId: 5 }
        },
        {
            name: 'permission-create',
            path: '/permission/create',
            component: () => import('~/pages/Permission/Create.vue'),
            meta: { featureId: 5 } // Giả sử tạo quyền cũng là feature 5
        },
        {
            name: 'user-management',
            path: '/user-management',
            component: () => import('~/pages/UserManagement/UserManagement.vue'),
            meta: { featureId: 6 }
        },
        {
            name: 'user-management-edit',
            path: '/user-management/edit/:id',
            component: () => import('~/pages/UserManagement/[id].vue'),
            meta: { featureId: 6 }
        },
        {
            name: 'user-management-create',
            path: '/user-management/create',
            component: () => import('~/pages/UserManagement/Create.vue'),
            meta: { featureId: 6 }
        },
        {
            name: 'edit-proffile',
            path: '/auth/edit',
            component: () => import('~/pages/auth/Edit.vue'),
        },
    ]       
};