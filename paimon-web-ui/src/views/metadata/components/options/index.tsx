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

import type { DataTableColumns } from 'naive-ui'
import { RemoveCircleOutline, Warning } from '@vicons/ionicons5'

import OptionsForm from '../options-form'
import OptionsEditForm from '../options-form/edit'
import { type TableOption, deleteOption, getOptions } from '@/api/models/catalog'
import { useCatalogStore } from '@/store/catalog'

export default defineComponent({
  name: 'MetadataOptions',
  setup() {
    const catalogStore = useCatalogStore()

    const [optionsList, useOptionsList, { loading }] = getOptions()
    const [, useDelete] = deleteOption()

    const columns: DataTableColumns<TableOption> = [
      {
        title: 'Key',
        key: 'key',
      },
      {
        title: 'Value',
        key: 'value',
      },
      {
        title: () => <div style={{ paddingRight: '6px' }}>Operation</div>,
        key: 'operation',
        align: 'right',
        render(rowData) {
          return (
            <n-space justify="end">
              <OptionsEditForm onConfirm={onFetchData} option={rowData} />
              <n-popconfirm onPositiveClick={() => onDeleteOption(rowData?.key)}>
                {{
                  default: () => 'Confirm to delete ? ',
                  trigger: () => (
                    <n-button strong secondary circle type="error">
                      {{
                        icon: () => <n-icon component={RemoveCircleOutline} />,
                      }}
                    </n-button>
                  ),
                  icon: () => <n-icon color="#EC4C4D" component={Warning} />,
                }}
              </n-popconfirm>
            </n-space>
          )
        },
      },
    ]

    async function onFetchData() {
      useOptionsList({
        params: catalogStore.currentTable,
      })
    }

    async function onDeleteOption(optionKey: string) {
      await useDelete({
        config: {
          params: {
            ...toRaw(catalogStore.currentTable),
            key: optionKey,
          },
        },
      })

      await onFetchData()

      return Promise.resolve(true)
    }

    watch(() => catalogStore.currentTable, onFetchData)

    onMounted(onFetchData)

    return {
      columns,
      optionsList,
      loading,
      pagination: {
        pageSize: 10,
      },

      onFetchData,
    }
  },
  render() {
    return (
      <n-spin show={this.loading}>
        <n-card title="Options">
          {{
            'header-extra': () => (
              <OptionsForm onConfirm={this.onFetchData} />
            ),
            'default': () => (
              <n-data-table
                columns={this.columns}
                data={this.optionsList || []}
                pagination={this.pagination}
              />
            ),
          }}
        </n-card>
      </n-spin>
    )
  },
})
