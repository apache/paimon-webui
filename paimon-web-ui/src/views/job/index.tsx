/* Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
'License'); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
'AS IS' BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License. */

import type { DataTableColumns } from 'naive-ui'
import { Reload, SearchOutline, SettingsOutline } from '@vicons/ionicons5'

import StatisticCard from './components/statistic-card'
import StatusTag from './components/status-tag'
import { type JobStatusType } from './constant'

import styles from './index.module.scss'

type RowData = {
  id: string
  name: string
  status: JobStatusType
  startTime: string
  endTime?: string
}

export default defineComponent({
  name: 'JobPage',
  setup() {
    const data: RowData[] = [
      {
        id: '1',
        name: 'ods_role_task',
        status: 'RUNNING',
        startTime: '2023-10-30 20:00:00',
      },
      {
        id: '2',
        name: 'ods_role_task',
        status: 'FINISH',
        startTime: '2023-10-30 20:00:00',
        endTime: '2023-11-01 20:00:00',
      },
      {
        id: '3',
        name: 'ods_role_task',
        status: 'CANCEL',
        startTime: '2023-10-30 20:00:00',
      },
      {
        id: '4',
        name: 'ods_role_task',
        status: 'FAIL',
        startTime: '2023-10-30 20:00:00',
      }
    ]

    const columns: DataTableColumns<RowData> = [
      {
        title: 'ID',
        key: 'id'
      },
      {
        title: '作业名称',
        key: 'name'
      },
      {
        title: '作业状态',
        key: 'status',
        render(rowData) {
          return < StatusTag type={rowData.status} />
        },
      },
      {
        title: '开始时间',
        key: 'startTime'
      },
      {
        title: '结束时间',
        key: 'endTime',
        render(rowData) {
          return rowData.endTime || '-'
        }
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
                <n-button type='primary'>
                  <n-icon> <SearchOutline /></n-icon>
                </n-button>
              </n-input-group>
              <n-space>
                <n-button quaternary circle type='primary'>
                  <n-icon> <Reload /></n-icon>
                </n-button>
                <n-button quaternary circle type='primary'>
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
