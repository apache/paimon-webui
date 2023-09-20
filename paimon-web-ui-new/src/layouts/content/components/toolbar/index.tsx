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

import { LANGUAGES } from '@/locales'
import { useConfigStore } from '@/store/config'
import { LogoGithub, Moon, SunnyOutline, Language } from '@vicons/ionicons5'

// ts-ignore
export default defineComponent({
  name: 'ToolBar',
  setup() {
    const { t, setLanguage } = useLocaleHooks()

    const handleLink = () => {
      window.open('https://github.com/apache/incubator-paimon-webui')
    }

    const configStore = useConfigStore()
    const handleTheme = () => {
      configStore.setCurrentTheme(
        configStore.getCurrentTheme === 'light' ? 'dark' : 'light'
      )
    }

    const handleLanguage = () => {
      const lang = configStore.getCurrentLocale === LANGUAGES.ZH ? LANGUAGES.EN : LANGUAGES.ZH

      configStore.setCurrentLocale(lang)
      setLanguage(lang)
    }

    return {
      t,
      handleLink,
      handleTheme,
      handleLanguage,
      configStore,
      active: ref(false)
    }
  },
  render() {
    return (
      <n-space align="center" size={20}>
        <n-popover trigger="hover" placement="bottom"
          v-slots={{
            trigger: () => (
              <n-icon size="24" onClick={this.handleTheme}>
                {
                  this.configStore.getCurrentTheme === 'light' ? <Moon /> : <SunnyOutline />
                }
              </n-icon>
            )
          }}
        >
          <span>{this.t('layout.' + String(this.configStore.getCurrentTheme === 'light' ? 'dark' : 'light'))}</span>
        </n-popover>
        <n-icon size="24" onClick={this.handleLink}>
          <LogoGithub />
        </n-icon>
        <n-icon size="24" onClick={this.handleLanguage}>
          <Language />
        </n-icon>
      </n-space>
    )
  }
})
