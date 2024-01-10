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

import { CreateOutline } from '@vicons/ionicons5'

import { createOption, type TableOption } from '@/api/models/catalog'
import { useCatalogStore } from '@/store/catalog'
import IModal from '@/components/modal'
import type { IFormInst } from '@/components/dynamic-form/types'
import { transformOption } from '@/views/metadata/constant'

const props = {
  value: Object as PropType<TableOption>,
  onConfirm: [Function, Array] as PropType<() => Promise<void>>
}

export default defineComponent({
  name: 'OptionEditForm',
  props,
  setup(props) {
    const { t } = useLocaleHooks()
    const message = useMessage()

    const catalogStore = useCatalogStore()
    const [result, createFetch, { loading }] = createOption()

    const modalRef = ref<{ formRef: IFormInst }>()
    const showModal = ref(false)

    const handleConfirm = async (values: TableOption) => {
      await modalRef.value?.formRef?.validate()
      await createFetch({
        params: transformOption({
          ...toRaw(catalogStore.currentTable),
          options: [toRaw(values)]
        })
      })

      if (result.value.code === 200) {
        handleCloseModal()
        message.success(t('Create Successfully'))
        props.onConfirm!()
      }
    }

    const handleOpenModal = (e: Event) => {
      e.stopPropagation()
      showModal.value = true
    }

    const handleCloseModal = () => {
      showModal.value = false
      props.onConfirm!()
    }

    return {
      modalRef,
      showModal,
      value: toRefs(props).value,

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
            icon: () => <n-icon component={CreateOutline} />
          }}
        </n-button>
        <IModal
          ref='modalRef'
          showModal={this.showModal}
          row={this.value}
          title={'Edit Option'}
          formType={'OPTIONS'}
          onCancel={this.handleCloseModal}
          onConfirm={this.handleConfirm}
        />
      </>
    )
  }
})
