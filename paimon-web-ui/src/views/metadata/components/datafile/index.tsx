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

import { type Datafile, getDataFile } from '@/api/models/catalog'
import { useCatalogStore } from '@/store/catalog'

export default defineComponent({
  name: 'MetadataDataFile',
  setup() {
    const [datafiles, useDataFile, { loading }] = getDataFile()

    const catalogStore = useCatalogStore()

    const columns: DataTableColumns<Datafile> = [
      {
        title: 'Partition',
        key: 'partition',
      },
      {
        title: 'Bucket',
        key: 'bucket',
      },
      {
        title: 'File Path',
        key: 'filePath',
      },
      {
        title: 'File Format',
        key: 'fileFormat',
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
            columns={this.columns}
            data={this.datafiles || []}
          />
        </n-spin>
      </n-card>
    )
  },
})
