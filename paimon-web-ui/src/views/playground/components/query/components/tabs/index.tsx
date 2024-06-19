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

import dayjs from 'dayjs'
import type * as monaco from 'monaco-editor'
import { format } from 'sql-formatter'
import { useMessage } from 'naive-ui'
import EditorDebugger from '../debugger'
import styles from './index.module.scss'
import MonacoEditor from '@/components/monaco-editor'
import EditorConsole from '@/views/playground/components/query/components/console'

export default defineComponent({
  name: 'EditorTabs',
  setup() {
    const message = useMessage()
    const { mittBus } = getCurrentInstance()!.appContext.config.globalProperties
    const editorSize = ref(0.6)
    const menuTreeRef = ref()

    const tabVariables = reactive({
      chooseTab: '',
      panelsList: [] as any,
      row: {} as any,
    })

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
      tabVariables.panelsList.find((item: any) => item.key === tabVariables.chooseTab).content = toRaw(editorVariables.editor).getValue()
      handleFormat()
      tabVariables.panelsList.find((item: any) => item.key === tabVariables.chooseTab).isSaved = true

      menuTreeRef.value && menuTreeRef.value?.onLoadRecordData()
    }

    const handleContentChange = (value: string) => {
      tabVariables.panelsList.find((item: any) => item.key === tabVariables.chooseTab).content = value
      tabVariables.panelsList.find((item: any) => item.key === tabVariables.chooseTab).isSaved = false
    }

    const handleAdd = () => {
      const timestamp = dayjs().format('YYYY-MM-DD HH:mm:ss')
      tabVariables.panelsList.push({
        tableName: timestamp,
        key: timestamp,
        isSaved: false,
        content: '',
        debuggerData: {},
      })
      tabVariables.chooseTab = timestamp
    }

    const handleClose = (key: any) => {
      const index = tabVariables.panelsList.findIndex((item: any) => item.key === key)
      tabVariables.panelsList.splice(index, 1)
      if (key === tabVariables.chooseTab) {
        if (tabVariables.panelsList[index - 1])
          tabVariables.chooseTab = tabVariables.panelsList[index - 1].key
        else
          tabVariables.chooseTab = tabVariables.panelsList[index]?.key || ''
      }
    }

    // mitt - handle tree choose
    const treeData = ref({}) as any
    const changeTreeChoose = (value: string) => {
      treeData.value.selectedKeys = [value]
      tabVariables.chooseTab = value
    }
    mittBus.on('initTreeData', (data: any) => {
      treeData.value = data
    })

    const contextMenuVariables = reactive({
      x: 0,
      y: 0,
      isShow: false,
    })

    const openContextMenu = (e: MouseEvent, item: any) => {
      e.preventDefault()
      contextMenuVariables.x = e.pageX
      contextMenuVariables.y = e.pageY
      contextMenuVariables.isShow = true
      tabVariables.row = item
      tabVariables.chooseTab = tabVariables.row.key
    }

    const handleContextMenuSelect = (keys: string) => {
      const index = tabVariables.panelsList.findIndex((item: any) => item.key === tabVariables.row.key)
      switch (keys) {
        case 'close_left':
          tabVariables.panelsList.splice(0, index)
          break
        case 'close_right':
          tabVariables.panelsList.splice(index + 1)
          break
        case 'close_others':
          tabVariables.panelsList = [tabVariables.row]
          break
        case 'close_all':
          tabVariables.panelsList = []
          break
      }
      contextMenuVariables.isShow = false
    }

    onMounted(() => {
      mittBus.emit('initTabData', tabVariables)
    })

    const handleConsoleUp = () => {
      editorSize.value = 0
      mittBus.emit('editorResized')
    }

    const handleConsoleDown = () => {
      editorSize.value = 0.6
      mittBus.emit('editorResized')
    }

    const showConsole = ref(true)
    const handleConsoleClose = () => {
      editorSize.value = 0.98
      showConsole.value = false
    }

    const handleDragEnd = () => {
      mittBus.emit('editorResized')
      mittBus.emit('resizeLog')
    }

    return {
      ...toRefs(tabVariables),
      ...toRefs(editorVariables),
      handleAdd,
      handleClose,
      changeTreeChoose,
      openContextMenu,
      ...toRefs(contextMenuVariables),
      handleContextMenuSelect,
      editorSize,
      handleDragEnd,
      tabVariables,
      editorMounted,
      editorSave,
      showConsole,
      handleContentChange,
      handleConsoleUp,
      handleConsoleDown,
      handleConsoleClose,
    }
  },
  render() {
    return (
      <div class={styles.container}>
        <n-tabs
          v-model:value={this.chooseTab}
          type="card"
          addable
          closable
          style={{ height: '100%' }}
          tab-style="min-width: 160px;"
          pane-style="padding-top: 0;"
          on-close={this.handleClose}
          on-add={this.handleAdd}
          on-update:value={this.changeTreeChoose}
          v-slots={{
            prefix: () => '',
            suffix: () => '',
          }}
        >
          {
            this.panelsList.map((item: any) => (
              <n-tab-pane
                name={item.key}
                v-slots={{
                  tab: () => (
                    <div class={styles.tabs} onContextmenu={(e: MouseEvent) => this.openContextMenu(e, item)}>
                      <div class={styles.dot}></div>
                      <div>{item.tableName}</div>
                      {!item.isSaved && <div class={styles.asterisk}>*</div>}
                    </div>
                  ),
                  default: () => [
                    <div class={styles.debugger}>
                      <EditorDebugger tabData={item} />
                    </div>,
                    <div style={{ display: 'flex', flex: 1, flexDirection: 'column', maxHeight: 'calc(100vh - 181px)', height: '100%' }}>
                      <n-split direction="vertical" max={0.6} min={0.00} resize-trigger-size={0} v-model:size={this.editorSize} on-drag-end={this.handleDragEnd}>
                        {{
                          '1': () => (
                            <div class={styles.editor}>
                              <n-card content-style="height: 100%;padding: 0;">
                                <MonacoEditor
                                  v-model={this.tabVariables.panelsList.find((item: any) => item.key === this.tabVariables.chooseTab).content}
                                  language={this.language}
                                  onEditorMounted={this.editorMounted}
                                  onEditorSave={this.editorSave}
                                  onChange={this.handleContentChange}
                                />
                              </n-card>
                            </div>
                          ),
                          '2': () => (this.showConsole && (
                            <div class={styles.console}>
                              <n-card content-style="height: 100%;padding: 0;">
                                <EditorConsole onConsoleDown={this.handleConsoleDown} onConsoleUp={this.handleConsoleUp} onConsoleClose={this.handleConsoleClose} />
                              </n-card>
                            </div>
                          )
                          ),
                          'resize-trigger': () => (
                            <div class={styles['console-splitter']} />
                          ),
                        }}
                      </n-split>
                    </div>,
                  ],
                }}
              >
              </n-tab-pane>
            ))
          }
        </n-tabs>
        {/* <ContextMenu
          x={this.x}
          y={this.y}
          visible={this.isShow}
          type={['close_left', 'close_right', 'close_others', 'close_all']}
          onUpdate:visible={() => this.isShow = false}
          onSelect={this.handleContextMenuSelect}
        /> */}
      </div>
    )
  },
})
