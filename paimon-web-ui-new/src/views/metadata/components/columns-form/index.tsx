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
import { type ColumnDTO, createColumns } from '@/api/models/catalog'
import { transformOption } from '@/views/metadata/constant'

import ColumnFormContent, { newField } from '../table-column-content'

type ColumnFormType = {
  tableColumns: ColumnDTO[]
}

const props = {
  visible: {
    type: Boolean as PropType<boolean>,
    required: true
  },
  tableColumns: {
    type: Object as PropType<ColumnDTO[]>
  },
  onConfirm: [Function, Array] as PropType<() => Promise<void>>,
  onClose: [Function, Array] as PropType<() => void>
}

export default defineComponent({
  name: 'ColumnForm',
  props,
  setup(props) {
    const { t } = useLocaleHooks()
    const message = useMessage()

    const catalogStore = useCatalogStore()
    const [, createFetch, { loading }] = createColumns()

    const formRef = ref()
    const formValue = ref<ColumnFormType>(resetState())

    const isEdit = computed(() => {
      return Boolean(props.tableColumns)
    })

    const handleConfirm = async () => {
      await formRef.value.validate()
      await createFetch({
        params: transformOption({
          ...toRaw(catalogStore.currentTable),
          tableColumns: toRaw(formValue.value).tableColumns
        })
      })

      handleCloseModal()
      message.success(t(`${isEdit.value ? 'Edit' : 'Create'} Column`))
      props.onConfirm!()
    }

    const handleCloseModal = () => {
      props.onClose!()
      nextTick(() => {
        formValue.value = resetState()
      })
    }

    function resetState() {
      return {
        tableColumns: (Boolean(props.tableColumns)
          ? [...(toRaw(props.tableColumns) || [])]
          : [JSON.parse(JSON.stringify(newField))]) as ColumnDTO[]
      }
    }

    watch(
      () => isEdit.value,
      () => {
        formValue.value = resetState()
      }
    )

    const handleAddOption = () => {
      formValue.value?.tableColumns.push(JSON.parse(JSON.stringify(newField)))
    }

    return {
      formRef,
      formValue,
      loading,
      isEdit,

      ...toRefs(props),

      t,
      handleCloseModal,
      handleConfirm,
      handleAddOption
    }
  },
  render() {
    return (
      <n-modal v-model:show={this.visible} mask-closable={false}>
        <n-card
          bordered={true}
          title={`${this.isEdit ? 'Edit' : 'Create'} Column`}
          style="width: 1100px"
        >
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
                <ColumnFormContent
                  data={this.formValue.tableColumns}
                  onUpdateColumns={(value) => (this.formValue.tableColumns = [...value])}
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
    )
  }
})
