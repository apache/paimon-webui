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

import { getManifest } from '@/api/models/catalog'

type RowData = {
  fileName: string
  fileSize: number
  numAddFiles: number
}


export default defineComponent({
  name: 'MetadataManifest',
  setup() {
    const { t } = useLocaleHooks()
    const [, useManifest, { loading }] = getManifest()


    const data: RowData[] = [
      {
        fileName: 'manifest-wekfj',
        fileSize: 29838,
        numAddFiles: 19,
      },
      {
        fileName: 'manifest-akagerjgerg38746',
        fileSize: 827387,
        numAddFiles: 27,
      },
      {
        fileName: 'manifest-aka38reophkrpoth746',
        fileSize: 36423,
        numAddFiles: 37,
      },
      {
        fileName: 'manifest-gerjgoiejrog',
        fileSize: 387423,
        numAddFiles: 34,
      },
    ]

    const columns: DataTableColumns<RowData> = [
      {
        title: 'File Name',
        key: 'fileName'
      },
      {
        title: 'File Size',
        key: 'fileSize'
      },
      {
        title: 'Number of Add Files',
        key: 'numAddFiles'
      },
    ]

    onMounted(() => {
      useManifest({
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
