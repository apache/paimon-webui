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

import styles from './index.module.scss'
import type { DataTableInst } from 'naive-ui'
import {useMessage} from "naive-ui";

export default defineComponent({
  name: 'TableResult',
  setup(props, { emit }) {
    const {t} = useLocaleHooks()
    const message = useMessage()

    const tableRef = ref<DataTableInst | null>(null);

    interface TableColumn {
      title: string
      key: string
      fixed?: string
      width?: number
      render?: (row: any, index: number) => string | number | JSX.Element
    }

    const data = ref([])
    const columns = ref<TableColumn[]>([])

    const { mittBus } = getCurrentInstance()!.appContext.config.globalProperties

    const handleResult = (result: any) => {
      if (result && result.resultData) {
        data.value = result.resultData
        if (data.value.length > 0) {
          generateColumns(data.value[0])
        }
      }
    }

    const generateColumns = (sampleObject: any) => {
      const indexColumn: TableColumn = {
        title: '#',
        key: 'index',
        fixed: 'left',
        width: 50,
        render: (row, index) => `${index + 1}`
      }

      const dynamicColumns = Object.keys(sampleObject).map(key => ({
        title: key,
        key: key,
        resizable: true,
        sortable: true
      }))

      columns.value = [indexColumn, ...dynamicColumns]
    }

    mittBus?.on('jobResult', handleResult)
    mittBus?.on('refreshedResult', handleResult)

    mittBus.on('triggerDownloadCsv', () => {
      if (tableRef.value) {
        tableRef.value.downloadCsv({ fileName: 'data-table' })
      }
    })

    mittBus.on('triggerCopyData', () => {
      if (data.value && data.value.length > 0) {
        const jsonData = JSON.stringify(data.value, null, 2)
        navigator.clipboard.writeText(jsonData)
          .then(() => message.success(t('playground.data_copied_successfully')))
          .catch(err => console.error('Failed to copy data: ', err))
      }
    })

    onUnmounted(() => {
      mittBus.off('jobResult', handleResult)
      mittBus.off('refreshedResult', handleResult)
      mittBus.off('triggerDownloadCsv')
      mittBus.off('triggerCopyData')
    });

    return {
      columns,
      data,
      tableRef,
    }
  },
  render() {
    return (
      <div>
        <n-data-table
          ref={(el: any) => { this.tableRef = el }}
          class={styles.table}
          columns={this.columns}
          data={this.data}
          max-height={90}
          v-slots={{
            empty: () => '',
          }}
        />
      </div>
    )
  },
})
