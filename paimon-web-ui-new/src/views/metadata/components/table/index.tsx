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
import { CreateOutline, RemoveCircleOutline, Warning } from '@vicons/ionicons5'

import { useCatalogStore } from '@/store/catalog'
import { getColumns, type ColumnDTO, deleteColumns, type ColumnParams } from '@/api/models/catalog'
import ColumnsForm from '../columns-form'

export default defineComponent({
  name: 'MetadataTable',
  setup() {
    const { t } = useLocaleHooks()

    const catalogStore = useCatalogStore()
    const [tableColumns, useColumns, { loading }] = getColumns()

    const columns: DataTableColumns<ColumnDTO> = [
      {
        title: 'Column Name',
        key: 'field'
      },
      {
        title: 'Data Type',
        key: 'dataType.type'
      },
      {
        title: 'Nullable',
        key: 'nullAble',
        align: 'center',
        render(rowData) {
          return <n-checkbox checked={rowData.dataType.nullable} />
        }
      },
      {
        title: 'Primary Key',
        key: 'primaryKey',
        align: 'center',
        render(rowData) {
          return <n-checkbox checked={rowData.pk} />
        }
      },
      {
        title: 'Partition Key',
        key: 'partitionKey',
        align: 'center',
        render(rowData) {
          const isChecked = (tableColumns.value?.partitionKey || [])?.includes(rowData.field)
          return <n-checkbox v-model:checked={isChecked} />
        }
      },
      {
        title: 'Default Value',
        key: 'defaultValue',
        align: 'center',
        render(rowData) {
          return rowData.defaultValue || '-'
        }
      },
      {
        title: 'Comment',
        key: 'comment',
        align: 'center',
        render(rowData) {
          return rowData.comment || '-'
        }
      },
      {
        title: 'Operation',
        key: 'operation',
        render(rowData) {
          return (
            <n-space>
              <n-button strong secondary circle>
                {{
                  icon: () => <n-icon component={CreateOutline} />
                }}
              </n-button>
              <n-popconfirm onPositiveClick={() => onDeleteColumn(rowData?.field)}>
                {{
                  default: () => 'Confirm to delete ? ',
                  trigger: () => (
                    <n-button strong secondary circle type="error">
                      {{
                        icon: () => <n-icon component={RemoveCircleOutline} />
                      }}
                    </n-button>
                  ),
                  icon: () => <n-icon color="#EC4C4D" component={Warning} />
                }}
              </n-popconfirm>
            </n-space>
          )
        }
      }
    ]

    const onDeleteColumn = async (columnName: string) => {
      await deleteColumns({
        ...toRaw(catalogStore.currentTable),
        columnName
      } as ColumnParams)

      await onFetchData()
    }

    const onFetchData = async () => {
      useColumns({
        params: catalogStore.currentTable
      })
    }

    watch(() => catalogStore.currentTable, onFetchData)

    onMounted(onFetchData)

    return {
      loading,
      columns,
      tableColumns,
      pagination: {
        pageSize: 10
      },

      onFetchData,
      t
    }
  },
  render() {
    return (
      <n-spin show={this.loading}>
        <n-card title="Common Column">
          {{
            'header-extra': () => (
              <ColumnsForm onConfirm={this.onFetchData} />
            ),
            default: () => (
              <n-data-table
                columns={this.columns}
                data={this.tableColumns?.columns || []}
                pagination={this.pagination}
              />
            )
          }}
        </n-card>
      </n-spin>
    )
  }
})
