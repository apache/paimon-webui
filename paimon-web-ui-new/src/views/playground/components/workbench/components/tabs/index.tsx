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

export default defineComponent({
  name: 'EditorTabs',
  setup() {
    const { mittBus }  = getCurrentInstance()!.appContext.config.globalProperties

    const tabVariables = reactive({
      chooseTab: '',
      panelsList: [] as any,
    })

    const handleAdd = () => {
      tabVariables.panelsList.push({
        tableName: 'test' + (tabVariables.panelsList.length + 1),
        key: 'test' + (tabVariables.panelsList.length + 1),
        isSaved: false,
        content: ''
      })
      tabVariables.chooseTab = 'test' + tabVariables.panelsList.length
    }

    const handleClose = (key: any) => {
      const index = tabVariables.panelsList.findIndex((item: any) => item.key === key)
      tabVariables.panelsList.splice(index, 1)
      if (key === tabVariables.chooseTab) {
        if (tabVariables.panelsList[index - 1]) {
          tabVariables.chooseTab = tabVariables.panelsList[index - 1].key
        } else {
          tabVariables.chooseTab = tabVariables.panelsList[index]?.key || ''
        }
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

    onMounted(() => {
      mittBus.emit('initTabData', tabVariables)
    })

    return {
      ...toRefs(tabVariables),
      handleAdd,
      handleClose,
      changeTreeChoose
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
          tab-style="min-width: 160px;"
          on-close={this.handleClose}
          on-add={this.handleAdd}
          on-update:value={this.changeTreeChoose}
          v-slots={{
            prefix: () => '',
            suffix: () => ''
          }}
        >
          {
            this.panelsList.map((item: any) => (
              <n-tab-pane name={item.key}
                v-slots={{
                  tab: () => (
                    <div class={styles.tabs}>
                      <div class={styles.dot}></div>
                      <div>{item.tableName}</div>
                      {!item.isSaved && <div class={styles.asterisk}>*</div>}
                    </div>
                  )
                }}
              ></n-tab-pane>
            ))
          }
        </n-tabs>
      </div>
    );
  }
});
