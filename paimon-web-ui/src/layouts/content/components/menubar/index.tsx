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
import { useConfigStore } from '@/store/config'
import type { NavBar } from '@/store/config/type'

export default defineComponent({
  props: {
    menuOptions: {
      type: Array<MenuOption>,
      default: [],
    },
  },
  name: 'MenuBar',
  setup() {
    const configStore = useConfigStore()
    const activeKey = ref<string>('playground')

    const handleUpdateValue = (value: string) => {
      activeKey.value = value
      configStore.setCurrentNavActive(value as NavBar)
    }

    onMounted(() => {
      activeKey.value = configStore.getCurrentNavActive
    })

    return {
      activeKey,
      handleUpdateValue,
    }
  },
  render() {
    return (
      <n-menu
        v-model:value={this.activeKey}
        mode="horizontal"
        options={this.menuOptions}
        on-update:value={this.handleUpdateValue}
      />
    )
  },
})
