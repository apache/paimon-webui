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

import { KeepAlive } from 'vue'
import NavBar from './components/topbar'
import styles from './index.module.scss'
import SideBar from './components/sidebar'
import { useData } from './use-data'
import { useConfigStore } from '@/store/config'
import { usePermissionStore } from '@/store/permission'
import { useUserStore } from '@/store/user'

export default defineComponent({
  name: 'ContentPage',
  setup() {
    const permissionStore = usePermissionStore()
    const configStore = useConfigStore()
    const userStore = useUserStore()
    const { menuOptions, state } = useData()
    const isAdmin = userStore.getAdmin
    const userMenus = userStore.getMenus
    const userDirectories = userStore.getDiresctoies
    const showDirectories = ref(menuOptions.value?.filter(e => isAdmin || userDirectories.includes(e.menuName)))
    const getSideOption = (state: any) => {
      const activeNavKey = configStore.getCurrentNavActive
      state.sideMenuOptions = showDirectories.value.find((m: any) => m.key === activeNavKey)?.sideMenuOptions?.filter((e) => {
        return isAdmin || userMenus?.includes(e.menuName)
      }) || []
      state.isShowSided = state.sideMenuOptions && state.sideMenuOptions.length
    }

    onMounted(permissionStore.getPermissionList)

    getSideOption(state)

    watch(
      () => configStore.getCurrentNavActive,
      () => {
        getSideOption(state)
      },
    )

    return {
      ...toRefs(state),
      showDirectories,
    }
  },
  render() {
    return (
      <div class={styles.container}>
        <n-layout style="height: 100%">
          <n-layout-header style="height: 64px;" bordered>
            <NavBar headerMenuOptions={this.showDirectories}></NavBar>
          </n-layout-header>
          <n-layout has-sider position="absolute" style="top: 64px">
            {
              this.isShowSided && (
                <SideBar
                  sideMenuOptions={this.sideMenuOptions}
                />
              )
            }
            <n-layout-content content-style="height: calc(100vh - 64px);">
              <router-view v-slots={{
                default: (props: any) => {
                  const Comp = props.Component
                  return (
                    <KeepAlive>
                      <Comp />
                    </KeepAlive>
                  )
                },
              }}
              />
            </n-layout-content>
          </n-layout>
        </n-layout>
      </div>
    )
  },
})
