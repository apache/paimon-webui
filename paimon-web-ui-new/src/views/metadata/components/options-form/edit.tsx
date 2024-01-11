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

import { EditOutlined } from '@vicons/antd'
import type { FormInst } from 'naive-ui'

import { createOption, type TableOption } from '@/api/models/catalog'
import { useCatalogStore } from '@/store/catalog'
import { transformOption } from '@/views/metadata/constant'

export default defineComponent({
  name: 'OptionEditForm',
  props: {
    option: {
      type: Object as PropType<TableOption>,
      required: true
    },
    onConfirm: [Function, Array] as PropType<() => Promise<void>>
  },
  setup(props) {
    const rules = {
      key: {
        required: true,
        trigger: ['blur', 'input'],
        message: 'Option key required'
      },
      value: {
        required: true,
        trigger: ['blur', 'input'],
        message: 'Option value required'
      }
    }

    const { t } = useLocaleHooks()
    const message = useMessage()

    const catalogStore = useCatalogStore()
    const [, createFetch, { loading }] = createOption()

    const formRef = ref<FormInst>()
    const showModal = ref(false)

    const formValue = ref({ ...toRaw(props.option) })

    const handleConfirm = async () => {
      await formRef.value?.validate()
      await createFetch({
        params: transformOption({
          ...toRaw(catalogStore.currentTable),
          options: [toRaw(formValue.value)]
        })
      })

      handleCloseModal()
      message.success(t('Edit Successfully'))
    }

    const handleOpenModal = (e: Event) => {
      e.stopPropagation()
      formValue.value = { ...toRaw(props.option) }
      showModal.value = true
    }

    const handleCloseModal = () => {
      showModal.value = false
      props.onConfirm!()
    }

    return {
      showModal,
      formValue,
      loading,
      rules,

      t,
      handleOpenModal,
      handleCloseModal,
      handleConfirm
    }
  },
  render() {
    return (
      <>
        <n-button onClick={this.handleOpenModal} strong secondary circle>
          {{
            icon: () => <n-icon component={EditOutlined} />
          }}
        </n-button>
        <n-modal v-model:show={this.showModal} mask-closable={false}>
          <n-card bordered={true} title={'Edit Option'} style="width: 700px">
            {{
              default: () => (
                <n-form
                  ref="formRef"
                  label-placement="top"
                  label-width="auto"
                  label-align="left"
                  rules={this.rules}
                  model={this.formValue}
                >
                  <n-form-item label="Key" path="key">
                    <n-input disabled v-model:value={this.formValue.key} placeholder="Key" />
                  </n-form-item>
                  <n-form-item label="Value" path="value">
                    <n-input v-model:value={this.formValue.value} placeholder="Value" />
                  </n-form-item>
                </n-form>
              ),
              action: () => (
                <n-space justify="end">
                  <n-button onClick={this.handleCloseModal}>{this.t('layout.cancel')}</n-button>
                  <n-button type="primary" loading={this.loading} onClick={this.handleConfirm}>
                    {this.t('layout.confirm')}
                  </n-button>
                </n-space>
              )
            }}
          </n-card>
        </n-modal>
      </>
    )
  }
})
