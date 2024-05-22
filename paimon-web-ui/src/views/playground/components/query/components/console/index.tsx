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

import { KeyboardDoubleArrowUpSharp, KeyboardDoubleArrowDownSharp, CloseSharp } from '@vicons/material'
import TableActionBar from './components/controls'
import TableResult from './components/table'
import styles from './index.module.scss'

export default defineComponent({
  name: 'EditorConsole',
  emits: ['ConsoleUp', 'ConsoleDown', 'ConsoleClose'],
  setup(props, { emit }) {
    const { t } = useLocaleHooks()

    const handleUp = () => {
      emit('ConsoleUp', 'up')
    }

    const handleDown = () => {
      emit('ConsoleDown', 'down')
    }

    const handleClose = () => {
      emit('ConsoleClose', 'close')
    }

    return {
      t,
      handleUp,
      handleDown,
      handleClose
    }
  },
  render() {
    return (
      <div class={styles['editor-console']}>
        <n-tabs
          type="line"
          size="large"
          default-value="logs"
          tabs-padding={20}
          pane-style="padding: 0px;box-sizing: border-box;"
        >
          <n-tab-pane name="logs" tab={this.t('playground.logs')}>
            {this.t('playground.logs')}
          </n-tab-pane>
          <n-tab-pane name="result" tab={this.t('playground.result')}>
            <TableActionBar/>
            <TableResult/>
          </n-tab-pane>
        </n-tabs>
        <div class={styles.operations}>
          <n-space>
            <n-popover trigger="hover" placement="bottom"
              v-slots={{
                trigger: () => (
                  <n-button
                    text
                    onClick={this.handleUp}
                    v-slots={{
                      icon: () => <n-icon component={KeyboardDoubleArrowUpSharp} size="20"></n-icon>
                    }}
                  >
                  </n-button>
                )
              }}>
              <span>{this.t('playground.unfold')}</span>
            </n-popover>
            <n-popover trigger="hover" placement="bottom"
              v-slots={{
                trigger: () => (
                  <n-button
                    text
                    onClick={this.handleDown}
                    v-slots={{
                      icon: () => <n-icon component={KeyboardDoubleArrowDownSharp} size="20"></n-icon>
                    }}
                  >
                  </n-button>
                )
              }}>
              <span>{this.t('playground.collapse')}</span>
            </n-popover>
            <n-popover trigger="hover" placement="bottom"
              v-slots={{
                trigger: () => (
                  <n-button
                    text
                    onClick={this.handleClose}
                    v-slots={{
                      icon: () => <n-icon component={CloseSharp} size="19"></n-icon>
                    }}
                  >
                  </n-button>
                )
              }}>
              <span>{this.t('playground.close')}</span>
            </n-popover>
          </n-space>
        </div>
      </div>   
    )
  }
})
