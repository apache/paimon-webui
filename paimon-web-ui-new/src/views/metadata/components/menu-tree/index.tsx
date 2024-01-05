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
import { Catalog, ChangeCatalog, DataBase } from '@vicons/carbon'
import { DatabaseFilled } from '@vicons/antd'
import { NIcon, type TreeOption } from 'naive-ui'

import { useCatalogStore } from '@/store/catalog'

import CatalogFormButton from '../catalog-form'
import DatabaseFormButton from '../database-form'
import TableFormButton from '../table-form'
import styles from './index.module.scss'

export default defineComponent({
  name: 'MenuTree',
  setup() {
    const { t } = useLocaleHooks()

    const catalogStore = useCatalogStore()
    const catalogStoreRef = storeToRefs(catalogStore)

    const filterValue = ref('')
    const isSearch = ref(false)
    const formType = ref('CATALOG')

    const renderPrefix = ({ option, expanded }: { option: TreeOption; expanded: boolean }) => {
      let icon = expanded ? Catalog : ChangeCatalog
      if (option.type !== 'catalog') {
        icon = expanded ? DataBase : DatabaseFilled
      }

      return h(NIcon, null, {
        default: () => h(icon)
      })
    }

    const renderSuffix = ({ option }: { option: TreeOption }) => {
      switch (option.type) {
        case 'catalog':
          const [catalogId] = option.key?.toString()?.split(' ') || []
          return h(DatabaseFormButton, { catalogId: Number(catalogId) })
        case 'database':
          const [id, name, databaseName] = option.key?.toString()?.split(' ') || []
          return h(TableFormButton, { catalogId: id, catalogName: name, databaseName })
        default:
          return undefined
      }
    }

    onMounted(() => {
      catalogStore.getAllCatalogs(true)
    })

    onUnmounted(catalogStore.resetCurrentTable)

    watch(
      () => filterValue.value,
      async (newValue) => {
        if (!newValue && isSearch.value) {
          isSearch.value = false
          await catalogStore.getAllCatalogs(true)
        }
      }
    )

    const onLoadMenu = async (node: TreeOption) => {
      if (node.type === 'catalog') {
        node.children = await catalogStore.getDatabasesById(node.key as number)
      } else {
        const [catalogId, databaseName] = (node.key as string)?.split(' ') || []
        const params = {
          catalogId: Number(catalogId),
          databaseName
        }
        node.children = (await catalogStore.getTablesByDataBaseId(params)) || []
      }

      return Promise.resolve()
    }

    const nodeProps = ({ option }: { option: TreeOption }) => {
      return {
        onClick() {
          const { type } = option
          if (type === 'table') {
            const [catalogId, catalogName, databaseName, name] =
              (option.key?.toString() || '')?.split(' ') || []
            catalogStore.setCurrentTable({
              catalogId: Number(catalogId),
              catalogName,
              databaseName,
              tableName: name
            })
          }
        }
      }
    }

    const onSearch = async (e: KeyboardEvent) => {
      if (e.code === 'Enter') {
        isSearch.value = true
        await catalogStore.getTablesByDataBaseId({
          name: filterValue.value
        })
      }
    }

    return {
      menuLoading: catalogStoreRef.catalogLoading,
      menuList: catalogStoreRef.catalogs,
      filterValue,
      isSearch,
      formType,
      t,
      onLoadMenu,
      onSearch,
      renderPrefix,
      renderSuffix,
      nodeProps
    }
  },
  render() {
    return (
      <div class={styles.container}>
        <n-card class={styles.card} content-style={'padding:20px 18px;'}>
          <n-space vertical>
            <n-space justify="space-between" align="enter">
              <article>Catalog</article>
              <CatalogFormButton />
            </n-space>
            <n-input
              placeholder={this.t('playground.search')}
              style="width: 100%;"
              v-model:value={this.filterValue}
              v-slots={{
                prefix: () => <n-icon component={Search} />
              }}
              onKeyup={this.onSearch}
            ></n-input>
            <n-spin show={this.menuLoading}>
              <n-tree
                block-line
                expand-on-click
                data={this.menuList}
                defaultExpandAll={this.isSearch}
                nodeProps={this.nodeProps}
                renderSuffix={this.renderSuffix}
                renderSwitcherIcon={this.renderPrefix}
                onLoad={this.onLoadMenu}
              />
            </n-spin>
          </n-space>
        </n-card>
      </div>
    )
  }
})
