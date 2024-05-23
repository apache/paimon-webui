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

import styles from './index.module.scss'
import { DataTable, Copy, Renew } from "@vicons/carbon"
import { StopOutline } from "@vicons/ionicons5"
import { LineChartOutlined, ClockCircleOutlined, DownloadOutlined } from "@vicons/antd"
import { useConfigStore } from '@/store/config'

export default defineComponent({
  name: 'TableActionBar',
  setup() {
    const { t } = useLocaleHooks()

    const configStore = useConfigStore();
    const isDarkMode = computed(() => configStore.theme === 'dark');

    const activeButton = ref('table');
    const setActiveButton = (button: any) => {
      activeButton.value = button;
    };

    return {
      t,
      activeButton,
      setActiveButton,
      isDarkMode
    }
  },
  render() {
    return (
      <div class={styles.container}>
       <n-space class={styles.left} item-style="display: flex; align-items: center;">
         <n-popover trigger="hover" placement="bottom-start" show-arrow={false} style="padding: 0"
            v-slots={{
              trigger: () => (
                <n-button
                  text
                  class={this.activeButton === 'table' ? styles['active-button'] : styles['table-action-bar-button']}
                  onClick={() => this.setActiveButton('table')}
                  v-slots={{
                    icon: () => <n-icon component={DataTable}  size="20"></n-icon>
                  }}
                >
                </n-button>
              )
            }}>
           <span>{this.t('playground.switch_to_table')}</span>
         </n-popover>
         <n-popover trigger="hover" placement="bottom-start" show-arrow={false}
            v-slots={{
              trigger: () => (
                <n-button
                  text
                  class={this.activeButton === 'chart' ? styles['active-button'] : styles['table-action-bar-button']}
                  onClick={() => this.setActiveButton('chart')}
                  v-slots={{
                    icon: () => <n-icon component={LineChartOutlined}  size="20"></n-icon>
                  }}
                >
                </n-button>
              )
            }}>
           <span>{this.t('playground.switch_to_chart')}</span>
         </n-popover>
         <n-divider vertical style="height: 20px; margin-left: 0px; margin-right: 0px; border-left-width: 3px;"/>
         <n-popover trigger="hover" placement="bottom-start" show-arrow={false}
            v-slots={{
              trigger: () => (
                <n-button
                  text
                  class={styles['table-action-bar-button']}
                  v-slots={{
                    icon: () => <n-icon component={Renew}  size="20"></n-icon>
                  }}
                >
                </n-button>
              )
            }}>
           <span>{this.t('playground.refresh_data')}</span>
         </n-popover>
         <n-popover trigger="hover" placement="bottom-start" show-arrow={false}
            v-slots={{
              trigger: () => (
                <n-button
                  text
                  class={styles['table-action-bar-button']}
                  style="color:  #D94F4F"
                  v-slots={{
                    icon: () => <n-icon component={StopOutline} size="20"></n-icon>
                  }}
                >
                </n-button>
              )
            }}>
           <span>{this.t('playground.stop_job')}</span>
         </n-popover>
         <n-popover trigger="hover" placement="bottom-start" show-arrow={false}
            v-slots={{
              trigger: () => (
                <n-button
                  text
                  class={styles['table-action-bar-button']}
                  v-slots={{
                    icon: () => <n-icon component={ClockCircleOutlined}  size="18.5"></n-icon>
                  }}
                >
                </n-button>
              )
            }}>
           <span>{this.t('playground.schedule_refresh')}</span>
         </n-popover>
         <n-divider vertical style="height: 20px; margin-left: 0px; margin-right: 0px;"/>
         <span class={styles['table-action-bar-text']}>4 Columns</span>
       </n-space>
        <div class={styles.right}>
          <n-space item-style="display: flex; align-items: center;">
            <div class={styles['table-action-bar-text']}>Job:<span style="color: #33994A"> Running</span></div>
            <span class={styles['table-action-bar-text']}>Rows: 3</span>
            <span class={styles['table-action-bar-text']}>1m:06s</span>
            <n-popover trigger="hover" placement="bottom-start" show-arrow={false}
               v-slots={{
                 trigger: () => (
                   <n-button
                     text
                     class={styles['table-action-bar-button']}
                     v-slots={{
                       icon: () => <n-icon component={DownloadOutlined} size="20"></n-icon>
                     }}
                   >
                   </n-button>
                 )
               }}>
              <span>{this.t('playground.download')}</span>
            </n-popover>
            <n-popover trigger="hover" placement="bottom-start" show-arrow={false}
               v-slots={{
                 trigger: () => (
                   <n-button
                     text
                     class={styles['table-action-bar-button']}
                     v-slots={{
                       icon: () => <n-icon component={Copy}  size="20"></n-icon>
                     }}
                   >
                   </n-button>
                 )
               }}>
              <span>{this.t('playground.copy')}</span>
            </n-popover>
          </n-space>
        </div>
      </div>
    )
  }
})