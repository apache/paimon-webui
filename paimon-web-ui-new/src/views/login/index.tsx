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
import { LANGUAGES } from '@/locales'
import logoImage from '@/assets/logo.svg'
import styles from './index.module.scss'

export default defineComponent({
  name: 'LoginPage',
  setup() {
    const { t, setLanguage } = useLocaleHooks()
    const { state, handleLogin } = useForm()

    const configStore = useConfigStore()

    const handleLocale = () => {
      const lang = configStore.getCurrentLocale === LANGUAGES.ZH ? LANGUAGES.EN : LANGUAGES.ZH
      
      configStore.setCurrentLocale(lang)
      setLanguage(lang)
    }

    return {
      configStore,
      t,
      handleLogin,
      handleLocale,
      ...toRefs(state)
    }
  },
  render() {
    return <n-layout>
      <n-space justify='center' align='center' class={styles['container']}>
        <n-card bordered={false} content-style={{padding: 0, width: '600px'}}>
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
                label={this.t('login.username')}
                path='userName'
              >
                <n-input
                  clearable
                  v-model={[this.model.username, 'value']}
                  placeholder={this.t('login.username_tips')}
                  autofocus
                  size='large'
                />
              </n-form-item>
              <n-form-item
                label={this.t('login.password')}
                path='userPassword'
              >
                <n-input
                  clearable
                  type='password'
                  v-model={[this.model.password, 'value']}
                  placeholder={this.t('login.password_tips')}
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
                {this.t('login.login')}
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
                {this.t('login.' + String(this.configStore.getCurrentTheme === 'light' ? 'dark' : 'light'))}
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
        </n-card>
      </n-space>
    </n-layout>
  }
})
