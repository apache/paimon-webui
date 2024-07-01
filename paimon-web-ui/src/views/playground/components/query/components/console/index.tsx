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
import { ClearOutlined } from '@vicons/antd'
import { throttle } from 'lodash'
import TableActionBar from './components/controls'
import TableResult from './components/table'
import LogConsole from './components/log'
import styles from './index.module.scss'
import { useJobStore } from '@/store/job'
import { clearLogs } from '@/api/models/job'

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
    const editorConsoleRef = ref<HTMLElement | null>(null)
    const adjustedHeight = ref(0)
    const tabData = toRef(props.tabData)
    const currentKey = computed(() => {
      const currentTab = tabData.value.panelsList.find((item: any) => item.key === tabData.value.chooseTab)
      return currentTab ? currentTab.key : null
    })
    const jobStore = useJobStore()
    const displayResult = computed(() => jobStore.getJobDetails(currentKey.value)?.displayResult)
    const jobStatus = computed(() => jobStore.getJobStatus(currentKey.value))

    const handleUp = () => {
      emit('ConsoleUp', 'up')
    }

    const handleDown = () => {
      emit('ConsoleDown', 'down')
    }

    const handleClose = () => {
      emit('ConsoleClose', 'close')
    }

    const handleClear = async () => {
      const response = await clearLogs()
      jobStore.setJobLog(response.data)
    }

    watch(jobStatus, (newStatus, oldStatus) => {
      if (newStatus === 'RUNNING' && oldStatus !== 'RUNNING')
        jobStore.startJobTimer(currentKey.value)

      else if (newStatus !== 'RUNNING' && oldStatus === 'RUNNING')
        jobStore.stopJobTimer(currentKey.value)
    })

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
      handleClear,
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
                    onClick={this.handleClear}
                    v-slots={{
                      icon: () => <n-icon component={ClearOutlined} size="15.5"></n-icon>,
                    }}
                  >
                  </n-button>
                ),
              }}
            >
              <span>{this.t('playground.clear')}</span>
            </n-popover>
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
