import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';

export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      '/api': {
        target: 'https://roomreservation.cleverapps.io',
        changeOrigin: true,
        secure: false,
      }
    }
  }
});