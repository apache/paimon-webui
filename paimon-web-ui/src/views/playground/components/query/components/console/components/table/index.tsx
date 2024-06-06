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

import type { DataTableInst } from 'naive-ui'
import { useMessage } from 'naive-ui'
import styles from './index.module.scss'
import { useJobStore } from '@/store/job'

export default defineComponent({
  name: 'TableResult',
  props: {
    maxHeight: {
      type: Number,
      default: 150,
    },
  },
  setup(props) {
    const { t } = useLocaleHooks()
    const message = useMessage()
    const jobStore = useJobStore()
    const scrollX = ref('100%')
    const tableContainer = ref<HTMLElement | null>(null)
    const tableRef = ref<DataTableInst | null>(null)
    const maxTableHeight = ref(0)

    interface TableColumn {
      title: string
      key: string
      fixed?: string
      width?: number
      originalWidth?: number
      render?: (row: any, index: number) => string | number | JSX.Element
    }

    const initialData = computed(() => jobStore.getCurrentJob?.resultData || [])
    const refreshedData = computed(() => jobStore.getJobResultData?.resultData || [])
    const data = computed(() => refreshedData.value.length > 0 ? refreshedData.value : initialData.value)

    const columns = computed(() => {
      if (data.value.length > 0)
        return generateColumns(data.value[0])

      return []
    })

    const { mittBus } = getCurrentInstance()!.appContext.config.globalProperties

    function generateColumns(sampleObject: any) {
      const indexColumn: TableColumn = {
        title: '#',
        key: 'index',
        fixed: 'left',
        width: 50,
        render: (_, index) => `${index + 1}`,
      }

      const dynamicColumns = Object.keys(sampleObject).map((key) => {
        const maxContentWidth = Math.max(...data.value.map(item => item[key]?.toString().length || 0))
        const maxTitleWidth = key.length * 10
        const originalWidth = Math.max(100, maxContentWidth * 10, maxTitleWidth)
        return {
          title: key,
          key,
          width: originalWidth,
          originalWidth,
          resizable: true,
          sortable: true,
        }
      })

      return [indexColumn, ...dynamicColumns]
    }

    mittBus.on('triggerDownloadCsv', () => {
      if (tableRef.value)
        tableRef.value?.downloadCsv({ fileName: 'data-table' })
    })

    mittBus.on('triggerCopyData', () => {
      if (data.value && data.value.length > 0) {
        const jsonData = JSON.stringify(data.value, null, 2)
        navigator.clipboard.writeText(jsonData)
          .then(() => message.success(t('playground.data_copied_successfully')))
          .catch(err => console.error('Failed to copy data: ', err))
      }
    })

    const updateTableWidth = () => {
      if (tableContainer.value) {
        const totalColumnWidth = columns.value.reduce((acc, col) => acc + (col.originalWidth || 100), 0)
        if (totalColumnWidth > tableContainer.value.clientWidth) {
          scrollX.value = `${totalColumnWidth}px`
          columns.value.forEach((col) => {
            if (col.originalWidth !== undefined)
              col.width = col.originalWidth
          })
        }
        else {
          scrollX.value = ''
          columns.value.forEach((col) => {
            col.width = undefined
          })
        }
      }
    }

    watchEffect(updateTableWidth)

    watch(data, async (newData) => {
      if (newData && newData.length > 0) {
        await nextTick()
        if (tableContainer.value) {
          const headerElement = tableContainer.value.querySelector('.n-data-table-base-table-header')
          if (headerElement) {
            const headerHeight = headerElement.clientHeight
            maxTableHeight.value = Math.max(0, props.maxHeight - headerHeight)
          }
        }
      }
    }, { immediate: true })

    onMounted(() => {
      nextTick(() => {
        updateTableWidth()
        window.addEventListener('resize', updateTableWidth)
      })
    })

    onUnmounted(() => {
      mittBus.off('triggerDownloadCsv')
      mittBus.off('triggerCopyData')
      window.removeEventListener('resize', updateTableWidth)
    })

    return {
      columns,
      data,
      tableRef,
      tableContainer,
      scrollX,
      maxHeight: props.maxHeight,
      maxTableHeight,
    }
  },
  render() {
    return (
      <div
        ref={(el: any) => { this.tableContainer = el }}
        style={{ height: `${this.maxHeight}px` }}
      >
        <n-data-table
          ref={(el: any) => { this.tableRef = el }}
          class={styles.table}
          columns={this.columns}
          data={this.data}
          max-height={`${this.maxTableHeight}px`}
          scroll-x={this.scrollX || undefined}
          v-slots={{
            empty: () => '',
          }}
        />
      </div>
    )
  },
})
