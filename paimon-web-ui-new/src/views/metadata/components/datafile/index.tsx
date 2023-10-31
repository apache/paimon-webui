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

type RowData = {
  partition: string
  bucket: number
  filePath: string
  fileFormat: string
}


export default defineComponent({
  name: 'MetadataDataFile',
  setup() {
    const { t } = useLocaleHooks()

    const data: RowData[] = [
      {
        partition: '[1]',
        bucket: 0,
        filePath: 'opt/paimon/warehouse/ods.db/t_role',
        fileFormat: 'ORC'
      },
      {
        partition: '[2]',
        bucket: 0,
        filePath: 'opt/paimon/warehouse/ods.db/t_role',
        fileFormat: 'ORC'
      },
      {
        partition: '[3]',
        bucket: 1,
        filePath: 'opt/paimon/warehouse/ods.db/t_role',
        fileFormat: 'ORC'
      },
      {
        partition: '[4]',
        bucket: 1,
        filePath: 'opt/paimon/warehouse/ods.db/t_role',
        fileFormat: 'ORC'
      },
    ]

    const columns: DataTableColumns<RowData> = [
      {
        title: 'Partition',
        key: 'partition'
      },
      {
        title: 'Bucket',
        key: 'bucket'
      },
      {
        title: 'File Path',
        key: 'filePath'
      },
      {
        title: 'File Format',
        key: 'fileFormat'
      }
    ]

    return {
      columns,
      data,
      t,
    }
  },
  render() {
    return (
      <n-card>
        <n-data-table
          columns={this.columns}
          data={this.data}
        />
      </n-card>
    );
  },
});
