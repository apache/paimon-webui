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

import {
  darkTheme,
  dateEnUS,
  dateZhCN,
  enUS,
  zhCN,
} from 'naive-ui'
import hljs from 'highlight.js/lib/core'
import csharp from 'highlight.js/lib/languages/csharp'
import { useConfigStore } from '@/store/config'
import themes from '@/themes'

hljs.registerLanguage('csharp', csharp)

const log = function (hljs: any) {
  return {
    contains: [
      ...hljs.getLanguage('csharp').contains,
      {
        begin: /Job ID: [^\]]+/,
        relevance: 0,
      },
    ],
  }
}

hljs.registerLanguage('log', log)

export default defineComponent({
  name: 'App',
  setup() {
    const configStore = useConfigStore()
    const theme = computed(() => configStore.getCurrentTheme === 'dark' ? darkTheme : undefined)
    const themeOverrides = computed(() => themes[theme.value ? 'dark' : 'light'])
    const locale = computed(() => configStore.getCurrentLocale)

    return {
      theme,
      themeOverrides,
      locale,
      hljs,
    }
  },
  render() {
    return (
      <n-config-provider
        theme={this.theme}
        theme-overrides={this.themeOverrides}
        locale={this.locale === 'en' ? enUS : zhCN}
        date-locale={this.locale === 'en' ? dateEnUS : dateZhCN}
        style={{ width: '100%', height: '100vh' }}
        hljs={this.hljs}
      >
        <n-message-provider>
          <n-dialog-provider>
            <router-view />
          </n-dialog-provider>
        </n-message-provider>
      </n-config-provider>
    )
  },
})
