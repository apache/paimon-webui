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

import { Language, LogoGithub, Moon, SunnyOutline } from '@vicons/ionicons5'
import type { Router } from 'vue-router'
import { NInput } from 'naive-ui'
import { LANGUAGES } from '@/locales'
import { useConfigStore } from '@/store/config'
import { onLogout } from '@/api/models/login'
import { useUserStore } from '@/store/user'
import { changePassword } from '@/api/models/user'
import type { UserDTO } from '@/api/models/user/types'

// ts-ignore
export default defineComponent({
  name: 'ToolBar',
  setup() {
    const router: Router = useRouter()
    const { t, setLanguage } = useLocaleHooks()
    const dialog = useDialog()
    const message = useMessage()
    const newPassword = ref('')

    const handleLink = () => {
      window.open('https://github.com/apache/paimon-webui')
    }

    const configStore = useConfigStore()
    const userStore = useUserStore()
    const nickname = ref(userStore.nickname)
    const handleTheme = () => {
      configStore.setCurrentTheme(
        configStore.getCurrentTheme === 'light' ? 'dark' : 'light',
      )
    }

    const handleLanguage = () => {
      const lang = configStore.getCurrentLocale === LANGUAGES.ZH ? LANGUAGES.EN : LANGUAGES.ZH

      configStore.setCurrentLocale(lang)
      setLanguage(lang)
    }

    const handleLogout = () => {
      onLogout().then(() => {
        router.push({ path: '/login' })
      })
    }

    const handleChangePassword = () => {
      const _dialogInst = dialog.create({
        title: 'Change Password',
        content: () => h(
          NInput,
          {
            placeholder: 'Input you new password',
            modelValue: newPassword.value,
            onInput: (e: string) => {
              newPassword.value = e
            },
          },
        ),
        positiveText: t('login.modify'),
        onPositiveClick: async () => {
          if (!newPassword.value || !newPassword.value.trim())
            return message.error('new password is required')

          const user: UserDTO = {
            id: userStore.getUserId,
            username: userStore.getUsername || '',
            password: newPassword.value,
            enabled: true,
          }

          _dialogInst.loading = true
          try {
            await changePassword(user)
              .then(() => {
                message.success('Change password successfully')
                handleLogout()
              })
          }
          catch (error) {
            console.error('Failed to change password:', error)
          }
          finally {
            _dialogInst.loading = false
          }
        },
      })
    }

    const avatarMenuOptions = [
      {
        label: t('login.logout'),
        key: 'log-out',
        props: {
          onClick: () => {
            handleLogout()
          },
        },
      },
      {
        label: t('login.change_password'),
        key: 'change-password',
        props: {
          onClick: () => {
            handleChangePassword()
          },
        },
      },
    ]

    return {
      t,
      handleLink,
      handleTheme,
      handleLanguage,
      handleLogout,
      configStore,
      active: ref(false),
      nickname,
      avatarMenuOptions,
    }
  },
  render() {
    return (
      <n-space align="center" size={20}>
        <n-popover
          trigger="hover"
          placement="bottom"
          v-slots={{
            trigger: () => (
              <n-icon size="24" onClick={this.handleTheme}>
                {
                  this.configStore.getCurrentTheme === 'light' ? <Moon /> : <SunnyOutline />
                }
              </n-icon>
            ),
          }}
        >
          <span>{this.t(`layout.${String(this.configStore.getCurrentTheme === 'light' ? 'dark' : 'light')}`)}</span>
        </n-popover>
        <n-popover
          trigger="hover"
          placement="bottom"
          v-slots={{
            trigger: () => (
              <n-icon size="24" onClick={this.handleLink}>
                <LogoGithub />
              </n-icon>
            ),
          }}
        >
          <span>GitHub</span>
        </n-popover>
        <n-popover
          trigger="hover"
          placement="bottom"
          v-slots={{
            trigger: () => (
              <n-icon size="24" onClick={this.handleLanguage}>
                <Language />
              </n-icon>
            ),
          }}
        >
          <span>{this.configStore.getCurrentLocale === LANGUAGES.ZH ? 'Switch to English' : '切换到中文'}</span>
        </n-popover>
        <n-dropdown trigger="click" options={this.avatarMenuOptions}>
          <n-avatar round>
            {' '}
            {this.nickname?.slice(0, 1)}
          </n-avatar>
        </n-dropdown>
      </n-space>
    )
  },
})
