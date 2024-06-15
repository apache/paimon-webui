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
import { useUserStore } from '@/store/user'
import type { ResponseOptions } from '@/api/types'
import { useConfigStore } from '@/store/config'

export function useForm() {
  const router: Router = useRouter()
  const userStore = useUserStore()
  const configStore = useConfigStore()
  const state = reactive({
    loginForm: ref(),
    model: {
      username: '',
      password: '',

    },
  })

  const handleLogin = async () => {
    state.loginForm.validate(async (errors: Array<FormValidationError>) => {
      if (!errors) {
        await onLogin({
          username: state.model.username,
          password: state.model.password,
          ldapLogin: false,
          rememberMe: true,
        }).then((res: ResponseOptions<any>) => {
          userStore.setUsername(res.data.user.username)
          userStore.setNickname(res.data.user.nickname)
          configStore.setCurrentNavActive(null)
          userStore.setAdmin(res.data.user.admin)
          userStore.setMenus(res.data.sysMenuList?.filter((e: any) => e.type === 'C')?.map((e: any) => {
            return e.menuName
          }) || [])
          userStore.setDirectories(res.data.sysMenuList?.filter((e: any) => e.type === 'M')?.map((e: any) => {
            return e.menuName
          }) || [])
        })
        router.push({ path: '/' })
      }
    })
  }

  return {
    state,
    handleLogin,
  }
}
