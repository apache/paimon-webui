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

import {CloseSharp, CodeSlash, FileTrayFullOutline, Search, ServerOutline} from '@vicons/ionicons5';
import { useCatalogStore } from '@/store/catalog'
import styles from './index.module.scss'
import { NIcon, type TreeOption } from 'naive-ui';
import {DatabaseOutlined} from "@vicons/antd";
import {getColumns} from "@/api/models/catalog";

export default defineComponent({
  name: 'MenuTree',
  setup() {
    const { t } = useLocaleHooks()

    const catalogStore = useCatalogStore()
    const catalogStoreRef = storeToRefs(catalogStore)
    const [tableColumns, useColumns, { loading }] = getColumns()

    const filterValue = ref('')
    const selectedKeys = ref([])

    const renderPrefix = ({ option }: { option: TreeOption }) => {
      let icon = ServerOutline
      switch (option.type) {
        case 'catalog':
          icon = DatabaseOutlined
          break
        case 'database':
          icon = ServerOutline
          break
        case 'table':
          icon = FileTrayFullOutline
      }

      return h(NIcon, null, {
        default: () => h(icon)
      })
    }

    const onLoadMenu = async (node: TreeOption) => {
      if (node.type === 'catalog') {
        node.children = await catalogStore.getDatabasesById(node.key as number)
      } else {
        const [catalogId, catalogName, databaseName] = (node.key as string)?.split(' ') || []
        const params = {
          catalogId: Number(catalogId),
          catalogName,
          databaseName
        }
        node.children = (await catalogStore.getTablesByDataBaseId(params)) || []
      }

      return Promise.resolve()
    }

    const nodeProps = ({ option }: { option: TreeOption }) => {
      return {
        onClick () {
          if (option.children) return
          if (tabData.value.panelsList?.some((item: any) => item.key === option.key)) {
            tabData.value.chooseTab = option.key
            return
          }
          tabData.value.panelsList.push({
            tableName: option.label,
            key: option.key,
            isSaved: false,
            content: option.content
          })
          tabData.value.chooseTab = option.key
        },
      }
    }

    const dataNodeProps = ({ option }: { option: TreeOption }) => {
      return {
        onClick () {
          const { type } = option
          if (type === 'table') {
            isDetailVisible.value = true
            const { catalogId, name, ...tableData } = JSON.parse(option.key?.toString() || '')
            catalogStore.setCurrentTable({
              catalogId: Number(catalogId),
              tableName: name,
              name,
              ...tableData
            })
          }
        },
      }
    }

    const handleTreeSelect = ({ option }: { option: TreeOption }) => {
    }

    // mitt - handle tab choose
    const tabData = ref({}) as any
    const { mittBus }  = getCurrentInstance()!.appContext.config.globalProperties
    mittBus.on('initTabData', (data: any) => {
      tabData.value = data
    })

    const savedQueryList = ref([
      {
        key: 1,
        label: 'test1',
        prefix: () =>
          h(NIcon, {color: '#0066FF'}, {
            default: () => h(CodeSlash)
          }),
        content: ''
      },
      {
        key: 2,
        label: 'test2',
        prefix: () =>
          h(NIcon, {color: '#0066FF'}, {
            default: () => h(CodeSlash)
          }),
        content: ''
      }
    ]) as any

    const recordList = ref([
      {
        key: 3,
        label: 'test3',
        prefix: () =>
          h(NIcon, {color: '#0066FF'}, {
            default: () => h(CodeSlash)
          }),
        content: ''
      },
      {
        key: 4,
        label: 'test4',
        prefix: () =>
          h(NIcon, {color: '#0066FF'}, {
            default: () => h(CodeSlash)
          }),
        content: ''
      }
    ]) as any

    const isDetailVisible = ref(true);
    const handleClose = () => {
      isDetailVisible.value = !isDetailVisible.value;
    }

    const onFetchData = async () => {
      if (catalogStore.currentTable && Object.keys(catalogStore.currentTable).length > 0) {
        useColumns({
          params: catalogStore.currentTable
        })
      }
    }

    watch(() => catalogStore.currentTable, onFetchData)

    onMounted(() => {
      catalogStore.getAllCatalogs(true)
    })

    onMounted(onFetchData)

    const columns = computed(() => tableColumns.value?.columns || []);

    return {
      t,
      filterValue,
      menuList: catalogStoreRef.catalogs,
      onLoadMenu,
      nodeProps,
      dataNodeProps,
      handleTreeSelect,
      renderPrefix,
      handleClose,
      savedQueryList,
      recordList,
      currentTable: catalogStoreRef.currentTable,
      columns,
      isDetailVisible,
      selectedKeys
    }
  },
  render() {
    return (
      <div class={styles.container}>
        <n-card class={styles.card} content-style={'padding:7px 18px;'}>
          <n-tabs default-value="data" justify-content="space-between" type="line" style={'height: 100%'}>
            <n-tab-pane name="data" tab={this.t('playground.data')} style={'height: 100%'}>
              <div class={styles.vertical}>
                <n-input placeholder={this.t('playground.search')}
                         v-model:value={this.filterValue}
                         v-slots={{
                           prefix: () => <n-icon component={Search} />
                         }}
                >
                </n-input>
                <div class={styles.scroll}>
                  <n-scrollbar style={'position: absolute'}>
                    <n-tree
                      block-line
                      expand-on-click
                      selected-keys={this.selectedKeys}
                      on-update:selected-keys={this.handleTreeSelect}
                      data={this.menuList}
                      pattern={this.filterValue}
                      node-props={this.dataNodeProps}
                      onLoad={this.onLoadMenu}
                      render-prefix={this.renderPrefix}
                    />
                  </n-scrollbar>
                </div>
                { this.isDetailVisible && this.currentTable && (
                  <div class={styles['detail-container']}>
                    <n-card style={'border-radius: 0; height: 100%; border-width: 1.4px 0px 0px 0px;'}
                            content-style={'padding:0;'} >
                      <div class={styles['detail-vertical']}>
                        <n-card style={'border: none;'} content-style={'padding:16px 14px;'}>
                          <n-space justify="space-between">
                            <span>{this.currentTable.tableName}</span>
                            <n-button
                              text
                              onClick={this.handleClose}
                              v-slots={{
                                icon: () => <n-icon component={CloseSharp}></n-icon>
                              }}
                            >
                            </n-button>
                          </n-space>
                        </n-card>
                        <n-card style={'border: none; flex:1;'} content-style={'padding:0px 20px;'}>
                          <div style={'height: 100%; position: relative;'}>
                            <n-scrollbar style={'position: absolute;'}>
                              <n-space vertical>
                                {this.columns.map((column, index) => (
                                  <n-space key={index} justify="space-between">
                                    <span>{column.field}</span>
                                    <span>{typeof column.dataType === 'object' ? column.dataType.type : column.dataType}</span>
                                  </n-space>
                                ))}
                              </n-space>
                            </n-scrollbar>
                          </div>
                        </n-card>
                      </div>
                    </n-card>
                  </div>
                )}
              </div>
            </n-tab-pane>
            <n-tab-pane name="saved_query" tab={this.t('playground.saved_query')}>
              <n-space vertical>
                <n-input placeholder={this.t('playground.search')} style="width: 100%;"
                  v-model:value={this.filterValue}
                  v-slots={{
                    prefix: () => <n-icon component={Search} />
                  }}
                >
                </n-input>
                <n-tree
                  block-line
                  expand-on-click
                  selected-keys={this.selectedKeys}
                  on-update:selected-keys={this.handleTreeSelect}
                  data={this.savedQueryList}
                  pattern={this.filterValue}
                  node-props={this.nodeProps}
                />
              </n-space>
            </n-tab-pane>
            <n-tab-pane name="query_record" tab={this.t('playground.query_record')}>
            <n-space vertical>
                <n-input placeholder={this.t('playground.search')} style="width: 100%;"
                  v-model:value={this.filterValue}
                  v-slots={{
                    prefix: () => <n-icon component={Search} />
                  }}
                >
                </n-input>
                <n-tree
                  block-line
                  expand-on-click
                  selected-keys={this.selectedKeys}
                  on-update:selected-keys={this.handleTreeSelect}
                  data={this.recordList}
                  pattern={this.filterValue}
                  node-props={this.nodeProps}
                />
              </n-space>
            </n-tab-pane>
          </n-tabs>
        </n-card>
      </div>
    );
  }
});
