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

import dayjs from 'dayjs'
import { type Datafile, getDataFile } from '@/api/models/catalog'
import { useCatalogStore } from '@/store/catalog'

export default defineComponent({
  name: 'MetadataDataFile',
  setup() {
    const [datafiles, useDataFile, { loading }] = getDataFile()

    const catalogStore = useCatalogStore()

    function parseKeyValueStringToJsonString(dataStr: string) {
      const result: { [key: string]: string } = {}
      const cleanStr = dataStr.replace(/^\{|\}$/g, '')
      const keyValuePairs = cleanStr.split(',')
      keyValuePairs.forEach((pair) => {
        const [key, value] = pair.split('=').map(item => item.trim())
        result[key] = value
      })
      return JSON.stringify(result, null, 2)
    }

    const columns: DataTableColumns<Datafile> = [
      {
        title: '#',
        type: 'expand',
        renderExpand: (row) => {
          const nullValueJsonString = parseKeyValueStringToJsonString(row.nullValueCounts || '')
          const minValueStatsJsonString = parseKeyValueStringToJsonString(row.minValueStats || '')
          const maxValueStatsJsonString = parseKeyValueStringToJsonString(row.maxValueStats || '')

          return (
            <div>
              <div>Null Value Counts: </div>
              <pre>{nullValueJsonString}</pre>
              <div>Min Value Stats: </div>
              <pre>{minValueStatsJsonString}</pre>
              <div>Max Value Stats: </div>
              <pre>{maxValueStatsJsonString}</pre>
            </div>
          )
        },
      },
      {
        title: 'File Path',
        key: 'filePath',
        width: 420,
      },
      {
        title: 'Partition',
        key: 'partition',
        width: 120,
      },
      {
        title: 'Bucket',
        key: 'bucket',
        width: 120,
      },
      {
        title: 'File Format',
        key: 'fileFormat',
        width: 120,
      },
      {
        title: 'SchemaId',
        key: 'schemaId',
        width: 120,
      },
      {
        title: 'Level',
        key: 'level',
        width: 80,
      },
      {
        title: 'Record Count',
        key: 'recordCount',
        width: 140,
      },
      {
        title: 'File Size In Bytes',
        key: 'fileSizeInBytes',
        width: 160,
      },
      {
        title: 'Min Key',
        key: 'minKey',
        width: 160,
      },
      {
        title: 'Max Key',
        key: 'maxKey',
        width: 160,
      },
      {
        title: 'Min Sequence Number',
        key: 'minSequenceNumber',
        width: 180,
      },
      {
        title: 'Max Sequence Number',
        key: 'maxSequenceNumber',
        width: 180,
      },
      {
        title: 'Creation Time',
        key: 'creationTime',
        width: 180,
        render: (row) => {
          return dayjs(row.creationTime).format('YYYY-MM-DD HH:mm:ss')
        },
      },
    ]

    const onFetchData = async () => {
      useDataFile({
        params: catalogStore.currentTable,
      })
    }

    watch(() => catalogStore.currentTable, onFetchData)

    onMounted(onFetchData)

    return {
      columns,
      datafiles,
      loading,
    }
  },
  render() {
    return (
      <n-card>
        <n-spin show={this.loading}>
          <n-data-table
            row-key={(rowData: Datafile) => rowData.filePath}
            columns={this.columns}
            data={this.datafiles || []}
            max-height="calc(100vh - 280px)"
            scroll-x="2200px"
          />
        </n-spin>
      </n-card>
    )
  },
})
