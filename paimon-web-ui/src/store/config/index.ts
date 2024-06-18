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

import type { Menu, NavBar, Theme } from './type'
import { LANGUAGES } from '@/locales'

export const useConfigStore = defineStore({
  id: 'config',
  state: (): { theme: Theme, locale: LANGUAGES, navActive: NavBar | null, menuActive: Menu } => ({
    theme: 'light',
    locale: LANGUAGES.ZH,
    navActive: 'playground',
    menuActive: 'Query',
  }),
  persist: true,
  getters: {
    getCurrentLocale(): LANGUAGES {
      return this.locale
    },
    getCurrentTheme(): Theme {
      return this.theme
    },
    getCurrentNavActive(): NavBar | null {
      return this.navActive
    },
    getCurrentMenuActive(): Menu {
      return this.menuActive
    },
  },
  actions: {
    setCurrentLocale(locale: LANGUAGES): void {
      this.locale = locale
    },
    setCurrentTheme(theme: Theme): void {
      this.theme = theme
    },
    setCurrentNavActive(navActive: NavBar | null): void {
      this.navActive = navActive
    },
    setCurrentMenuActive(menuActive: Menu): void {
      this.menuActive = menuActive
    },
  },
})
