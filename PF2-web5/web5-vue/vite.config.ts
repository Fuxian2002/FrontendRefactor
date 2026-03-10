import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5175,
    strictPort: true, // Force port 5175, fail if busy
    // Force encoding to UTF-8 (removed invalid fs option)
    proxy: {
      '/project': {
        target: 'http://localhost:7086',
        changeOrigin: true,
      },
      '/file': {
        target: 'http://localhost:7086',
        changeOrigin: true,
      }
    }
  }
})
