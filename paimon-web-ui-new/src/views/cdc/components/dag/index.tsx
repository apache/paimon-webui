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

import { Leaf, Save } from "@vicons/ionicons5"
import styles from './index.module.scss';
import DagCanvas from "./dag-canvas";

export default defineComponent({
  name: 'DagPage',
  setup() {
    const { t } = useLocaleHooks()
    
    return {
      t,
    }
  },
  render() {
    return (
      <n-card>
        <n-space vertical size={24}>
          <n-card>
            <div class={styles.title}>
              <n-space align="center">
                <n-icon component={Leaf} color="#2F7BEA" size="18" />
                <span>{this.t('cdc.synchronization_job_definition')}</span>
              </n-space>
              <div class={styles.operation}>
                <n-space>
                  <n-popover trigger="hover" placement="bottom"
                    v-slots={{
                      trigger: () => (
                        <n-button
                          v-slots={{
                            icon: () => <n-icon component={Save}></n-icon>
                          }}
                        >
                        </n-button>
                      )
                    }}>
                    <span>{this.t('cdc.save')}</span>
                  </n-popover>
                </n-space>
              </div>
            </div>
          </n-card>
          <DagCanvas></DagCanvas>
        </n-space>
      </n-card>
    )
  }
})
