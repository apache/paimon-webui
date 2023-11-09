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

import { FileTrayFullOutline, Search, ServerOutline } from '@vicons/ionicons5'
import styles from './index.module.scss'
import { NIcon } from 'naive-ui'

export default defineComponent({
  name: 'MenuTree',
  emits: ['selected'],
  setup(props, { emit }) {
    const { t } = useLocaleHooks()

    const treeVariables = reactive({
      treeData: [
        {
          key: 'paimon',
          label: 'paimon',
          prefix: () =>
            h(NIcon, null, {
              default: () => h(ServerOutline)
            }),
          children: [
            {
              key: 'user',
              label: 'user',
              prefix: () =>
                h(NIcon, null, {
                  default: () => h(ServerOutline)
                }),
              children: [
                {
                  label: 'user_table',
                  key: '1',
                  content: 'select * from abc where abc.a="abc";select * from cba where cba.a="cba";',
                  prefix: () =>
                    h(NIcon, null, {
                      default: () => h(FileTrayFullOutline)
                    })
                },
                {
                  label: 'people_table',
                  key: '2',
                  content: 'select * from abc where abc.a="abc";',
                  prefix: () =>
                    h(NIcon, null, {
                      default: () => h(FileTrayFullOutline)
                    })
                }
              ]
            },
            {
              key: 'role',
              label: 'role',
              prefix: () =>
                h(NIcon, null, {
                  default: () => h(ServerOutline)
                }),
              children: [
                {
                  label: 'user_table',
                  key: '3',
                  content: 'select * from kkk;',
                  prefix: () =>
                    h(NIcon, null, {
                      default: () => h(FileTrayFullOutline)
                    })
                },
              ]
            }
          ]
        }
      ],
      filterValue: '',
      selectedKeys: []
    })

    const handleTreeSelect = (value: never[], option: { children: any; }[]) => {
      if (option[0]?.children) return
      treeVariables.selectedKeys = value
      emit('selected', value)
    }

    return {
      t,
      ...toRefs(treeVariables),
      handleTreeSelect
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
              data={this.treeData}
              pattern={this.filterValue}
            />
          </n-space>
        </n-card>
      </div>
    );
  }
});
