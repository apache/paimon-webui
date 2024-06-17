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

export default defineComponent({
  name: 'EditorConsole',
  emits: ['ConsoleUp', 'ConsoleDown', 'ConsoleClose'],
  setup(props, { emit }) {
    const { t } = useLocaleHooks()
    const { mittBus } = getCurrentInstance()!.appContext.config.globalProperties
    const editorConsoleRef = ref<HTMLElement | null>(null)
    const adjustedHeight = ref(0)
    const displayResult = ref(false)

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
    }
  },
  render() {
    return (
      <div class={styles['editor-console']} ref="editorConsoleRef">
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
                <TableActionBar />,
                <TableResult maxHeight={this.adjustedHeight} />,
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
