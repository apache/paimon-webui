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

import { ChevronDown, ChevronUp, TrashOutline } from '@vicons/ionicons5'
import styles from './index.module.scss'

export default defineComponent({
  name: 'EditorConsole',
  emits: ['ConsoleUp', 'ConsoleDown'],
  setup(props, { emit }) {
    const { t } = useLocaleHooks()

    const handleUp = () => {
      emit('ConsoleUp', 'up')
    }

    const handleDown = () => {
      emit('ConsoleDown', 'down')
    }

    return {
      t,
      handleUp,
      handleDown
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
          pane-style="padding: 20px;"
        >
          <n-tab-pane name="logs" tab={this.t('playground.logs')}>
            {this.t('playground.logs')}
          </n-tab-pane>
          <n-tab-pane name="result" tab={this.t('playground.result')}>
            {this.t('playground.result')}
          </n-tab-pane>
        </n-tabs>
        <div class={styles.operations}>
          <n-space>
            <n-popover trigger="hover" placement="bottom"
              v-slots={{
                trigger: () => (
                  <n-button
                    text
                    v-slots={{
                      icon: () => <n-icon component={TrashOutline}></n-icon>
                    }}
                  >
                  </n-button>
                )
              }}>
              <span>{this.t('playground.clear')}</span>
            </n-popover>
            <n-popover trigger="hover" placement="bottom"
              v-slots={{
                trigger: () => (
                  <n-button
                    text
                    onClick={this.handleUp}
                    v-slots={{
                      icon: () => <n-icon component={ChevronUp}></n-icon>
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
                      icon: () => <n-icon component={ChevronDown}></n-icon>
                    }}
                  >
                  </n-button>
                )
              }}>
              <span>{this.t('playground.pack_up')}</span>
            </n-popover>
          </n-space>
        </div>
      </div>   
    )
  }
})
