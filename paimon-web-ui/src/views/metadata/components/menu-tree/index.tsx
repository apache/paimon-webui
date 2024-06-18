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

import { Search, Warning } from '@vicons/ionicons5'
import { Catalog, ChangeCatalog, DataBase } from '@vicons/carbon'
import { DatabaseFilled } from '@vicons/antd'
import { NIcon, NInput, type TreeOption, useMessage } from 'naive-ui'

import CatalogFormButton from '../catalog-form'
import DatabaseFormButton from '../database-form'
import TableFormButton from '../table-form'
import styles from './index.module.scss'
import { useCatalogStore } from '@/store/catalog'
import { dropDatabase, dropTable, removeCatalog, renameTable } from '@/api/models/catalog'

export default defineComponent({
  name: 'MenuTree',
  setup() {
    const { t } = useLocaleHooks()
    const dialog = useDialog()
    const message = useMessage()

    const catalogStore = useCatalogStore()
    const catalogStoreRef = storeToRefs(catalogStore)

    const filterValue = ref('')
    const isSearch = ref(false)
    const formType = ref('CATALOG')
    const contextMenuVisible = ref(false)
    const contextMenuPosition = ref({ x: 0, y: 0 })
    const currentOption = ref<TreeOption | null>(null)
    const showConfirm = ref(false)
    const newTableName = ref('')

    const [, useRemoveCatalog] = removeCatalog()
    const [, useDropDatabase] = dropDatabase()

    const renderPrefix = ({ option, expanded }: { option: TreeOption, expanded: boolean }) => {
      let icon = expanded ? Catalog : ChangeCatalog
      if (option.type !== 'catalog')
        icon = expanded ? DataBase : DatabaseFilled

      return h(NIcon, null, {
        default: () => h(icon),
      })
    }

    const renderSuffix = ({ option }: { option: TreeOption }) => {
      switch (option.type) {
        case 'catalog': {
          const [catalogId] = option.key?.toString()?.split(' ') || []
          return h(DatabaseFormButton, { catalogId: Number(catalogId) })
        }
        case 'database': {
          const [id, name, databaseName] = option.key?.toString()?.split(' ') || []
          return h(TableFormButton, { catalogId: Number(id), catalogName: name, databaseName })
        }
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
      },
    )

    const onLoadMenu = async (node: TreeOption) => {
      if (node.type === 'catalog') {
        node.children = await catalogStore.getDatabasesById(node.key as number)
      }
      else {
        const [catalogId, catalogName, databaseName] = (node.key as string)?.split(' ') || []
        const params = {
          catalogId: Number(catalogId),
          catalogName,
          databaseName,
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
            const { catalogId, name, ...tableData } = JSON.parse(option.key?.toString() || '')
            catalogStore.setCurrentTable({
              catalogId: Number(catalogId),
              tableName: name,
              name,
              ...tableData,
            })
          }
        },
        onContextmenu(event: MouseEvent) {
          event.preventDefault()
          contextMenuPosition.value = { x: event.clientX, y: event.clientY }
          currentOption.value = option
          contextMenuVisible.value = true
        },
      }
    }

    async function onDeleteCatalog() {
      const name = currentOption.value?.label || ''
      await useRemoveCatalog({
        params: {
          type: '',
          name,
          warehouse: '',
          options: {
            fileSystemType: 'local',
            endpoint: '',
            accessKey: '',
            secretKey: '',
          },
        },
      })
      catalogStore.getAllCatalogs(true)
      showConfirm.value = false
    }

    async function onDeleteDatabase() {
      const [catalogId,, databaseName] = (currentOption.value?.key as string)?.split(' ') || []
      await useDropDatabase({
        params: {
          catalogId: Number(catalogId),
          name: databaseName,
          description: '',
          ignoreIfExists: false,
          cascade: true,
        },
      })
      catalogStore.getAllCatalogs(true)
      showConfirm.value = false
    }

    async function onDeleteTable() {
      const { catalogName, databaseName, name } = JSON.parse(currentOption.value?.key?.toString() || '')
      await dropTable(catalogName, databaseName, name)
      catalogStore.getAllCatalogs(true)
      showConfirm.value = false
    }

    const onRenameTable = async () => {
      if (!currentOption.value)
        return
      const _dialogInst = dialog.create({
        title: 'Rename Table',
        content: () => h(
          NInput,
          {
            placeholder: 'Input you new table name',
            modelValue: newTableName.value,
            onInput: (e: string) => {
              newTableName.value = e
            },
          },
        ),
        positiveText: t('playground.save'),
        onPositiveClick: async () => {
          if (!newTableName.value || !newTableName.value.trim())
            return message.error('new table name is required')
          _dialogInst.loading = true
          try {
            const { catalogName, databaseName, name: oldTableName } = JSON.parse(currentOption.value?.key?.toString() || '')
            await renameTable(catalogName, databaseName, oldTableName, newTableName.value.trim())
              .then(() => {
                message.success('Rename table successfully')
                catalogStore.getAllCatalogs(true)
              })
          }
          catch (error) {
            console.error('Failed to rename table:', error)
          }
          finally {
            _dialogInst.loading = false
          }
        },
      })
    }

    const onPositiveClick = () => {
      if (!currentOption.value)
        return
      switch (currentOption.value.type) {
        case 'catalog':
          return onDeleteCatalog()
        case 'database':
          return onDeleteDatabase()
        case 'table':
          return onDeleteTable()
      }
    }

    const generateContextMenuItems = () => {
      if (!currentOption.value)
        return []

      const items = []
      switch (currentOption.value.type) {
        case 'catalog':
          items.push({ label: t('metadata.delete_catalog'), key: 'delete-catalog' })
          break
        case 'database':
          items.push({ label: t('metadata.delete_database'), key: 'delete-database' })
          break
        case 'table':
          items.push({ label: t('metadata.rename_table'), key: 'rename-table' })
          items.push({ label: t('metadata.delete_table'), key: 'delete-table' })
          break
      }
      return items
    }

    const handleMenuSelect = (key: string) => {
      switch (key) {
        case 'delete-catalog':
        case 'delete-database':
        case 'delete-table':
          showConfirm.value = true
          break
        case 'rename-table':
          onRenameTable()
          break
      }
      contextMenuVisible.value = false
    }

    const onSearch = async (e: KeyboardEvent) => {
      if (e.code === 'Enter') {
        isSearch.value = true
        await catalogStore.getTablesByDataBaseId({
          name: filterValue.value,
        })
      }
    }

    const handleClickOutside = () => {
      contextMenuVisible.value = false
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
      nodeProps,
      contextMenuVisible,
      contextMenuPosition,
      currentOption,
      generateContextMenuItems,
      handleMenuSelect,
      handleClickOutside,
      onPositiveClick,
      showConfirm,
      newTableName,
    }
  },
  render() {
    return (
      <div class={styles.container}>
        <n-card class={styles.card} content-style="padding:20px 18px;">
          <div class={styles.vertical}>
            <n-space justify="space-between" align="enter">
              <article>Catalog</article>
              <CatalogFormButton />
            </n-space>
            <n-input
              placeholder={this.t('playground.search')}
              style="width: 100%;"
              v-model:value={this.filterValue}
              v-slots={{
                prefix: () => <n-icon component={Search} />,
              }}
              onKeyup={this.onSearch}
            >
            </n-input>
            <div class={styles.scroll}>
              <n-scrollbar style="position: absolute" x-scrollable>
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
                    style="white-space: nowrap;"
                  />
                </n-spin>
              </n-scrollbar>
            </div>
            <n-dropdown
              trigger="manual"
              show={this.contextMenuVisible}
              x={this.contextMenuPosition.x}
              y={this.contextMenuPosition.y}
              options={this.generateContextMenuItems()}
              onSelect={this.handleMenuSelect}
              on-clickoutside={this.handleClickOutside}
            />
            <n-popconfirm
              v-model:show={this.showConfirm}
              positive-text="Confirm"
              negative-text="Cancel"
              onPositiveClick={this.onPositiveClick}
              onNegativeClick={() => { this.showConfirm = false }}
              style={`position: fixed; left: ${this.contextMenuPosition.x}px; top: ${this.contextMenuPosition.y}px; min-width: 160px; padding: 10px; text-align: center;`}
            >
              {{
                default: () => 'Confirm to delete ? ',
                trigger: () => (
                  <n-button style="display: none;" />
                ),
                icon: () => <n-icon color="#EC4C4D" component={Warning} />,
              }}
            </n-popconfirm>
          </div>
        </n-card>
      </div>
    )
  },
})
