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

import { useForm } from './use-form'
import { useConfigStore } from '@/store/config'
import i18n from '@/locales'
import backgroundImage from '@/assets/background.png'
import logoImage from '@/assets/logo.svg'
import styles from './index.module.scss'

export default defineComponent({
  name: 'LoginPage',
  setup() {
    const { state, handleLogin } = useForm()

    const configStore = useConfigStore()

    const handleLocale = () => {
      configStore.setCurrentLocale(configStore.getCurrentLocale === 'zh' ? 'en' : 'zh')
      i18n.global.locale.value = configStore.getCurrentLocale === 'zh' ? 'en' : 'zh'
    }

    return {
      configStore,
      handleLogin,
      handleLocale,
      ...toRefs(state)
    }
  },
  render() {
    return <n-layout>
      <n-space justify='center' align='center' class={styles['container']}>
        <n-card bordered={false} content-style={{padding: 0, width: '1200px'}}>
          <n-space justify='start'>
            <img src={backgroundImage} alt='background-image' class={styles['background']} />
            <div class={styles['form-container']}>
              <n-space align='center' justify='center'>
                <img class={styles['logo']} src={logoImage} alt='logo-image'/>
                <h1>Apache Paimon</h1>
              </n-space>
              <n-form
                ref='loginForm'
                model={this.model}
                label-width='auto'
                style={{marginTop: '50px'}}
              >
                <n-form-item
                  label={i18n.global.t('login.username')}
                  path='userName'
                >
                  <n-input
                    clearable
                    v-model={[this.model.username, 'value']}
                    placeholder={i18n.global.t('login.username_tips')}
                    autofocus
                    size='large'
                  />
                </n-form-item>
                <n-form-item
                  label={i18n.global.t('login.password')}
                  path='userPassword'
                >
                  <n-input
                    clearable
                    type='password'
                    v-model={[this.model.password, 'value']}
                    placeholder={i18n.global.t('login.password_tips')}
                    size='large'
                  />
                </n-form-item>
                <n-button
                  size='large'
                  type='primary'
                  disabled={!this.model.password || !this.model.username}
                  style={{ width: '100%' }}
                  onClick={this.handleLogin}
                >
                  {i18n.global.t('login.login')}
                </n-button>
              </n-form>
              <n-space justify='center' style={{marginTop: '80px'}}>
                <n-button
                  quaternary
                  type='primary'
                  style={{width: '100px'}}
                  onClick={() => this.configStore.setCurrentTheme(
                    this.configStore.getCurrentTheme === 'light' ? 'dark' : 'light'
                  )}
                >
                  {i18n.global.t('login.' + String(this.configStore.getCurrentTheme === 'light' ? 'dark' : 'light'))}
                </n-button>
                <n-button
                  quaternary
                  type='primary'
                  style={{width: '100px'}}
                  onClick={this.handleLocale}
                >
                  {this.configStore.getCurrentLocale === 'zh' ? '简体中文' : 'English'}
                </n-button>
              </n-space>
            </div>
          </n-space>
        </n-card>
      </n-space>
    </n-layout>
  }
})