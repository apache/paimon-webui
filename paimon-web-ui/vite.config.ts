/* Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License. */

import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import SemiPlugin from "vite-plugin-semi-theme";
import { resolve } from 'path'

export default defineConfig({
  plugins: [
    /*SemiPlugin({
      theme: "@semi-bot/semi-theme-figma"
    }),*/
    react()
  ],
  resolve: {
    alias: {
      '@src': resolve(__dirname, './src'),
      '@assets': resolve(__dirname, './src/assets'),
      '@components': resolve(__dirname, './src/components'),
      '@pages': resolve(__dirname, './src/pages'),
      '@utils': resolve(__dirname, './src/utils'),
      '@config': resolve(__dirname, './src/config'),
      '@mock': resolve(__dirname, './mock'),
      '@api': resolve(__dirname, './src/api')
    }
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:10088',
        changeOrigin: true
      }
    }
  }
})
