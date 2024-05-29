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

import { Copy, DataTable, Renew } from '@vicons/carbon'
import { StopOutline, Stop } from '@vicons/ionicons5'
import { ClockCircleOutlined, DownloadOutlined, LineChartOutlined } from '@vicons/antd'
import styles from './index.module.scss'
import { useConfigStore } from '@/store/config'
import type {Job, JobResultData} from "@/api/models/job/types/job";
import {fetchResult, getJobStatus} from "@/api/models/job";

export default defineComponent({
  name: 'TableActionBar',
  setup: function () {
    const {t} = useLocaleHooks()

    const currentJob = ref<Job | null>(null)
    const tableData = ref<JobResultData | null>(null)
    const jobStatus = ref<string>('')

    const configStore = useConfigStore()
    const isDarkMode = computed(() => configStore.theme === 'dark')

    const activeButton = ref('table')
    const setActiveButton = (button: any) => {
      activeButton.value = button
    }

    const {mittBus} = getCurrentInstance()!.appContext.config.globalProperties
    mittBus.on('jobResult', (jobData: any) => {
      currentJob.value = jobData;
    })

    const refreshData = async () => {
      if (currentJob.value) {
        if (currentJob.value.shouldFetchResult) {
          try {
            const resultFetchDTO = {
              submitId: currentJob.value.submitId,
              clusterId: currentJob.value.clusterId,
              sessionId: currentJob.value.sessionId,
              taskType: currentJob.value.type,
              token: currentJob.value.token
            }

            const response: any = await fetchResult(resultFetchDTO);
            console.log(response.data)
            tableData.value = response.data;
            mittBus.emit('refreshedResult', response.data)
          } catch (error) {
            tableData.value = null;
            console.error('Error fetching result:', error)
          }
        } else {
          console.log('No fetching needed or job data is not available.')
        }
      } else {
        console.log('No fetching needed or job data is not available.')
      }
    }

    onMounted(() => {
      setInterval(async () => {
        if (currentJob.value && currentJob.value.jobId) {
          const response: any = await getJobStatus(currentJob.value.jobId);
          if (response.data) {
            jobStatus.value = response.data.status
          }
        }
      }, 1000);
    });

    const currentStopIcon = computed(() => jobStatus.value === 'RUNNING' ? StopOutline : Stop);

    const isButtonDisabled = computed(() => {
      return jobStatus.value !== 'RUNNING'
    })

    return {
      t,
      activeButton,
      setActiveButton,
      isDarkMode,
      refreshData,
      tableData,
      jobStatus,
      currentStopIcon,
      isButtonDisabled
    }
  },
  render() {
    return (
      <div class={styles.container}>
        <n-space class={styles.left} item-style="display: flex; align-items: center;">
          <n-popover
            trigger="hover"
            placement="bottom-start"
            show-arrow={false}
            style="padding: 0"
            v-slots={{
              trigger: () => (
                <n-button
                  text
                  class={this.activeButton === 'table' ? styles['active-button'] : styles['table-action-bar-button']}
                  onClick={() => this.setActiveButton('table')}
                  v-slots={{
                    icon: () => <n-icon component={DataTable} size="20"></n-icon>,
                  }}
                >
                </n-button>
              ),
            }}
          >
            <span>{this.t('playground.switch_to_table')}</span>
          </n-popover>
          <n-popover
            trigger="hover"
            placement="bottom-start"
            show-arrow={false}
            v-slots={{
              trigger: () => (
                <n-button
                  text
                  class={this.activeButton === 'chart' ? styles['active-button'] : styles['table-action-bar-button']}
                  onClick={() => this.setActiveButton('chart')}
                  v-slots={{
                    icon: () => <n-icon component={LineChartOutlined} size="20"></n-icon>,
                  }}
                >
                </n-button>
              ),
            }}
          >
            <span>{this.t('playground.switch_to_chart')}</span>
          </n-popover>
          <n-divider vertical style="height: 20px; margin-left: 0px; margin-right: 0px; border-left-width: 3px;" />
          <n-popover
            trigger="hover"
            placement="bottom-start"
            show-arrow={false}
            v-slots={{
              trigger: () => (
                <n-button
                  text
                  onClick={this.refreshData}
                  class={styles['table-action-bar-button']}
                  v-slots={{
                    icon: () => <n-icon component={Renew} size="20"></n-icon>,
                  }}
                >
                </n-button>
              ),
            }}
          >
            <span>{this.t('playground.refresh_data')}</span>
          </n-popover>
          <n-popover
            trigger="hover"
            placement="bottom-start"
            show-arrow={false}
            v-slots={{
              trigger: () => (
                <n-button
                  text
                  disabled={this.isButtonDisabled}
                  class={this.jobStatus === 'RUNNING' ? styles['stop-button-running'] : styles['table-action-bar-button']}
                  v-slots={{
                    icon: () => <n-icon component={this.currentStopIcon} size="20"></n-icon>,
                  }}
                >
                </n-button>
              ),
            }}
          >
            <span>{this.t('playground.stop_job')}</span>
          </n-popover>
          <n-popover
            trigger="hover"
            placement="bottom-start"
            show-arrow={false}
            v-slots={{
              trigger: () => (
                <n-button
                  text
                  class={styles['table-action-bar-button']}
                  v-slots={{
                    icon: () => <n-icon component={ClockCircleOutlined} size="18.5"></n-icon>,
                  }}
                >
                </n-button>
              ),
            }}
          >
            <span>{this.t('playground.schedule_refresh')}</span>
          </n-popover>
          <n-divider vertical style="height: 20px; margin-left: 0px; margin-right: 0px;" />
          <span class={styles['table-action-bar-text']}>{this.tableData?.columns} Columns</span>
        </n-space>
        <div class={styles.right}>
          <n-space item-style="display: flex; align-items: center;">
            <div class={styles['table-action-bar-text']}>
              Job:
              <span style="color: #33994A"> {this.jobStatus}</span>
            </div>
            <span class={styles['table-action-bar-text']}>Rows: {this.tableData?.rows}</span>
            <span class={styles['table-action-bar-text']}>1m:06s</span>
            <n-popover
              trigger="hover"
              placement="bottom-start"
              show-arrow={false}
              v-slots={{
                trigger: () => (
                  <n-button
                    text
                    class={styles['table-action-bar-button']}
                    v-slots={{
                      icon: () => <n-icon component={DownloadOutlined} size="20"></n-icon>,
                    }}
                  >
                  </n-button>
                ),
              }}
            >
              <span>{this.t('playground.download')}</span>
            </n-popover>
            <n-popover
              trigger="hover"
              placement="bottom-start"
              show-arrow={false}
              v-slots={{
                trigger: () => (
                  <n-button
                    text
                    class={styles['table-action-bar-button']}
                    v-slots={{
                      icon: () => <n-icon component={Copy} size="20"></n-icon>,
                    }}
                  >
                  </n-button>
                ),
              }}
            >
              <span>{this.t('playground.copy')}</span>
            </n-popover>
          </n-space>
        </div>
      </div>
    )
  },
})
