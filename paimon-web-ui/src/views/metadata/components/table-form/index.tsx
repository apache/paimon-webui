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
import { createTable, type TableDTO } from '@/api/models/catalog'
import { transformOption } from '@/views/metadata/constant'

import OptionContent, { newOption } from '../options-form-content'
import ColumnFormContent, { newField } from '../table-column-content'

import styles from './index.module.scss'

const props = {
  catalogId: {
    type: Number as PropType<number>,
    require: true
  },
  catalogName: {
    type: String as PropType<string>,
    require: true
  },
  databaseName: {
    type: String as PropType<string>,
    require: true
  }
}

const resetFormValue = () => {
  return {
    name: '',
    tableColumns: [
      {
        field: '',
        dataType: {
          nullable: true,
          type: undefined
        },
        comment: '',
        defaultValue: '',
        pk: false
      }
    ],
    options: []
  }
}

export default defineComponent({
  name: 'TableForm',
  props,
  setup(props) {
    const rules = {}

    const { t } = useLocaleHooks()
    const message = useMessage()

    const catalogStore = useCatalogStore()
    const [, createFetch, { loading }] = createTable()

    const formRef = ref()
    const formValue = ref<TableDTO>(resetFormValue())
    const showModal = ref(false)

    const tableKeys = computed(() => {
      return formValue.value
        .tableColumns!.filter((item) => Boolean(item.field))
        .map((item) => {
          return {
            label: item.field,
            value: item.field
          }
        })
    })

    const handleConfirm = async () => {
      await formRef.value.validate()

      await createFetch({
        params: {
          ...toRaw(props),
          ...transformOption(toRaw(formValue.value))
        }
      })

      handleCloseModal()
      message.success(t('Create Successfully'))
      formValue.value = resetFormValue()
      await catalogStore.getAllCatalogs(true)
    }

    const handleOpenModal = (e: Event) => {
      e.stopPropagation()
      showModal.value = true
    }

    const handleCloseModal = () => {
      showModal.value = false
      formValue.value = resetFormValue()
    }

    const handleAddOption = () => {
      formValue.value.options?.push({ ...newOption })
    }

    const handleAddColumn = () => {
      formValue.value.tableColumns?.push(JSON.parse(JSON.stringify(newField)))
    }

    return {
      formRef,
      formValue,
      showModal,
      loading,

      tableKeys,
      rules,

      t,
      handleOpenModal,
      handleCloseModal,
      handleConfirm,
      handleAddOption,
      handleAddColumn
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
        <n-modal v-model:show={this.showModal} mask-closable={false}>
          <n-card bordered={false} title={this.t('metadata.create_table')} style="width: 1100px">
            {{
              default: () => (
                <n-form ref="formRef" rules={this.rules} model={this.formValue}>
                  <div class={styles.form_title}>{this.t('metadata.table_basic_information')}</div>
                  <n-form-item
                    rule={{
                      required: true,
                      message: 'Name is required',
                      trigger: ['input', 'blur']
                    }}
                    label={this.t('metadata.table_name')}
                    path="name"
                  >
                    <n-input v-model:value={this.formValue.name} />
                  </n-form-item>
                  <n-form-item label={this.t('metadata.table_des')} path="type">
                    <n-input v-model:value={this.formValue.description} type="textarea" />
                  </n-form-item>

                  <n-space align="center" justify="space-between" class={styles.form_title}>
                    {this.t('metadata.table_columns')}
                    <n-button circle onClick={this.handleAddColumn}>
                      <n-icon>
                        <Add />
                      </n-icon>
                    </n-button>
                  </n-space>
                  <ColumnFormContent
                    onUpdateColumns={(value) => {
                      this.formValue.tableColumns = value
                    }}
                    data={this.formValue.tableColumns}
                  />
                  <div class={styles.form_title}>{this.t('metadata.partition_columns')}</div>
                  <n-form-item showLabel={false} path="type">
                    <n-select
                      multiple
                      options={this.tableKeys}
                      v-model:value={this.formValue.partitionKey}
                      placeholder="Select"
                    />
                  </n-form-item>
                  <n-space align="center" justify="space-between" class={styles.form_title}>
                    {this.t('metadata.table_add_options')}
                    <n-button circle onClick={this.handleAddOption}>
                      <n-icon>
                        <Add />
                      </n-icon>
                    </n-button>
                  </n-space>
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
