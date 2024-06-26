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
import { Stop, StopOutline } from '@vicons/ionicons5'
import { ClockCircleOutlined, DownloadOutlined, LineChartOutlined } from '@vicons/antd'
import { useMessage } from 'naive-ui'
import dayjs from 'dayjs'
import duration from 'dayjs/plugin/duration'
import styles from './index.module.scss'
import { fetchResult, stopJob } from '@/api/models/job'
import { useJobStore } from '@/store/job'

export default defineComponent({
  name: 'TableActionBar',
  props: {
    tabData: {
      type: Object as PropType<any>,
      default: () => ({}),
    },
  },
  setup(props) {
    const { t } = useLocaleHooks()
    const message = useMessage()
    const jobStore = useJobStore()
    const { mittBus } = getCurrentInstance()!.appContext.config.globalProperties
    const tabData = toRef(props.tabData)
    const currentKey = computed(() => {
      const currentTab = tabData.value.panelsList.find((item: any) => item.key === tabData.value.chooseTab)
      return currentTab ? currentTab.key : null
    })
    const currentJob = computed(() => jobStore.getCurrentJob(currentKey.value))
    const jobStatus = computed(() => jobStore.getJobStatus(currentKey.value))
    const executionTime = computed(() => formatTime(jobStore.getExecutionTime(currentKey.value)))
    const selectedInterval = ref('Disabled')
    const refreshIntervalId = ref<number | null>(null)
    const activeButton = ref('table')

    const setActiveButton = (button: any) => {
      activeButton.value = button
    }

    const handleRefreshData = async () => {
      if (currentJob.value) {
        if (currentJob.value.shouldFetchResult) {
          try {
            jobStore.updateLoading(currentKey.value, true)
            const job = toRaw(currentJob.value)
            const { submitId, clusterId, sessionId, type: taskType, token } = job
            const resultFetchDTO = {
              submitId,
              clusterId,
              sessionId,
              taskType,
              token,
            }
            const response = await fetchResult(resultFetchDTO)
            jobStore.updateJobResultData(currentKey.value, response.data)
          }
          catch (error) {
            console.error('Error fetching result:', error)
          }
          finally {
            jobStore.updateLoading(currentKey.value, false)
          }
        }
        else {
          message.warning(t('playground.no_data'))
        }
      }
      else {
        message.warning(t('playground.no_data'))
      }
    }

    const handleStopJob = async () => {
      if (currentJob.value) {
        const job = toRaw(currentJob.value)
        const { clusterId, jobId, type: taskType } = job
        const stopJobDTO = {
          clusterId,
          jobId,
          taskType,
          withSavepoint: false,
        }
        try {
          const response = await stopJob(stopJobDTO)
          if (response.code === 200) {
            message.success(t('playground.job_stopping_successfully'))
          }
          else {
            message.warning(t('playground.job_stopping_failed'))
          }
        }
        catch (error) {
          message.warning(t('playground.job_stopping_failed'))
        }
      }
    }

    const currentStopIcon = computed(() => jobStatus.value === 'RUNNING' ? StopOutline : Stop)

    const isButtonDisabled = computed(() => {
      return jobStatus.value !== 'RUNNING'
    })

    const isScheduleButtonDisabled = computed(() => {
      return jobStore.getExecutionMode(currentKey.value) === 'Batch'
    })

    const jobStatusColor = computed(() => {
      switch (jobStatus.value.toUpperCase()) {
        case 'RUNNING':
          return '#33994A'
        case 'CANCELED':
          return '#f6b658'
        case 'FINISHED':
          return '#f5c1bd'
        case 'FAILED':
          return '#f9827c'
        default:
          return '#7ce998'
      }
    })

    const formattedJobStatus = computed(() => {
      return jobStatus.value.charAt(0).toUpperCase() + jobStatus.value.slice(1).toLowerCase()
    })

    const dropdownOptions = [
      { label: 'Disabled', key: 'Disabled' },
      { label: '5s', key: '5s' },
      { label: '10s', key: '10s' },
      { label: '30s', key: '30s' },
      { label: '1m', key: '1m' },
      { label: '5m', key: '5m' },
    ]

    const clearRefreshInterval = () => {
      if (refreshIntervalId.value) {
        clearInterval(refreshIntervalId.value)
        refreshIntervalId.value = null
      }
    }

    const setRefreshInterval = (milliseconds: number) => {
      clearRefreshInterval()
      refreshIntervalId.value = setInterval(handleRefreshData, milliseconds)
    }

    watch(jobStatus, () => {
      if (jobStatus.value !== 'RUNNING') {
        if (refreshIntervalId.value)
          clearRefreshInterval()
      }
    })

    dayjs.extend(duration)
    const handleSelect = (key: any) => {
      selectedInterval.value = key
      switch (key) {
        case '5s':
          setRefreshInterval(dayjs.duration(5, 'seconds').asMilliseconds())
          break
        case '10s':
          setRefreshInterval(dayjs.duration(10, 'seconds').asMilliseconds())
          break
        case '30s':
          setRefreshInterval(dayjs.duration(30, 'seconds').asMilliseconds())
          break
        case '1m':
          setRefreshInterval(dayjs.duration(1, 'minute').asMilliseconds())
          break
        case '5m':
          setRefreshInterval(dayjs.duration(5, 'minutes').asMilliseconds())
          break
        case 'Disabled':
        default:
          clearRefreshInterval()
          break
      }
    }

    function formatTime(seconds: number): string {
      const days = Math.floor(seconds / 86400)
      const hours = Math.floor((seconds % 86400) / 3600)
      const mins = Math.floor((seconds % 3600) / 60)
      const secs = seconds % 60
      return `${days > 0 ? `${days}d:` : ''}${hours > 0 || days > 0 ? `${hours}h:` : ''}${mins}m:${secs}s`
    }

    const rowCount = computed(() => jobStore.getRows(currentKey.value))
    const columnCount = computed(() => jobStore.getColumns(currentKey.value))

    return {
      t,
      mittBus,
      activeButton,
      setActiveButton,
      handleRefreshData,
      jobStatus,
      currentStopIcon,
      isButtonDisabled,
      isScheduleButtonDisabled,
      handleStopJob,
      formattedJobStatus,
      jobStatusColor,
      dropdownOptions,
      selectedInterval,
      handleSelect,
      columnCount,
      rowCount,
      executionTime,
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
                  onClick={this.handleRefreshData}
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
                  onClick={this.handleStopJob}
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
          <n-dropdown
            trigger="click"
            size="small"
            placement="bottom-start"
            options={this.dropdownOptions}
            disabled={this.isScheduleButtonDisabled}
            v-model:value={this.selectedInterval}
            on-select={this.handleSelect}
            v-slots={{
              trigger: () => (
                <n-button
                  text
                  disabled={this.isScheduleButtonDisabled}
                  class={styles['table-action-bar-button']}
                >
                </n-button>
              ),
              default: () => (
                <n-icon
                  size="20"
                  class={styles['table-action-bar-button']}
                  component={ClockCircleOutlined}
                />
              ),
            }}
          />
          <n-divider vertical style="height: 20px; margin-left: 0px; margin-right: 0px;" />
          <span class={styles['table-action-bar-text']}>
            {this.columnCount}
            {' '}
            Columns
          </span>
        </n-space>
        <div class={styles.right}>
          <n-space item-style="display: flex; align-items: center;">
            <div class={styles['table-action-bar-text']}>
              Job:
              <span style={{ color: this.jobStatusColor }}>
                {' '}
                {this.formattedJobStatus}
              </span>
            </div>
            <span class={styles['table-action-bar-text']}>
              Rows:
              {this.rowCount}
            </span>
            <span class={styles['table-action-bar-text']}>{ this.executionTime }</span>
            <n-popover
              trigger="hover"
              placement="bottom-start"
              show-arrow={false}
              v-slots={{
                trigger: () => (
                  <n-button
                    text
                    onClick={() => this.mittBus.emit('triggerDownloadCsv')}
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
                    onClick={() => this.mittBus.emit('triggerCopyData')}
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
