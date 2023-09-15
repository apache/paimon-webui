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

import i18n from '@/locales'
import { useConfigStore } from '@/store/config'

export default defineComponent({
  name: 'MenuBar',
  setup() {
    const configStore = useConfigStore()

    const menuOptions = ref([] as any[]) 

    watch(
      () => configStore.getCurrentLocale,
      () => {
        menuOptions.value = [
          {
            label: i18n.global.t('layout.playground'),
            key: 'playground',
          },
          {
            label: i18n.global.t('layout.metadata'),
            key: 'metadata',
          },
          {
            label: i18n.global.t('layout.cdc_ingestion'),
            key: 'cdc_ingestion',
          },
          {
            label: i18n.global.t('layout.system'),
            key: 'system',
          },
        ]
      },
      { immediate: true }
    )

    return {
      activeKey: ref<string | null>('playground'),
      menuOptions
    }
  },
  render () {
    return (
      <n-menu 
        v-model:value={this.activeKey}
        mode="horizontal"
        options={this.menuOptions}
      />
    )
  }
})
