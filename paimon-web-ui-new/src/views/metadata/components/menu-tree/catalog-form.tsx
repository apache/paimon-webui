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

import { Add } from '@vicons/ionicons5'

import { useCatalogStore } from '@/store/catalog'

export default defineComponent({
  name: 'CatalogForm',
  setup() {
    const { t } = useLocaleHooks()

    const catalogStore = useCatalogStore()

    const formRef = ref()
    const formValue = ref({
      name: '',
      type: '',
      warehouse: '',
    })
    const showModal = ref(false)

    const handleConfirm = async () => {
      await formRef.value.validate()
    }

    const handleOpenModal = (e: Event) => {
      e.stopPropagation()
      showModal.value = true
    }

    const handleCloseModal = () => {
      showModal.value = false
    }

    return {
      formRef,
      formValue,
      showModal,

      t,
      handleOpenModal,
      handleCloseModal,
      handleConfirm
    }
  },
  render() {
    return (
      <>
        <n-button
          quaternary
          circle
          size='tiny'
          onClick={this.handleOpenModal}
        >
          <n-icon>
            <Add />
          </n-icon>
        </n-button>
        <n-modal
          v-model:show={this.showModal}
          mask-closable={false}
        >
          <n-card bordered={false} title={this.t('metadata.create_catalog')} style="width: 600px">
            {{
              default: () => (
                <n-form
                  ref='formRef'
                  inline
                  model={this.formValue}
                >
                  <n-form-item label={this.t('metadata.catalog_name')} path='name'>
                    <n-input v-model:value={this.formValue.name} />
                  </n-form-item>
                  <n-form-item label={this.t('metadata.catalog_type')} path='type'>
                    <n-input v-model:value={this.formValue.type} />
                  </n-form-item>
                  <n-form-item label={this.t('metadata.catalog_warehouse')} path='warehouse'>
                    <n-input v-model:value={this.formValue.warehouse} />
                  </n-form-item>
                  <n-form-item>
                    <n-button attr-type='button' onClick='handleValidateClick'>
                      验证
                    </n-button>
                  </n-form-item>
                </n-form>
              ),
              footer: () => (
                <n-space justify='end'>
                  <n-button onClick={this.handleCloseModal}>
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
      </>
    )
  }
})
