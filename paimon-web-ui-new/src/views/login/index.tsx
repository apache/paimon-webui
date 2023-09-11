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

import { useConfigStore } from '@/store/config'
import { useForm } from '@/views/login/use-form'
import backgroundImage from '@/assets/background.png'
import logoImage from '@/assets/logo.svg'

export default defineComponent({
  name: 'login',
  setup() {
    const { t } = VueI18n.useI18n()
    const configStore = useConfigStore()
    const { state, handleLogin } = useForm()

    return {
      t,
      handleLogin,
      ...toRefs(state)
    }
  },
  render() {
    return <n-layout>
      <n-space class='w-screen h-screen' justify='center' align='center'>
        <n-card bordered={false} content-style={{padding: 0}}>
          <div class='h-4/6 flex'>
            <div class='w-1/2'>
              <img src={backgroundImage} alt='background-image' />
            </div>
            <div class='w-1/2 py-2 px-3'>
              <n-space align='center' justify='center'>
                <img class='h-16 w-16' src={logoImage} alt='logo-image'/>
                <h1 class='text-2xl'>Apache Paimon</h1>
              </n-space>
              <n-form
                ref='loginForm'
                model={this.model}
                rules={this.rules}
                label-placement='left'
                label-width='auto'
              >
                <n-form-item
                  label={this.t('login.username')}
                  label-style={{ color: 'black' }}
                  path='userName'
                >
                  <n-input
                    clearable
                    v-model={[this.model.username, 'value']}
                    placeholder={this.t('login.username_tips')}
                    autofocus
                  />
                </n-form-item>
                <n-form-item
                  label={this.t('login.password')}
                  label-style={{ color: 'black' }}
                  path='userPassword'
                >
                  <n-input
                    clearable
                    type='password'
                    v-model={[this.model.password, 'value']}
                    placeholder={this.t('login.password_tips')}
                  />
                </n-form-item>
              </n-form>
            </div>
          </div>
        </n-card>
      </n-space>
    </n-layout>
  }
})