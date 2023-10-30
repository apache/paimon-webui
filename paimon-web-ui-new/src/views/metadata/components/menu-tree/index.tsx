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

import { Search } from '@vicons/ionicons5'

import { useCatalogStore } from '@/store/catalog'

import styles from './index.module.scss'

export default defineComponent({
  name: 'MenuTree',
  setup() {
    const { t } = useLocaleHooks()

    const catalogStore = useCatalogStore()
    const catalogStoreRef = storeToRefs(catalogStore)

    const filterValue = ref('')

    const dropdownMenu = [
      {
        label: t('playground.new_folder'),
        key: 'new_folder',
      },
      {
        label: t('playground.new_file'),
        key: 'new_file',
      },
    ]

    const nodeProps = () => {
      return {
        onClick () {
        },
      }
    }

    onMounted(catalogStore.getAllCatalogs)

    return {
      menuLoading: catalogStoreRef.catalogLoading,
      menuList: catalogStoreRef.catalogs,
      dropdownMenu,
      filterValue,
      t,
      nodeProps,
    }
  },
  render() {
    return (
      <div class={styles.container}>
        <n-card class={styles.card} content-style={'padding:20px 18px;'}>
          <n-space vertical>
            <n-input placeholder={this.t('playground.search')} style="width: 100%;"
              v-model:value={this.filterValue}
              v-slots={{
                prefix: () => <n-icon component={Search} />
              }}
            >
            </n-input>
            <n-spin show={this.menuLoading}>
              <n-tree
                block-line
                expand-on-click
                on-load={() => {}}
                data={this.menuList}
                pattern={this.filterValue}
                node-props={this.nodeProps}
              />
            </n-spin>
          </n-space>
        </n-card>
      </div>
    );
  }
});
