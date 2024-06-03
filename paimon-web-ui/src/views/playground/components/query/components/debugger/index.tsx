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
import { NInput, useMessage } from 'naive-ui'

import styles from './index.module.scss'
import { getClusterListByType } from '@/api/models/cluster'
import type { Cluster } from '@/api/models/cluster/types'
import type { JobSubmitDTO } from '@/api/models/job/types/job'
import { createRecord, submitJob } from '@/api/models/job'
import { useJobStore } from '@/store/job'

import type { ExecutionMode } from '@/store/job/type'
import type { RecordDTO } from '@/api/models/job/types/record'

export default defineComponent({
  name: 'EditorDebugger',
  emits: ['handleFormat', 'handleSave'],
  props: {
    tabData: {
      type: Object as PropType<any>,
      default: () => ({}),
    },
  },
  setup(props, { emit }) {
    const message = useMessage()
    const dialog = useDialog()

    const { t } = useLocaleHooks()
    const jobStore = useJobStore()

    const statementName = ref<string>('')
    const tabData = toRef(props.tabData)

    const debuggerVariables = reactive<{
      operatingConditionOptions: { label: string, key: string }[]
      conditionValue: string
      bigDataOptions: { label: string, value: string }[]
      conditionValue2: string
      clusterOptions: { label: string, value: string }[]
      conditionValue3: string
      executionModeOptions: { label: string, value: string }[]
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

    const handleSelect = () => {
    }

    const handleFormat = () => {
      emit('handleFormat')
    }

    async function handleSave() {
      const currentTab = tabData.value.panelsList.find((item: any) => item.key === tabData.value.chooseTab)

      if (!currentTab)
        return

      const currentSQL = currentTab.content
      if (!currentSQL) {
        message.warning(`Can't submit Empty content`)
        return
      }

      const _dialogInst = dialog.create({
        title: 'Create Record',
        content: () => h(
          NInput,
          {
            placeholder: 'Input you statement name',
            modelValue: statementName.value,
            onInput: (e: string) => {
              statementName.value = e
            },
          },
        ),
        positiveText: t('playground.save'),
        onPositiveClick: async () => {
          if (!statementName.value || !statementName.value.trim())
            return message.error('statement name is required')

          const recordDataDTO: RecordDTO = {
            statementName: statementName.value,
            taskType: debuggerVariables.conditionValue,
            clusterId: Number(debuggerVariables.conditionValue2),
            statements: currentSQL,
            isStreaming: debuggerVariables.conditionValue3 === 'Streaming',
          }

          _dialogInst.loading = true
          try {
            const response = await createRecord(recordDataDTO)
            if (response.code === 200)
              emit('handleSave')

            else
              message.error(`${t('playground.job_submission_failed')}`)
          }
          catch (error) {
            console.error('Failed to submit job:', error)
          }
          finally {
            _dialogInst.loading = false
          }
        },
      })
    }

    function getClusterData() {
      getClusterListByType(debuggerVariables.conditionValue, 1, Number.MAX_SAFE_INTEGER).then((response) => {
        if (response && response.data) {
          const clusterList = response.data as Cluster[]
          debuggerVariables.clusterOptions = clusterList.map(cluster => ({
            label: cluster.clusterName,
            value: cluster.id.toString(),
          }))
          if (debuggerVariables.clusterOptions.length > 0)
            debuggerVariables.conditionValue2 = debuggerVariables.clusterOptions[0].value
        }
      }).catch((error) => {
        console.error('Failed to fetch clusters:', error)
      })
    }

    watch(() => debuggerVariables.conditionValue, () => {
      getClusterData()
    })

    onMounted(getClusterData)

    const { mittBus } = getCurrentInstance()!.appContext.config.globalProperties
    mittBus.on('initTabData', (data: any) => {
      tabData.value = data
    })

    const handleSubmit = async () => {
      const currentTab = tabData.value.panelsList.find((item: any) => item.key === tabData.value.chooseTab)

      if (!currentTab)
        return

      jobStore.setExecutionMode(debuggerVariables.conditionValue3 as ExecutionMode)
      jobStore.resetCurrentResult()

      const currentSQL = currentTab.content
      if (!currentSQL)
        return

      const jobDataDTO: JobSubmitDTO = {
        jobName: currentTab.tableName,
        taskType: debuggerVariables.conditionValue,
        clusterId: debuggerVariables.conditionValue2,
        statements: currentSQL,
        streaming: debuggerVariables.conditionValue3 === 'Streaming',
      }

      try {
        const response = await submitJob(jobDataDTO)
        if (response.code === 200) {
          message.success(t('playground.job_submission_successfully'))
          jobStore.setCurrentJob(response.data)
          mittBus.emit('jobResult', response.data)
        }
        else {
          message.error(`${t('playground.job_submission_failed')}`)
        }
      }
      catch (error) {
        console.error('Failed to submit job:', error)
      }
    }

    return {
      t,
      ...toRefs(debuggerVariables),
      handleSelect,
      handleFormat,
      handleSave,
      handleSubmit,
    }
  },
  render() {
    return (
      <div class={styles.container}>
        <n-space>
          <n-button
            type="primary"
            onClick={this.handleSubmit}
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
