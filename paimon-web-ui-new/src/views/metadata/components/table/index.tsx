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
  name: 'MetadataTable',
  setup() {
    const { t } = useLocaleHooks()

    const data: RowData[] = [
      {
        key: 0,
        columnName: 'ID',
        dataType: 'INT',
        nullAble: true,
        primaryKey: false,
        partitionKey: false,
      },
      {
        key: 1,
        columnName: 'Name',
        dataType: 'STRING',
        nullAble: false,
        primaryKey: true,
        partitionKey: false
      },
      {
        key: 2,
        columnName: 'Description',
        dataType: 'STRING',
        nullAble: false,
        primaryKey: true,
        partitionKey: false,
        defaultValue: '123'
      }
    ]

    const columns: DataTableColumns<RowData> = [
      {
        title: 'Column Name',
        key: 'columnName'
      },
      {
        title: 'Data Type',
        key: 'dataType'
      },
      {
        title: 'Nullable',
        key: 'nullAble',
        align: 'center',
        render(rowData) {
            return <n-checkbox v-model:checked={rowData.nullAble} />
        },
      },
      {
        title: 'Primary Key',
        key: 'primaryKey',
        align: 'center',
        render(rowData) {
          return <n-checkbox v-model:checked={rowData.primaryKey} />
       },
      },
      {
        title: 'Partition Key',
        key: 'partitionKey',
        align: 'center',
        render(rowData) {
          return <n-checkbox v-model:checked={rowData.partitionKey} />
       },
      },
      {
        title: 'Default Value',
        key: 'defaultValue',
        align: 'center',
        render(rowData) {
            return rowData.defaultValue || '-'
        },
      },
      {
        title: 'Description',
        key: 'description',
        align: 'center',
        render(rowData) {
            return rowData.defaultValue || '-'
        },
      },
      {
        title: 'Operate',
        key: 'actions',
        render() {
          return <n-space>
            <n-button strong secondary circle>
              {{
                icon: () => <n-icon component={CreateOutline} />,
              }}
            </n-button>
            <n-button strong secondary circle type="error">
              {{
                icon: () => <n-icon component={RemoveCircleOutline} />,
              }}
            </n-button>
          </n-space>
        }
      }
    ]

    return {
      columns,
      data,
      pagination: {
        pageSize: 10
      },
      t,
    }
  },
  render() {
    return (
      <n-card title='Common Column'>
        {{
          'header-extra': () => (
             <n-button strong secondary circle>
              {{
                icon: () => <n-icon component={AddCircleOutline} />,
              }}
            </n-button>
          ),
          default: () => (
            <n-data-table
              columns={this.columns}
              data={this.data}
              pagination={this.pagination}
            />
          )
        }}
      </n-card>
    );
  },
});
