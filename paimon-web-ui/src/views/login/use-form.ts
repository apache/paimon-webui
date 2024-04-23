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

import type { FormValidationError } from 'naive-ui'
import type { Router } from 'vue-router'
import { onLogin } from '@/api'

export function useForm() {
  const router: Router = useRouter()

  const state = reactive({
    loginForm: ref(),
    model: {
      username: '',
      password: ''
    }
  })

  const handleLogin = () => {
    state.loginForm.validate(async (errors: Array<FormValidationError>) => {
      if (!errors) {
        onLogin({
          params: {
            username: state.model.username,
            password: state.model.password,
            ldapLogin: false,
            rememberMe: true
          }
        })
        router.push({ path: '/' })
      }
    })
  }

  return {
    state,
    handleLogin
  }
}
