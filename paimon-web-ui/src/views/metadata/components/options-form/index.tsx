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

import { Add, AddCircleOutline } from '@vicons/ionicons5'

import { useCatalogStore } from '@/store/catalog'
import { createOption, type TableOption } from '@/api/models/catalog'
import { transformOption } from '@/views/metadata/constant'

import OptionContent, { newOption } from '../options-form-content'

type OptionsFormType = {
  options: TableOption[]
}

const props = {
  onConfirm: [Function, Array] as PropType<() => Promise<void>>
}

export default defineComponent({
  name: 'OptionForm',
  props,
  setup(props) {
    const { t } = useLocaleHooks()
    const message = useMessage()

    const catalogStore = useCatalogStore()
    const [, createFetch, { loading }] = createOption()

    const formRef = ref()
    const formValue = ref<OptionsFormType>({
      options: [{ ...newOption }]
    })
    const showModal = ref(false)

    const handleConfirm = async () => {
      await formRef.value.validate()
      await createFetch({
        params: transformOption({
          ...toRaw(catalogStore.currentTable),
          options: toRaw(formValue.value).options
        })
      })

      handleCloseModal()
      message.success(t('Create Successfully'))
      resetState()
      props.onConfirm!()
    }

    const handleOpenModal = (e: Event) => {
      e.stopPropagation()
      showModal.value = true
    }

    const handleCloseModal = () => {
      showModal.value = false
      resetState()
    }

    const resetState = () => {
      formValue.value = {
        options: [{ ...newOption }]
      }
    }

    const handleAddOption = () => {
      formValue.value?.options.push({ ...newOption })
    }

    return {
      formRef,
      formValue,
      showModal,
      loading,

      t,
      handleOpenModal,
      handleCloseModal,
      handleConfirm,
      handleAddOption
    }
  },
  render() {
    return (
      <>
        <n-button strong secondary circle onClick={this.handleOpenModal}>
          {{
            icon: () => <n-icon component={AddCircleOutline} />
          }}
        </n-button>
        <n-modal v-model:show={this.showModal} mask-closable={false}>
          <n-card bordered={true} title={'Create Option'} style="width: 700px">
            {{
              'header-extra': () => (
                <n-button quaternary circle size="tiny" onClick={this.handleAddOption}>
                  <n-icon>
                    <Add />
                  </n-icon>
                </n-button>
              ),
              default: () => (
                <n-form
                  ref="formRef"
                  label-placement="top"
                  label-width="auto"
                  label-align="left"
                  model={this.formValue}
                >
                  <OptionContent
                    onUpdateOptions={(value) => {
                      this.formValue.options = value
                    }}
                    options={this.formValue.options || []}
                  />
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
