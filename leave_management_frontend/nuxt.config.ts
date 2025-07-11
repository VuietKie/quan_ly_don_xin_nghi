// https://nuxt.com/docs/api/configuration/nuxt-config
import tailwindcss from '@tailwindcss/vite';
export default defineNuxtConfig({
    compatibilityDate: '2024-11-01',
    devtools: { enabled: true },
    build: {
        transpile: ['vue-toastification'],
    },

    devServer: {
        port: Number(process.env.NUXT_PUBLIC_PORT),
    },
    runtimeConfig: {
        public: {
            apiBase: process.env.NUXT_PUBLIC_API_BASE,
        },
    },
    css: ['~/assets/css/tailwind.css', '~/assets/css/easydatatable.css'],

    vite: {
        plugins: [tailwindcss()],
    },

    modules: ['shadcn-nuxt', "@pinia/nuxt"],
    shadcn: {
        /**
         * Prefix for all the imported component
         */
        prefix: '',
        /**
         * Directory that the component lives in.
         * @default "./components/ui"
         */
        componentDir: './components/ui',
    },
    app: {
        head: {
          charset: 'utf-8',
          viewport: 'width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no',
          title: 'LEAVE MANAGEMENT',
          meta: [{ name: 'description', content: 'leavemanagement.vn' }],
          noscript: [],
          bodyAttrs: {
            id: 'leavemanagement',
          },
        },
      },
});
