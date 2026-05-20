import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/auth': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        rewrite: path => path.replace(/^\/auth/, '/api/v1/auth')
      },
      '/family': {
        target: 'http://localhost:8082',
        changeOrigin: true,
        rewrite: path => path.replace(/^\/family/, '/api/v1/families')
      },
      '/calendar': {
        target: 'http://localhost:8083',
        changeOrigin: true,
        rewrite: path => path.replace(/^\/calendar/, '/api/v1/families')
      }
    }
  }
});
