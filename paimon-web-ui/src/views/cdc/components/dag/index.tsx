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

import { Leaf, Save } from "@vicons/ionicons5"
import styles from './index.module.scss';
import DagCanvas from "./dag-canvas";
import { useCDCStore } from "@/store/cdc";
import type { Router } from "vue-router";
import { createCdcJob, updateCdcJob } from "@/api/models/cdc";

export default defineComponent({
  name: 'DagPage',
  setup() {
    const { t } = useLocaleHooks()
    const CDCStore = useCDCStore()
    const name = ref('')
    onMounted(() => {
      name.value = CDCStore.getModel.name
    })

    const dagRef = ref() as any
    const handleSave = () => {
      const editMode = CDCStore.getModel.editMode
      if (editMode === 'edit') {
        updateCdcJob({ id: CDCStore.getModel.id, name: name.value + "", description: CDCStore.getModel.description, cdcType: CDCStore.getModel.synchronizationType, config: JSON.stringify(dagRef.value.graph.toJSON()) })
          .then(() => {
            router.push({ path: '/cdc_ingestion' })
          })
      } else {
        createCdcJob({ name: name.value + "", description: CDCStore.getModel.description, cdcType: CDCStore.getModel.synchronizationType, config: JSON.stringify(dagRef.value.graph.toJSON()) })
          .then(() => {
            router.push({ path: '/cdc_ingestion' })
          })
      }

    }

    const router: Router = useRouter()
    const handleJump = () => {
      router.push({ path: '/cdc_ingestion' })
    }

    onMounted(() => {
      if (dagRef.value && dagRef.value.graph) {
        dagRef.value.graph.fromJSON({
          cells: CDCStore.getModel.cells
        })
      }
    })

    return {
      t,
      name,
      handleSave,
      dagRef,
      handleJump
    }
  },
  render() {
    return (
      <n-card>
        <n-space vertical size={24}>
          <n-card>
            <div class={styles['title-bar']}>
              <n-space align="center">
                <n-icon component={Leaf} color="#2F7BEA" size="18" />
                <span class={styles.title} onClick={this.handleJump}>{this.t('cdc.cdc_job_definition')} {
                  this.name ? ` - ${this.name}` : ''
                }</span>
              </n-space>
              <div class={styles.operation}>
                <n-space>
                  <n-popover trigger="hover" placement="bottom"
                    v-slots={{
                      trigger: () => (
                        <n-button
                          onClick={this.handleSave}
                          v-slots={{
                            icon: () => <n-icon component={Save}></n-icon>
                          }}
                        >
                        </n-button>
                      )
                    }}>
                    <span>{this.t('cdc.save')}</span>
                  </n-popover>
                </n-space>
              </div>
            </div>
          </n-card>
          <DagCanvas ref='dagRef'></DagCanvas>
        </n-space>
      </n-card>
    )
  }
})
