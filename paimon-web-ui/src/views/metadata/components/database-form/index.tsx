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

import { createDatabase, type DatabaseFormDTO } from '@/api/models/catalog'
import { useCatalogStore } from '@/store/catalog'
import IModal from '@/components/modal'
import type { IFormInst } from '@/components/dynamic-form/types'

const props = {
  catalogId: {
    type: Number as PropType<number>,
    require: true
  }
}

export default defineComponent({
  name: 'DatabaseForm',
  props,
  setup(props) {
    const { t } = useLocaleHooks()
    const message = useMessage()

    const catalogStore = useCatalogStore()
    const [, createFetch, { loading }] = createDatabase()

    const modalRef = ref<{ formRef: IFormInst }>()
    const showModal = ref(false)

    const handleConfirm = async (values: DatabaseFormDTO) => {
      await modalRef.value?.formRef?.validate()
      if (props.catalogId) {
        await createFetch({
          params: {
            ...values,
            catalogId: props.catalogId
          }
        })

        handleCloseModal()
        message.success(t('Create Successfully'))
        values = reactive({
          name: ''
        })
        catalogStore.getAllCatalogs(true)
      }
    }

    const handleOpenModal = (e: Event) => {
      e.stopPropagation()
      showModal.value = true
    }

    const handleCloseModal = () => {
      showModal.value = false
      modalRef.value?.formRef?.resetValues({
        name: ''
      })
    }

    return {
      modalRef,
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
        <n-button quaternary circle size="tiny" onClick={this.handleOpenModal}>
          <n-icon>
            <Add />
          </n-icon>
        </n-button>
        <IModal
          ref='modalRef'
          showModal={this.showModal}
          title={this.t(`metadata.create_database`)}
          formType={'DATABASE'}
          onCancel={this.handleCloseModal}
          onConfirm={this.handleConfirm}
        />
      </>
    )
  }
})
