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

import { getSnapshot } from '@/api/models/catalog'

type RowData = {
  snapshotId: number
  schemaId: number
  commitIdentifier: number
  commitKind: string
  commitTime: string
}


export default defineComponent({
  name: 'MetadataSnapshot',
  setup() {
    const { t } = useLocaleHooks()
    const [, useSnapshot, { loading }] = getSnapshot()

    const data: RowData[] = [
      {
        snapshotId: 0,
        schemaId: 2,
        commitIdentifier: 3,
        commitKind: 'APPEND',
        commitTime: '2023-10-30 10:00:00'
      },
      {
        snapshotId: 1,
        schemaId: 1,
        commitIdentifier: 1,
        commitKind: 'APPEND',
        commitTime: '2023-10-28 11:08:14'
      },
    ]

    const columns: DataTableColumns<RowData> = [
      {
        title: 'Snapshot ID',
        key: 'snapshotId'
      },
      {
        title: 'Schema ID',
        key: 'schemaId'
      },
      {
        title: 'Commit Identifier',
        key: 'commitIdentifier'
      },

      {
        title: 'Commit Kind',
        key: 'commitKind'
      },
      {
        title: 'Commit Time',
        key: 'commitTime'
      }
    ]

    onMounted(() => {
      useSnapshot({
        params: {
          catalogId: -1632763902,
          catalogName: "streaming_warehouse",
          databaseName: "ods",
          tableName: "t_user"
        }
      })
    })

    return {
      columns,
      data,
      loading,
      t,
    }
  },
  render() {
    return (
      <n-card>
        <n-spin show={this.loading}>
          <n-data-table
            columns={this.columns}
            data={this.data}
          />
        </n-spin>
      </n-card>
    );
  },
});
