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

import { type DataTableColumns } from 'naive-ui'
import { AddCircleOutline, CreateOutline, RemoveCircleOutline } from '@vicons/ionicons5'

import { getOptions, type TableOption } from '@/api/models/catalog'
import { useCatalogStore } from '@/store/catalog'

type RowData = {
  key: number
  columnName: string
  dataType: string
  nullAble: boolean
  primaryKey: boolean
  partitionKey: boolean
  defaultValue?: string
  description?: string
}

export default defineComponent({
  name: 'MetadataOptions',
  setup() {
    const { t } = useLocaleHooks()
    const catalogStore = useCatalogStore()

    const [optionsList, useOptionsList, { loading }] = getOptions()

    const columns: DataTableColumns<TableOption> = [
      {
        title: 'Key',
        key: 'key'
      },
      {
        title: 'Value',
        key: 'value'
      },
      {
        title: 'Operation',
        key: 'operation',
        render() {
          return (
            <n-space>
              <n-button strong secondary circle>
                {{
                  icon: () => <n-icon component={CreateOutline} />
                }}
              </n-button>
              <n-button strong secondary circle type="error">
                {{
                  icon: () => <n-icon component={RemoveCircleOutline} />
                }}
              </n-button>
            </n-space>
          )
        }
      }
    ]

    const onFetchData = async () => {
      useOptionsList({
        params: catalogStore.currentTable
      })
    }

    watch(() => catalogStore.currentTable, onFetchData)

    onMounted(onFetchData)

    return {
      columns,
      data: optionsList.value?.data || [],
      pagination: {
        pageSize: 10
      },
      t
    }
  },
  render() {
    return (
      <n-card title="Options">
        {{
          'header-extra': () => (
            <n-button strong secondary circle>
              {{
                icon: () => <n-icon component={AddCircleOutline} />
              }}
            </n-button>
          ),
          default: () => (
            <n-data-table columns={this.columns} data={this.data} pagination={this.pagination} />
          )
        }}
      </n-card>
    )
  }
})
