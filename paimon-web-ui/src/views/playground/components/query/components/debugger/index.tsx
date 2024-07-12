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

import { Pause, Play, Reload, Save } from '@vicons/ionicons5'
import { FormatAlignLeftOutlined } from '@vicons/material'
import { NInput, useMessage } from 'naive-ui'

import styles from './index.module.scss'
import { getClusterListByDeploymentMode } from '@/api/models/cluster'
import type { Cluster } from '@/api/models/cluster/types'
import type { JobSubmitDTO } from '@/api/models/job/types/job'
import { createRecord, stopJob, submitJob } from '@/api/models/job'
import { useJobStore } from '@/store/job'

import type { RecordDTO } from '@/api/models/job/types/record'

export default defineComponent({
  name: 'EditorDebugger',
  emits: ['handleFormat', 'reloadLayout', 'handleSave'],
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
    const isSubmitting = ref(false)
    const jobStore = useJobStore()
    const { mittBus } = getCurrentInstance()!.appContext.config.globalProperties
    const statementName = ref<string>('')
    const tabData = toRef(props.tabData)
    const currentKey = computed(() => {
      const currentTab = tabData.value.panelsList.find((item: any) => item.key === tabData.value.chooseTab)
      return currentTab ? currentTab.key : null
    })
    const jobStatus = ref('')
    const currentJob = computed(() => jobStore.getCurrentJob(currentKey.value))
    watchEffect(() => {
      const key = currentKey.value
      if (key !== null)
        jobStatus.value = jobStore.getJobStatus(key)
    })

    const debuggerVariables = reactive<{
      operatingConditionOptions: { label: string, value: number }[]
      conditionValue: string
      bigDataOptions: { label: string, value: string, disabled?: boolean }[]
      conditionValue2: string
      clusterOptions: { label: string, value: string }[]
      conditionValue3: string
      maxRows: number
      executionModeOptions: { label: string, value: string }[]
    }>({
      operatingConditionOptions: [
        { label: '500', value: 500 },
        { label: '1000', value: 1000 },
        { label: '2000', value: 2000 },
        { label: '5000', value: 5000 },
        { label: 'ALL', value: 2147483647 },
      ],
      conditionValue: 'Flink',
      bigDataOptions: [
        { label: 'Flink', value: 'Flink' },
      ],
      conditionValue2: '',
      clusterOptions: [],
      conditionValue3: 'Streaming',
      executionModeOptions: [
        { label: 'Streaming', value: 'Streaming' },
        { label: 'Batch', value: 'Batch' },
      ],
      maxRows: 500,
    })

    const handleSelect = () => {
    }

    const handleFormat = () => {
      emit('handleFormat')
    }

    const handleReload = () => {
      mittBus.emit('reloadLayout')
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
      const deploymentMode = debuggerVariables.conditionValue === 'Flink' ? 'flink-sql-gateway' : debuggerVariables.conditionValue
      getClusterListByDeploymentMode(deploymentMode, 1, Number.MAX_SAFE_INTEGER).then((response) => {
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

    mittBus.on('initTabData', (data: any) => {
      tabData.value = data
    })

    const handleStopJob = async () => {
      if (currentJob.value) {
        const job = toRaw(currentJob.value)
        const { clusterId, jobId, type: taskType } = job
        const stopJobDTO = {
          clusterId,
          jobId,
          taskType,
          withSavepoint: false,
        }
        try {
          const response = await stopJob(stopJobDTO)
          if (response.code === 200)
            message.success(t('playground.job_stopping_successfully'))

          else
            message.warning(t('playground.job_stopping_failed'))
        }
        catch (error) {
          message.warning(t('playground.job_stopping_failed'))
        }
      }
    }

    const handleSubmit = async () => {
      const currentTab = tabData.value.panelsList.find((item: any) => item.key === tabData.value.chooseTab)

      if (!currentTab)
        return

      if (jobStatus.value === 'RUNNING') {
        handleStopJob()
      }
      else {
        isSubmitting.value = true
        if (jobStore.getJobDetails(currentKey.value))
          jobStore.resetJob(currentKey.value)

        const currentSQL = currentTab.content
        if (!currentSQL) {
          isSubmitting.value = false
          return
        }

        const jobDataDTO: JobSubmitDTO = {
          jobName: currentTab.tableName,
          fileName: currentTab.key,
          taskType: debuggerVariables.conditionValue,
          clusterId: debuggerVariables.conditionValue2,
          statements: currentSQL,
          streaming: debuggerVariables.conditionValue3 === 'Streaming',
          maxRows: debuggerVariables.maxRows,
        }

        try {
          const response = await submitJob(jobDataDTO)
          if (response.code === 200) {
            message.success(t('playground.job_submission_successfully'))
            mittBus.emit('jobResult', response.data)
          }
          else {
            isSubmitting.value = false
            message.error(`${t('playground.job_submission_failed')}`)
          }
        }
        catch (error) {
          isSubmitting.value = false
          console.error('Failed to submit job:', error)
        }
      }
    }

    watch(jobStatus, (newStatus) => {
      if (newStatus === 'RUNNING' || newStatus === 'FINISHED' || newStatus === 'FAILED')
        isSubmitting.value = false
    })

    return {
      t,
      ...toRefs(debuggerVariables),
      handleSelect,
      handleFormat,
      handleSave,
      handleSubmit,
      jobStatus,
      handleReload,
      isSubmitting,
    }
  },
  render() {
    return (
      <n-space justify="space-between" class={styles.container} align="center">
        <n-space align="center">
          <n-button
            type="primary"
            loading={this.isSubmitting}
            onClick={this.handleSubmit}
            v-slots={{
              icon: () => <n-icon component={this.jobStatus === 'RUNNING' ? Pause : Play} />,
              default: () => {
                return (
                  <div class={styles.run}>
                    {this.jobStatus === 'RUNNING' ? this.t('playground.stop') : this.t('playground.run')}
                  </div>
                )
              },
            }}
          >
          </n-button>
          <span>{this.t('playground.execution_mode')}</span>
          <n-select style="width:110px;" v-model:value={this.conditionValue3} options={this.executionModeOptions} />
          <span>{this.t('playground.execution_engine')}</span>
          <n-select style="width:110px;" v-model:value={this.conditionValue} options={this.bigDataOptions} />
          <span>{this.t('playground.deployment_cluster')}</span>
          <n-select style="width:200px;" v-model:value={this.conditionValue2} options={this.clusterOptions} />
          <span>{this.t('playground.limit_records')}</span>
          <n-select style="width:100px;" v-model:value={this.maxRows} options={this.operatingConditionOptions} />
        </n-space>
        <n-space align="center">
          <n-popover
            trigger="hover"
            placement="bottom"
            v-slots={{
              trigger: () => (
                <n-button
                  onClick={this.handleFormat}
                  v-slots={{
                    icon: () => <n-icon component={FormatAlignLeftOutlined}></n-icon>,
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
                  onClick={this.handleReload}
                  v-slots={{
                    icon: () => <n-icon component={Reload}></n-icon>,
                  }}
                >
                </n-button>
              ),
            }}
          >
            <span>{this.t('playground.reload')}</span>
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
      </n-space>
    )
  },
})
