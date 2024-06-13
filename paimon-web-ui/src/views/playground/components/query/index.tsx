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

import type * as monaco from 'monaco-editor'
import { format } from 'sql-formatter'
import { useMessage } from 'naive-ui'
import styles from './index.module.scss'
import MenuTree from './components/menu-tree'
import EditorTabs from './components/tabs'
import EditorDebugger from './components/debugger'
import EditorConsole from './components/console'
import MonacoEditor from '@/components/monaco-editor'
import { useJobStore } from '@/store/job'
import { getJobStatus } from '@/api/models/job'

export default defineComponent({
  name: 'QueryPage',
  setup() {
    const message = useMessage()
    const jobStore = useJobStore()

    const menuTreeRef = ref()

    const tabData = ref({}) as any
    const startTime = ref(0)
    const elapsedTime = ref(0)
    const currentJob = computed(() => jobStore.getCurrentJob)
    const jobStatus = computed(() => jobStore.getJobStatus)

    const formattedTime = computed(() => formatTime(elapsedTime.value))

    const editorVariables = reactive({
      editor: {} as any,
      language: 'sql',
    })

    const editorMounted = (editor: monaco.editor.IStandaloneCodeEditor) => {
      editorVariables.editor = editor
    }

    const handleFormat = () => {
      toRaw(editorVariables.editor).setValue(format(toRaw(editorVariables.editor).getValue()))
    }

    const editorSave = () => {
      message.success('Save success')
      tabData.value.panelsList.find((item: any) => item.key === tabData.value.chooseTab).content = toRaw(editorVariables.editor).getValue()
      handleFormat()
      tabData.value.panelsList.find((item: any) => item.key === tabData.value.chooseTab).isSaved = true

      menuTreeRef.value && menuTreeRef.value?.onLoadRecordData()
    }

    const handleContentChange = (value: string) => {
      tabData.value.panelsList.find((item: any) => item.key === tabData.value.chooseTab).content = value
      tabData.value.panelsList.find((item: any) => item.key === tabData.value.chooseTab).isSaved = false
    }

    const consoleHeightType = ref('down')

    const handleConsoleUp = (type: string) => {
      consoleHeightType.value = type
    }

    const handleConsoleDown = (type: string) => {
      consoleHeightType.value = type
    }

    const showConsole = ref(true)
    const handleConsoleClose = () => {
      showConsole.value = false
    }

    watch(
      () => consoleHeightType.value,
      () => {
        if (tabData.value.panelsList?.length > 0)
          editorVariables.editor?.layout()
      },
    )

    // mitt - handle tab choose
    const { mittBus } = getCurrentInstance()!.appContext.config.globalProperties
    mittBus.on('initTabData', (data: any) => {
      tabData.value = data
    })

    const getJobStatusIntervalId = ref<number | undefined>()

    onMounted(() => {
      getJobStatusIntervalId.value = setInterval(async () => {
        if (currentJob.value && currentJob.value.jobId) {
          const response = await getJobStatus(currentJob.value.jobId)
          if (response.data)
            jobStore.setJobStatus(response.data.status)
        }
      }, 1000)
    })

    let computeExecutionTimeIntervalId: number
    const startTimer = () => {
      if (computeExecutionTimeIntervalId)
        clearInterval(computeExecutionTimeIntervalId)

      elapsedTime.value = 0
      startTime.value = Date.now()
      computeExecutionTimeIntervalId = setInterval(() => {
        elapsedTime.value = Math.floor((Date.now() - startTime.value) / 1000)
      }, 3000)
    }

    const stopTimer = () => {
      if (computeExecutionTimeIntervalId)
        clearInterval(computeExecutionTimeIntervalId)
    }

    watch(jobStatus, (newStatus, oldStatus) => {
      if (newStatus === 'RUNNING' && oldStatus !== 'RUNNING') {
        startTimer()
      }
      else if (newStatus !== 'RUNNING' && oldStatus === 'RUNNING') {
        stopTimer()
        elapsedTime.value = Math.floor((Date.now() - startTime.value) / 1000)
      }
    })

    function formatTime(seconds: number): string {
      const days = Math.floor(seconds / 86400)
      const hours = Math.floor((seconds % 86400) / 3600)
      const mins = Math.floor((seconds % 3600) / 60)
      const secs = seconds % 60
      return `${days > 0 ? `${days}d:` : ''}${hours > 0 || days > 0 ? `${hours}h:` : ''}${mins}m:${secs}s`
    }

    watch(formattedTime, formattedTime => jobStore.setExecutionTime(formattedTime))

    onUnmounted(() => jobStore.resetCurrentResult())

    return {
      ...toRefs(editorVariables),
      menuTreeRef,
      editorMounted,
      editorSave,
      handleContentChange,
      handleFormat,
      tabData,
      handleConsoleUp,
      handleConsoleDown,
      handleConsoleClose,
      showConsole,
      consoleHeightType,
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
                <n-card class={styles.card} content-style="padding: 5px 18px;display: flex;flex-direction: column;">
                  <div class={styles.tabs}>
                    <EditorTabs />
                  </div>
                  <div class={styles.debugger}>
                    {
                      this.tabData.panelsList?.length > 0
                      && (<EditorDebugger tabData={this.tabData} onHandleFormat={this.handleFormat} onHandleSave={this.editorSave} />)
                    }
                  </div>
                  <n-split direction="vertical" max={0.60} min={0.00} resize-trigger-size={0} default-size={0.6}>
                    {{
                      '1': () => (
                        <div class={styles.editor}>
                          {
                            this.tabData.panelsList?.length > 0
                            && (
                              <n-card content-style="padding: 0;">
                                <MonacoEditor
                                  v-model={this.tabData.panelsList.find((item: any) => item.key === this.tabData.chooseTab).content}
                                  language={this.language}
                                  onEditorMounted={this.editorMounted}
                                  onEditorSave={this.editorSave}
                                  onChange={this.handleContentChange}
                                />
                              </n-card>
                            )
                          }
                        </div>
                      ),
                      '2': () => (this.showConsole && (
                        <div class={styles.console}>
                          {
                              this.tabData.panelsList?.length > 0
                              && (
                                <n-card content-style="padding: 0;">
                                  <EditorConsole onConsoleDown={this.handleConsoleDown} onConsoleUp={this.handleConsoleUp} onConsoleClose={this.handleConsoleClose} />
                                </n-card>
                              )
                            }
                        </div>
                      )
                      ),
                      'resize-trigger': () => (
                        <div class={styles['console-splitter']} />
                      ),
                    }}
                  </n-split>
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
