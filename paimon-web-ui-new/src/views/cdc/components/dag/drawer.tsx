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

import Form from "@/components/dynamic-form"
import { useTask } from "@/components/modal/use-task"

const props = {
  title: {
    type: String as PropType<string>,
    default: ''
  },
  row: {
    type: Object as PropType<any>,
    default: () => {}
  },
  showDrawer: {
    type: Boolean as PropType<boolean>,
    default: false
  },
  autoFocus: {
    type: Boolean as PropType<boolean>,
    default: false
  },
  closeable: {
    type: Boolean as PropType<boolean>,
    default: true
  },
  formType: {
    type: String as PropType<string>,
    default: ''
  },
}

export default defineComponent({
  name: 'DrawerPage',
  props,
  emits: ['confirm', 'cancel'],
  setup(props, { expose, emit }) {
    const { t } = useLocaleHooks()
    const formRef = ref()
    expose({formRef})

    const chooseTab = ref('connection_information')

    const { elementsRef, rulesRef, model } = useTask({
      data: {
        ...props.row,
        tabType: chooseTab
      },
      formType: props.formType
    })

    const handleConfirm = () => {
      emit('confirm', model)
    }

    const handleCancel = () => {
      emit('cancel')
    }


    watch(
      () => props.row.type,
      (val) => {
        if (val === 'INPUT') {
          chooseTab.value = 'connection_information'
        } else if (val === 'OUTPUT') {
          chooseTab.value = 'catalog_configuration'
        }
      },
      {
        immediate: true
      }
    )
    
    return {
      t,
      handleConfirm,
      handleCancel,
      formRef,
      elementsRef,
      rulesRef,
      model,
      chooseTab
    }
  },
  render () {
    return (
      <n-drawer
        v-model:show={this.showDrawer}
        mask-closable={false}
        auto-focus={this.autoFocus}
        default-width="502"
        resizable
      >
        <n-drawer-content>
          {{
            default: () => (
              <n-tabs type="line" v-model:value={this.chooseTab}>
                {
                  this.row.type === 'INPUT' &&
                  <n-tab-pane name="connection_information" tab={this.t('cdc.connection_information')}>
                    <Form
                      ref={this.formRef}
                      meta={{
                        model: this.model,
                        rules: this.rulesRef,
                        elements: this.elementsRef,
                      }}
                      gridProps={{
                        xGap: 10
                      }}
                    />
                  </n-tab-pane>
                }
                {
                  this.row.type === 'OUTPUT' &&
                  <n-tab-pane name="catalog_configuration" tab={this.t('cdc.catalog_configuration')}>
                    <Form
                      ref={this.formRef}
                      meta={{
                        model: this.model,
                        rules: this.rulesRef,
                        elements: this.elementsRef,
                      }}
                      gridProps={{
                        xGap: 10
                      }}
                    />
                  </n-tab-pane>
                }
                <n-tab-pane name="synchronization_configuration" tab={this.t('cdc.synchronization_configuration')}>
                  <Form
                    ref={this.formRef}
                    meta={{
                      model: this.model,
                      rules: this.rulesRef,
                      elements: this.elementsRef,
                    }}
                    gridProps={{
                      xGap: 10
                    }}
                  />
                </n-tab-pane>
              </n-tabs>
            ),
            footer: () => (
              <n-space justify='end'>
                <n-button onClick={this.handleCancel}>
                  {this.t('layout.cancel')}
                </n-button>
                <n-button type='primary' onClick={this.handleConfirm}>
                  {this.t('layout.confirm')}
                </n-button>
              </n-space>
            )
          }}
        </n-drawer-content>
      </n-drawer>
    )
  }
})
