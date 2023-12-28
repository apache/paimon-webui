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

import { FolderOutline, FolderOpenOutline, Search } from '@vicons/ionicons5'
import { NIcon, type TreeOption } from 'naive-ui'

import { useCatalogStore } from '@/store/catalog'

import CatalogFormButton from './catalog-form'
import DatabaseFormButton from './database-form'
import styles from './index.module.scss'

export default defineComponent({
  name: 'MenuTree',
  setup() {
    const { t } = useLocaleHooks()

    const catalogStore = useCatalogStore()
    const catalogStoreRef = storeToRefs(catalogStore)

    const filterValue = ref('')
    const showMoal = ref(false)
    const modalRef = ref()
    const formType = ref('CATALOG')

    const handleConfirm = async () => {
      await modalRef.value.formRef.validate()
    }

    const updatePrefixWithExpanded = (
      _keys: Array<string | number>,
      _option: Array<TreeOption | null>,
      meta: {
        node: TreeOption | null
        action: 'expand' | 'collapse' | 'filter'
      }
    ) => {
      if (!meta.node) return
      switch (meta.action) {
        case 'expand':
          meta.node.prefix = () =>
            h(NIcon, null, {
              default: () => h(FolderOpenOutline)
            })
          break
        case 'collapse':
          meta.node.prefix = () =>
            h(NIcon, null, {
              default: () => h(FolderOutline)
            })
          break
      }
    }

    const handleOpenModal = () => {
      showMoal.value = true
    }

    const handleCloseModal = () => {
      formType.value = 'CATALOG'
      showMoal.value = false
    }

    const renderSuffix = ({ option }: { option: TreeOption }) => {
      switch (option.type) {
        case 'catalog':
          return 11;
        case 'database':
          const [catalogId, databaseName] = option.key?.toString()?.split(' ') || [];
          return h(DatabaseFormButton, { catalogId: Number(catalogId), databaseName })
        default:
          return undefined
      }
    }

    onMounted(catalogStore.getAllCatalogs)

    onUnmounted(catalogStore.resetCurrentTable)

    const onLoadMenu = async (node: TreeOption) => {
      if (node.type === 'catalog') {
        node.children = await catalogStore.getDatabasesById(node.key as number)
      } else {
        const [catalogId, databaseName] = (node.key as string)?.split(' ') || []
        const params = {
          catalogId: Number(catalogId),
          databaseName
        }
        node.children = await catalogStore.getTablesByDataBaseId(params)
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

    return {
      menuLoading: catalogStoreRef.catalogLoading,
      menuList: catalogStoreRef.catalogs,
      filterValue,
      formType,
      showMoal,
      t,
      onLoadMenu,
      updatePrefixWithExpanded,
      renderSuffix,
      nodeProps,
      handleOpenModal,
      handleCloseModal,
      handleConfirm
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
            ></n-input>
            <n-spin show={this.menuLoading}>
              <n-tree
                block-line
                expand-on-click
                nodeProps={this.nodeProps}
                renderSuffix={this.renderSuffix}
                onUpdate:expandedKeys={this.updatePrefixWithExpanded}
                onLoad={this.onLoadMenu}
                data={this.menuList}
                pattern={this.filterValue}
              />
            </n-spin>
          </n-space>
        </n-card>
      </div>
    )
  }
})
