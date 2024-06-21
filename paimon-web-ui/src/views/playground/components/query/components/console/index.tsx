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

import { CloseSharp, KeyboardDoubleArrowDownSharp, KeyboardDoubleArrowUpSharp } from '@vicons/material'
import { throttle } from 'lodash'
import TableActionBar from './components/controls'
import TableResult from './components/table'
import LogConsole from './components/log'
import styles from './index.module.scss'
import { useJobStore } from '@/store/job'
import { getJobStatus } from '@/api/models/job'

export default defineComponent({
  name: 'EditorConsole',
  emits: ['ConsoleUp', 'ConsoleDown', 'ConsoleClose'],
  props: {
    tabData: {
      type: Object as PropType<any>,
      default: () => ({}),
    },
  },
  setup(props, { emit }) {
    const { t } = useLocaleHooks()
    const { mittBus } = getCurrentInstance()!.appContext.config.globalProperties
    const editorConsoleRef = ref<HTMLElement | null>(null)
    const adjustedHeight = ref(0)
    const displayResult = ref(true)
    const startTime = ref(0)
    const elapsedTime = ref(0)
    const tabData = toRef(props.tabData)
    const currentKey = computed(() => {
      const currentTab = tabData.value.panelsList.find((item: any) => item.key === tabData.value.chooseTab)
      return currentTab ? currentTab.key : null
    })
    const jobStore = useJobStore()
    const currentJob = computed(() => jobStore.getCurrentJob(currentKey.value))
    const jobStatus = computed(() => jobStore.getJobStatus(currentKey.value))
    const formattedTime = computed(() => formatTime(elapsedTime.value))

    const handleUp = () => {
      emit('ConsoleUp', 'up')
    }

    const handleDown = () => {
      emit('ConsoleDown', 'down')
    }

    const handleClose = () => {
      emit('ConsoleClose', 'close')
    }

    mittBus.on('displayResult', () => displayResult.value = true)

    // get job status
    const getJobStatusIntervalId = ref<number | undefined>()
    const stopGetJobStatus = () => {
      if (getJobStatusIntervalId.value)
        clearInterval(getJobStatusIntervalId.value)
    }

    const startGetJobStatus = () => {
      stopGetJobStatus()
      getJobStatusIntervalId.value = setInterval(async () => {
        if (currentJob.value && currentJob.value.jobId) {
          const response = await getJobStatus(currentJob.value.jobId)
          if (response.data)
            jobStore.updateJobStatus(currentKey.value, response.data.status)
        }
      }, 1000)
    }

    mittBus.on(`getStatus_${currentKey.value}`, () => startGetJobStatus())
    watch(jobStatus, (jobStatus) => {
      if (jobStatus === 'FINISHED' || jobStatus === 'CANCELED' || jobStatus === 'FAILED')
        stopGetJobStatus()
    })

    // compute execution time
    let computeExecutionTimeIntervalId: number
    const startTimer = () => {
      if (computeExecutionTimeIntervalId)
        clearInterval(computeExecutionTimeIntervalId)

      elapsedTime.value = 0
      startTime.value = Date.now()
      computeExecutionTimeIntervalId = setInterval(() => {
        elapsedTime.value = Math.floor((Date.now() - startTime.value) / 1000)
      }, 3000)
    }

    const stopTimer = () => {
      if (computeExecutionTimeIntervalId)
        clearInterval(computeExecutionTimeIntervalId)
    }

    watch(jobStatus, (newStatus, oldStatus) => {
      if (newStatus === 'RUNNING' && oldStatus !== 'RUNNING') {
        startTimer()
      }
      else if (newStatus !== 'RUNNING' && oldStatus === 'RUNNING') {
        stopTimer()
        elapsedTime.value = Math.floor((Date.now() - startTime.value) / 1000)
      }
    })

    function formatTime(seconds: number): string {
      const days = Math.floor(seconds / 86400)
      const hours = Math.floor((seconds % 86400) / 3600)
      const mins = Math.floor((seconds % 3600) / 60)
      const secs = seconds % 60
      return `${days > 0 ? `${days}d:` : ''}${hours > 0 || days > 0 ? `${hours}h:` : ''}${mins}m:${secs}s`
    }
    watch(formattedTime, formattedTime => jobStore.updateExecutionTime(currentKey.value, formattedTime))

    // handle resize
    const handleResize = throttle((entries) => {
      for (const entry of entries) {
        const { height } = entry.contentRect
        adjustedHeight.value = height - 106
      }
    }, 100)

    let resizeObserver: ResizeObserver
    onMounted(() => {
      if (editorConsoleRef.value) {
        resizeObserver = new ResizeObserver(handleResize)
        resizeObserver.observe(editorConsoleRef.value)
      }
    })

    onUnmounted(() => {
      if (editorConsoleRef.value)
        resizeObserver.unobserve(editorConsoleRef.value)
    })

    return {
      t,
      handleUp,
      handleDown,
      handleClose,
      editorConsoleRef,
      adjustedHeight,
      displayResult,
      tabData,
    }
  },
  render() {
    return (
      <div class={styles.container} ref="editorConsoleRef">
        <n-tabs
          type="line"
          size="large"
          default-value="logs"
          tabs-padding={20}
          pane-style="padding: 0px;box-sizing: border-box;"
        >
          <n-tab-pane name="logs" tab={this.t('playground.logs')}>
            <LogConsole maxHeight={this.adjustedHeight} />
          </n-tab-pane>
          <n-tab-pane name="result" tab={this.t('playground.result')}>
            {
              this.displayResult
              && [
                <TableActionBar tabData={this.tabData} />,
                <TableResult maxHeight={this.adjustedHeight} tabData={this.tabData} />,
              ]
            }
          </n-tab-pane>
        </n-tabs>
        <div class={styles.operations}>
          <n-space>
            <n-popover
              trigger="hover"
              placement="bottom"
              v-slots={{
                trigger: () => (
                  <n-button
                    text
                    onClick={this.handleUp}
                    v-slots={{
                      icon: () => <n-icon component={KeyboardDoubleArrowUpSharp} size="20"></n-icon>,
                    }}
                  >
                  </n-button>
                ),
              }}
            >
              <span>{this.t('playground.unfold')}</span>
            </n-popover>
            <n-popover
              trigger="hover"
              placement="bottom"
              v-slots={{
                trigger: () => (
                  <n-button
                    text
                    onClick={this.handleDown}
                    v-slots={{
                      icon: () => <n-icon component={KeyboardDoubleArrowDownSharp} size="20"></n-icon>,
                    }}
                  >
                  </n-button>
                ),
              }}
            >
              <span>{this.t('playground.collapse')}</span>
            </n-popover>
            <n-popover
              trigger="hover"
              placement="bottom"
              v-slots={{
                trigger: () => (
                  <n-button
                    text
                    onClick={this.handleClose}
                    v-slots={{
                      icon: () => <n-icon component={CloseSharp} size="19"></n-icon>,
                    }}
                  >
                  </n-button>
                ),
              }}
            >
              <span>{this.t('playground.close')}</span>
            </n-popover>
          </n-space>
        </div>
      </div>
    )
  },
})
