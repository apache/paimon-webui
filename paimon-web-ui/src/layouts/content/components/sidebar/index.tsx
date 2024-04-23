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


import type { MenuOption } from 'naive-ui/es/menu/src/interface'
import { defineComponent, ref, type PropType } from 'vue'
import {  useRouter } from 'vue-router'
import type { Router } from 'vue-router'

const Sidebar = defineComponent({
  name: 'Sidebar',
  props: {
    sideMenuOptions: {
      type: Array<MenuOption>,
      default: []
    },
  },
  setup() {
    const collapsedRef = ref(false)

    const router: Router = useRouter()

    const handleMenuClick = (key: string) => {
      router.push({
        path: `${key}`,
      })
    }

    return { collapsedRef, handleMenuClick }
  },
  render() {
    return (
      <n-layout-sider 
        bordered
        nativeScrollbar={false}
        show-trigger='bar'
        collapse-mode='width'
        collapsed={this.collapsedRef}
        onCollapse={() => (this.collapsedRef = true)}
        onExpand={() => (this.collapsedRef = false)}
      >
        <n-menu
          options={this.sideMenuOptions}
          onUpdateValue={this.handleMenuClick}
        />
      </n-layout-sider >
    )
  }
})

export default Sidebar
