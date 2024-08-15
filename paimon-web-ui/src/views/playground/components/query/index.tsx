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

import { onMounted } from 'vue'
import styles from './index.module.scss'
import MenuTree from './components/menu-tree'
import EditorTabs from './components/tabs'
import { useJobStore } from '@/store/job'
import { getLogs, refreshJobStatus } from '@/api/models/job'
import { createSession } from '@/api/models/session'
import type { ExecutionMode, JobDetails } from '@/store/job/type'

export default defineComponent({
  name: 'QueryPage',
  setup() {
    const jobStore = useJobStore()
    const { mittBus } = getCurrentInstance()!.appContext.config.globalProperties

    const tabData = ref({
      panelsList: [],
      chooseTab: null,
    }) as any
    const currentKey = computed(() => {
      const currentTab = tabData.value.panelsList.find((item: any) => item.key === tabData.value.chooseTab)
      return currentTab ? currentTab.key : null
    })
    const currentJob = computed(() => jobStore.getCurrentJob(currentKey.value))

    // mitt - handle tab choose
    mittBus.on('initTabData', (data: any) => {
      tabData.value = data
    })

    let getJobLogsIntervalId: number | undefined
    const getJobLog = () => {
      getJobLogsIntervalId = setInterval(async () => {
        const response = await getLogs()
        jobStore.setJobLog(response.data)
      }, 1000)
    }

    let createSessionIntervalId: number | undefined
    watch(currentJob, (newJob) => {
      if (newJob && createSessionIntervalId === undefined) {
        createSessionIntervalId = setInterval(() => {
          createSession()
        }, 300000)
      }

      if (!newJob && createSessionIntervalId !== undefined) {
        clearInterval(createSessionIntervalId)
        createSessionIntervalId = undefined
      }
    })

    let refreshJobStatusIntervalId: number | undefined
    watch(currentJob, (newJob) => {
      if (newJob && refreshJobStatusIntervalId === undefined) {
        refreshJobStatusIntervalId = setInterval(() => {
          refreshJobStatus()
        }, 1000)
      }

      if (!newJob && refreshJobStatusIntervalId !== undefined) {
        clearInterval(refreshJobStatusIntervalId)
        refreshJobStatusIntervalId = undefined
      }
    })

    // get job status
    const wsUrl = import.meta.env.VITE_WS_URL
    function setupGetJobStatusWebSocket() {
      const { connect, subscribe } = useWebSocket(wsUrl, {
        onMessage: (message) => {
          const data = JSON.parse(message.body)
          if (data && data.jobId && data.status && data.fileName)
            jobStore.updateJobStatus(data.fileName, data.status)
        },
        onOpen: () => {
          subscribe('/topic/jobStatus')
        },
        onError: (event) => {
          console.error('WebSocket encountered an error:', event)
        },
        onClose: () => {
        },
      })

      connect()
    }

    function setupSubmitJobWebSocket() {
      const { connect, subscribe } = useWebSocket(wsUrl, {
        onMessage: (message) => {
          const data = JSON.parse(message.body)
          if (!data.jobId && !data.shouldFetchResult) {
            const jobDetail: JobDetails = {
              executionMode: data.executeMode as ExecutionMode,
              job: data,
              jobResultData: data.resultData.length > 0 ? data.resultData: null,
              jobStatus: data.status,
              executionTime: 0,
              startTime: Date.now(),
              displayResult: true,
              loading: false,
            }
            jobStore.addJob(data.fileName, jobDetail)
          } else if (data && data.jobId && data.fileName) {
            const jobDetail: JobDetails = {
              executionMode: data.executeMode as ExecutionMode,
              job: data,
              jobResultData: null,
              jobStatus: '',
              executionTime: 0,
              startTime: Date.now(),
              displayResult: true,
              loading: false,
            }
            jobStore.addJob(data.fileName, jobDetail)
          }
        },
        onOpen: () => {
          subscribe('/topic/job')
        },
        onError: (event) => {
          console.error('WebSocket encountered an error:', event)
        },
        onClose: () => {
        },
      })

      connect()
    }

    onMounted(() => {
      setupGetJobStatusWebSocket()
      setupSubmitJobWebSocket()
      getJobLog()
    })

    onUnmounted(() => {
      jobStore.resetJob(currentKey.value)
      if (refreshJobStatusIntervalId !== undefined) {
        clearInterval(refreshJobStatusIntervalId)
        refreshJobStatusIntervalId = undefined
      }
      if (createSessionIntervalId !== undefined) {
        clearInterval(createSessionIntervalId)
        createSessionIntervalId = undefined
      }
      if (getJobLogsIntervalId !== undefined) {
        clearInterval(getJobLogsIntervalId)
        getJobLogsIntervalId = undefined
      }
    })

    return {
      tabData,
    }
  },
  render() {
    return (
      <div class={styles.query}>
        <n-split direction="horizontal" max={0.35} min={0.16} resize-trigger-size={0} default-size={0.20}>
          {{
            '1': () => (
              <div class={styles['menu-tree']}>
                <MenuTree ref="menuTreeRef" />
              </div>
            ),
            '2': () => (
              <div class={styles['editor-area']}>
                <n-card class={styles.card} content-style="padding: 5px 18px;display: flex;flex-direction: column; height:100%;">
                  <div class={styles.tabs}>
                    <EditorTabs />
                  </div>
                </n-card>
              </div>
            ),
            'resize-trigger': () => (
              <div class={styles.split} />
            ),
          }}
        </n-split>
      </div>
    )
  },
})
