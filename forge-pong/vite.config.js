import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import basicSsl from '@vitejs/plugin-basic-ssl'
import { templateCompilerOptions } from '@tresjs/core'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue({
      ...templateCompilerOptions
    }),
    basicSsl() // Enable HTTPS for DeviceMotion on mobile
  ],
  server: {
    https: true,
    host: true // Expose to network
  }
})
