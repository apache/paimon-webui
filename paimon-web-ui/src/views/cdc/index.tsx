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

import { Leaf, Search } from '@vicons/ionicons5'
import type { Router } from 'vue-router'
import List from './components/list'
import styles from './index.module.scss'
import Modal from '@/components/modal'
import { useCDCStore } from '@/store/cdc'
import { type CdcJobSubmit, submitCdcJob } from '@/api/models/cdc'

export default defineComponent({
  name: 'CDCPage',
  setup() {
    const { t } = useLocaleHooks()
    const showModalRef = ref(false)
    const showSubmitCdcJobModalRef = ref(false)

    const filterValue = ref()
    const CDCStore = useCDCStore()
    const router: Router = useRouter()
    const CDCModalRef = ref()
    const submitCdcJobModalRef = ref()
    const handleConfirm = async (model: any) => {
      CDCStore.setModel(model)
      await CDCModalRef.value.formRef.validate()
      showModalRef.value = false
      router.push({ path: '/cdc_ingestion/dag' })
    }

    const cdcJobTableRef = ref()

    function handleOpenModal() {
      showModalRef.value = true
    }

    function handleOpenSubmitCdcJobModal() {
      showSubmitCdcJobModalRef.value = true
    }

    function handleCdcSubmitConfirm(form: CdcJobSubmit) {
      const CDCStore = useCDCStore()
      submitCdcJob(CDCStore.getModel.id, form)
      showSubmitCdcJobModalRef.value = false
    }

    function handleSeachCdcJobTable() {
      cdcJobTableRef.value.getTableData(filterValue.value)
    }

    return {
      t,
      showModalRef,
      showSubmitCdcJobModalRef,
      handleOpenModal,
      handleConfirm,
      CDCModalRef,
      submitCdcJobModalRef,
      handleOpenSubmitCdcJobModal,
      handleCdcSubmitConfirm,
      cdcJobTableRef,
      handleSeachCdcJobTable,
      filterValue,
    }
  },
  render() {
    return (
      <div class={styles['cdc-page']}>
        <n-card>
          <n-space vertical size={24}>
            <n-card>
              <div class={styles.title}>
                <n-space align="center">
                  <n-icon component={Leaf} color="#2F7BEA" size="18" />
                  <span>{this.t('cdc.cdc_job_definition')}</span>
                </n-space>
                <div class={styles.operation}>
                  <n-space>
                    <n-input
                      placeholder={this.t('playground.search')}
                      v-model:value={this.filterValue}
                      v-slots={{
                        prefix: () => <n-icon component={Search} />,
                      }}
                      onBlur={this.handleSeachCdcJobTable}
                    />
                    <n-button type="primary" onClick={this.handleOpenModal}>
                      {this.t('cdc.create_synchronization_job')}
                    </n-button>
                  </n-space>
                </div>
              </div>
            </n-card>
            <List ref="cdcJobTableRef" onCdcJobSubmit={() => (this.showSubmitCdcJobModalRef = true)}></List>
            {this.showModalRef && (
              <Modal
                ref="CDCModalRef"
                showModal={this.showModalRef}
                title={this.t('cdc.create_synchronization_job')}
                formType="CDCLIST"
                onCancel={() => (this.showModalRef = false)}
                onConfirm={this.handleConfirm}
              />
            )}
            {this.showSubmitCdcJobModalRef && (
              <Modal
                ref="submitCdcJobModalRef"
                showModal={this.showSubmitCdcJobModalRef}
                title={this.t('cdc.submit_cdc_job')}
                formType="CDCSUBMIT"
                onCancel={() => (this.showSubmitCdcJobModalRef = false)}
                onConfirm={this.handleCdcSubmitConfirm}
              />
            )}
          </n-space>
        </n-card>
      </div>
    )
  },
})
