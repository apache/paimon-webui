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

import { ChevronDown, Play, ReaderOutline, Save } from '@vicons/ionicons5'
import { getClusterListByType } from '@/api/models/cluster'
import styles from './index.module.scss'
import type { Cluster } from "@/api/models/cluster/types";

export default defineComponent({
  name: 'EditorDebugger',
  emits: ['handleFormat', 'handleSave'],
  setup(props, { emit }) {
    const { t } = useLocaleHooks()

    const debuggerVariables = reactive<{
      operatingConditionOptions: { label: string; key: string }[]
      conditionValue: string
      bigDataOptions: { label: string; value: string }[]
      conditionValue2: string
      clusterOptions: { label: string; value: string }[]
      conditionValue3: string
      executionModeOptions: { label: string; value: string }[]
    }>({
      operatingConditionOptions: [
        { label: 'Limit 100 items', key: '100' },
        { label: 'Limit 1000 items', key: '1000' },
      ],
      conditionValue: 'Flink',
      bigDataOptions: [
        { label: 'Flink', value: 'Flink' },
        { label: 'Spark', value: 'Spark' },
      ],
      conditionValue2: '',
      clusterOptions: [],
      conditionValue3: 'Streaming',
      executionModeOptions: [
        { label: 'Streaming', value: 'Streaming' },
        { label: 'Batch', value: 'Batch' },
      ],
    })

    const handleSelect = (key: string) => {
      console.log(key)
    }

    const handleFormat = () => {
      emit('handleFormat')
    }

    const handleSave = () => {
      emit('handleSave')
    }

    function getClusterData() {
      getClusterListByType(debuggerVariables.conditionValue, 1, Number.MAX_SAFE_INTEGER).then(response => {
        if (response && response.data) {
          const clusterList = response.data as Cluster[];
          debuggerVariables.clusterOptions = clusterList.map(cluster => ({
            label: cluster.clusterName,
            value: cluster.id.toString()
          }))
          if (debuggerVariables.clusterOptions.length > 0) {
            debuggerVariables.conditionValue2 = debuggerVariables.clusterOptions[0].value;
          }
        }
      }).catch(error => {
        console.error('Failed to fetch clusters:', error);
      })
    }

    watch(() => debuggerVariables.conditionValue, (newValue) => {
      getClusterData();
    })

    onMounted(() => {getClusterData()})

    return {
      t,
      ...toRefs(debuggerVariables),
      handleSelect,
      handleFormat,
      handleSave,
    }
  },
  render() {
    return (
      <div class={styles.container}>
        <n-space>
          <n-button
            type="primary"
            v-slots={{
              icon: () => <n-icon component={Play} />,
              default: () => {
                return (
                  <div class={styles.run}>
                    {this.t('playground.run')}
                    <n-divider vertical />
                    <n-dropdown trigger="hover" show-arrow options={this.operatingConditionOptions} on-select={this.handleSelect}>
                      <n-icon component={ChevronDown} />
                    </n-dropdown>
                  </div>
                )
              },
            }}
          >
          </n-button>
          <n-select style="width:160px;" v-model:value={this.conditionValue} options={this.bigDataOptions} />
          <n-select style="width:160px;" v-model:value={this.conditionValue2} options={this.clusterOptions} />
          <n-select style="width:160px;" v-model:value={this.conditionValue3} options={this.executionModeOptions} />
        </n-space>
        <div class={styles.operations}>
          <n-space>
            <n-popover
              trigger="hover"
              placement="bottom"
              v-slots={{
                trigger: () => (
                  <n-button
                    onClick={this.handleFormat}
                    v-slots={{
                      icon: () => <n-icon component={ReaderOutline}></n-icon>,
                    }}
                  >
                  </n-button>
                ),
              }}
            >
              <span>{this.t('playground.format')}</span>
            </n-popover>
            <n-popover
              trigger="hover"
              placement="bottom"
              v-slots={{
                trigger: () => (
                  <n-button
                    onClick={this.handleSave}
                    v-slots={{
                      icon: () => <n-icon component={Save}></n-icon>,
                    }}
                  >
                  </n-button>
                ),
              }}
            >
              <span>{this.t('playground.save')}</span>
            </n-popover>
          </n-space>
        </div>
      </div>
    )
  },
})
