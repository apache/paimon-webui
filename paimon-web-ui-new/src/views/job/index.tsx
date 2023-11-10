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
import { Reload, SearchOutline, SettingsOutline } from '@vicons/ionicons5'

import StatisticCard from './components/statistic-card'

import styles from './index.module.scss'

type RowData = {
  partition: string
  bucket: number
  filePath: string
  fileFormat: string
}

export default defineComponent({
  name: 'JobPage',
  setup() {

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
        fileFormat: 'PARQUET'
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
        fileFormat: 'PARQUET'
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
      data,
      columns
    }
  },
  render() {
    return (
      <div class={styles.container}>
        <n-space item-style={{ flex: 1 }} justify='space-between'>
          <StatisticCard type='TOTAL' value={10} />
          <StatisticCard type='RUNNING' value={10} />
          <StatisticCard type='FINISH' value={10} />
          <StatisticCard type='CANCEL' value={10} />
          <StatisticCard type='FAIL' value={10} />
        </n-space>
        <n-card>
          <n-space vertical size={14} justify='space-between'>
            <n-space justify='space-between'>
              <n-input-group>
                <n-input clearable style={{ width: '300px' }} />
                <n-button type="primary">
                  <n-icon> <SearchOutline /></n-icon>
                </n-button>
              </n-input-group>
              <n-space>
                <n-button quaternary circle ghost type="primary">
                  <n-icon> <Reload /></n-icon>
                </n-button>
                <n-button quaternary circle ghost type="primary">
                  <n-icon> <SettingsOutline /></n-icon>
                </n-button>
              </n-space>
            </n-space>
            <n-data-table
              columns={this.columns}
              data={this.data}
            />
          </n-space>
        </n-card>
      </div>
    )
  }
})
