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
import dayjs from 'dayjs'

import { getSchema, type Schema } from '@/api/models/catalog'
import { useCatalogStore } from '@/store/catalog'

import { fieldsColumns, optionsColumns } from './columns'
import styles from './index.module.scss'

export default defineComponent({
  name: 'MetadataSchema',
  setup() {
    const { t } = useLocaleHooks()

    const catalogStore = useCatalogStore()

    const [schemaData, useSchemaInfo, { loading }] = getSchema()

    const columns: DataTableColumns<Schema> = [
      {
        title: '#',
        type: 'expand',
        renderExpand: (row) => {
          const { fields = [], option = [] } = row

          return (
            <div>
              <div class={styles.schema_field_title}>Fields: </div>
              <n-data-table columns={fieldsColumns} data={fields} bordered={false} />
              <div class={styles.schema_field_title}>Options: </div>
              <n-data-table columns={optionsColumns} data={option} bordered={false} />
            </div>
          )
        }
      },
      {
        title: 'ID',
        key: 'schemaId'
      },
      {
        title: 'Partition Keys',
        key: 'partitionKeys'
      },
      {
        title: 'Primary Keys',
        key: 'primaryKeys'
      },
      {
        title: 'Comment',
        key: 'comment',
        align: 'center',
        render: (row) => {
          return row.comment || '-'
        }
      },
      {
        title: 'Update Time',
        key: 'updateTime',
        render: (row) => {
          return dayjs(row.updateTime).format('YYYY-MM-DD HH:mm:ss')
        }
      }
    ]

    const onFetchData = async () => {
      useSchemaInfo({
        params: catalogStore.currentTable
      })
    }

    watch(() => catalogStore.currentTable, onFetchData)

    onMounted(onFetchData)

    return {
      columns,
      schemaData,
      loading,
      t
    }
  },
  render() {
    return (
      <n-card>
        <n-spin show={this.loading}>
          <n-data-table
            row-key={(rowData: Schema) => rowData.schemaId}
            columns={this.columns}
            data={this.schemaData || []}
            max-height="calc(100vh - 260px)"
          />
        </n-spin>
      </n-card>
    )
  }
})
