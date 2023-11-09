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
import { Layers, CodeSlashSharp, Settings, Terminal, GitBranch } from '@vicons/ionicons5';
import { useConfigStore } from '@/store/config'
import { NIcon } from 'naive-ui';

export default defineComponent({
  name: 'SliderPage',
  setup() {
    const configStore = useConfigStore()
    const { t } = useLocaleHooks()
    const router = useRouter()

    const renderIcon = (icon: any) => {
      return () => h(NIcon, { size: 24 }, { default: () => h(icon) })
    }

    const sliderVariables = reactive({
      workspaceList: [
        {
          icon: renderIcon(Layers),
          title: 'Query',
          description: computed(() => (t('playground.query'))),
          isClick: true,
          path: '/playground/query'
        },
        {
          icon: renderIcon(CodeSlashSharp),
          title: 'SQL',
          description: computed(() => (t('playground.sql'))),
          isClick: false,
          path: '/playground/workbench'
        },
      ],
      domainList: [
        {
          icon: renderIcon(Settings),
          title: 'Settings',
          description: computed(() => (t('playground.settings')))
        },
        {
          icon: renderIcon(Terminal),
          title: 'Terminal',
          description: computed(() => (t('playground.terminal')))
        },
        {
          icon: renderIcon(GitBranch),
          title: 'GitBranch',
          description: computed(() => (t('playground.git_branch'))),
        }
      ],
    })

    const handleClick = (index: number, type: string) => {
      if (type === 'workspace') {
        for (const i in sliderVariables.workspaceList) {
          sliderVariables.workspaceList[i].isClick = false
        }
        sliderVariables.workspaceList[index].isClick = true
        configStore.setCurrentMenuActive(sliderVariables.workspaceList[index].title as any)
        router.push(sliderVariables.workspaceList[index].path)
      }
    }

    onMounted(() => {
      for (const i in sliderVariables.workspaceList) {
        sliderVariables.workspaceList[i].isClick = false
        if (sliderVariables.workspaceList[i].title === configStore.getCurrentMenuActive) {
          sliderVariables.workspaceList[i].isClick = true
        }
      }
    })

    return {
      configStore,
      handleClick,
      ...toRefs(sliderVariables)
    }
  },
  render() {
    return (
      <div class={[this.configStore.getCurrentTheme === 'light' ? styles.light : styles.dark, styles.slider]}>
        <div class={styles.workspace}>
          <n-space vertical size={20}>
            { 
              this.workspaceList.map((item: any, index: number) => {
                return (
                  <n-popover trigger="hover" placement="right"
                    v-slots={{
                      trigger: () => (
                        <n-button
                          type={item.isClick ? 'primary' : 'default'}
                          text
                          onClick={() => this.handleClick(index, 'workspace')}
                          v-slots={{
                            icon: () => item.icon()
                          }}
                        >
                        </n-button>
                      )
                    }}>
                    <span>{item.description}</span>
                  </n-popover>
                )
              })
            }
          </n-space>
        </div>
        <div class={styles['functional-domain']}>
          <n-space vertical size={20}>
            { 
              this.domainList.map((item: any, index: number) => {
                return (
                  <n-popover trigger="hover" placement="right"
                    v-slots={{
                      trigger: () => (
                        <n-button
                          text
                          onClick={() => this.handleClick(index, 'domain')}
                          v-slots={{
                            icon: () => item.icon()
                          }}
                        >
                        </n-button>
                      )
                    }}>
                    <span>{item.description}</span>
                  </n-popover>
                )
              })
            }
          </n-space>
        </div>
      </div>
    );
  },
});
