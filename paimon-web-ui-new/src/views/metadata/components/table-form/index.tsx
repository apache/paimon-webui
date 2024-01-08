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
import { createTable, type TableDTO, type TableOption } from '@/api/models/catalog'

import OptionContent, { newOption } from '../options-form-content'
import TableColumnContent from '../table-column-content'

import styles from './index.module.scss'

export default defineComponent({
  name: 'TableForm',
  setup() {
    const rules = {}

    const { t } = useLocaleHooks()
    const message = useMessage()

    const catalogStore = useCatalogStore()
    const [result, createFetch, { loading }] = createTable()

    const formRef = ref()
    const formValue = ref<TableDTO>({
      name: '',
      tableColumns: [
        {
          field: '',
          dataType: {
            type: '',
            nullable: true
          },
          comment: '',
          defaultValue: '',
          pk: false
        }
      ],
      options: []
    })
    const showModal = ref(false)

    const handleConfirm = async () => {
      await formRef.value.validate()
      await createFetch({
        params: toRaw(formValue.value)
      })

      if (result.value.code === 200) {
        handleCloseModal()
        message.success(t('Create Successfully'))
        formValue.value = {
          name: ''
        }
        catalogStore.getAllCatalogs(true)
      }
    }

    const handleOpenModal = (e: Event) => {
      e.stopPropagation()
      showModal.value = true
    }

    const handleCloseModal = () => {
      showModal.value = false
    }

    const handleAddOption = () => {
      formValue.value.options?.push({ ...newOption })
    }

    return {
      formRef,
      formValue,
      showModal,
      loading,

      rules,

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
                  <n-form-item label={this.t('metadata.table_name')} path="name">
                    <n-input v-model:value={this.formValue.name} />
                  </n-form-item>
                  <n-form-item label={this.t('metadata.table_des')} path="type">
                    <n-input type="textarea" />
                  </n-form-item>

                  <n-space align="center" justify="space-between" class={styles.form_title}>
                    {this.t('metadata.table_columns')}
                    <n-button circle onClick={this.handleOpenModal}>
                      <n-icon>
                        <Add />
                      </n-icon>
                    </n-button>
                  </n-space>
                  <TableColumnContent
                    onUpdateColumns={(value) => {
                      this.formValue.tableColumns = value
                    }}
                    data={this.formValue.tableColumns}
                  />
                  <div class={styles.form_title}>{this.t('metadata.partition_columns')}</div>
                  <n-form-item showLabel={false} path="type">
                    <n-select placeholder="Select" />
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
