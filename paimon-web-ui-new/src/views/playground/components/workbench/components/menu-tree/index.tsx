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

import { Search, CodeSlash } from '@vicons/ionicons5';
import styles from './index.module.scss'
import { NIcon, type TreeOption } from 'naive-ui';

export default defineComponent({
  name: 'MenuTree',
  setup() {
    const { t } = useLocaleHooks()

    const treeVariables = reactive({
      savedQueryList: [
        {
          key: 1,
          label: 'test1',
          prefix: () =>
            h(NIcon, {color: '#0066FF'}, {
              default: () => h(CodeSlash)
            }),
          content: 'select * from abc where abc.a="abc";select * from cba where cba.a="cba";'
        },
        {
          key: 2,
          label: 'test2',
          prefix: () =>
            h(NIcon, {color: '#0066FF'}, {
              default: () => h(CodeSlash)
            }),
          content: 'select * from efg where efg.e="efg";select * from efg where efg.e="efg";'
        }
      ],
      filterValue: '',
      selectedKeys: []
    })

    const handleTreeSelect = (value: never[], option: { children: any; }[]) => {
      if (option[0]?.children) return
      treeVariables.selectedKeys = value
    }

    const nodeProps = ({ option }: { option: TreeOption }) => {
      return {
        onClick () {
          if (option.children) return
          if (tabData.value.panelsList?.some((item: any) => item.key === option.key)) {
            tabData.value.chooseTab = option.key
            return
          }
          tabData.value.panelsList.push({
            tableName: option.label,
            key: option.key,
            isSaved: false,
            content: option.content
          })
          tabData.value.chooseTab = option.key
        },
      }
    }

    // mitt - handle tab choose
    const tabData = ref({}) as any
    const { mittBus }  = getCurrentInstance()!.appContext.config.globalProperties
    mittBus.on('initTabData', (data: any) => {
      tabData.value = data
    })

    onMounted(() => {
      mittBus.emit('initTreeData', treeVariables)
    })

    return {
      t,
      ...toRefs(treeVariables),
      handleTreeSelect,
      nodeProps,
    }
  },
  render() {
    return (
      <div class={styles.container}>
        <n-card class={styles.card} content-style={'padding:20px 18px;'}>
          <n-space vertical>
            <n-input placeholder={this.t('playground.search')} style="width: 100%;"
              v-model:value={this.filterValue}
              v-slots={{
                prefix: () => <n-icon component={Search} />
              }}
            >
            </n-input>
            <n-tree
              block-line
              expand-on-click
              selected-keys={this.selectedKeys}
              on-update:selected-keys={this.handleTreeSelect}
              data={this.savedQueryList}
              pattern={this.filterValue}
              node-props={this.nodeProps}
            />
          </n-space>
        </n-card>
      </div>
    );
  }
});
