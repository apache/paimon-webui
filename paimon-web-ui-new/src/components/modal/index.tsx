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
import { useTask } from "./use-task"

const props = {
  title: {
    type: String as PropType<string>,
    default: ''
  },
  row: {
    type: Object as PropType<any>,
    default: () => {}
  },
  showModal: {
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
  }
}

export default defineComponent({
  name: 'ModalPage',
  props,
  emits: ['confirm', 'cancel'],
  setup(props, { expose, emit }) {
    const { t } = useLocaleHooks()
    const formRef = ref()
    expose({formRef})

    const { elementsRef, rulesRef, model } = useTask({
      data: props.row,
      formType: props.formType
    })

    const handleConfirm = () => {
      emit('confirm')
    }

    const handleCancel = () => {
      emit('cancel')
    }

    return {
      t,
      handleConfirm,
      handleCancel,
      formRef,
      elementsRef,
      rulesRef,
      model
    }
  },
  render () {
    return (
      <n-modal
        v-model:show={this.showModal}
        mask-closable={false}
        auto-focus={this.autoFocus}
      >
        <n-card
          style="width: 600px"
          title={this.title}
          bordered={false}
          closable={this.closeable}
          on-close={this.handleCancel}
        >
          {{
            default: () => (
              <Form
                ref={this.formRef}
                meta={{
                  ...this.model,
                  rules: this.rulesRef,
                  elements: this.elementsRef,
                }}
                gridProps={{
                  xGap: 10
                }}
              />
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
        </n-card>
      </n-modal>
    )
  }
})
