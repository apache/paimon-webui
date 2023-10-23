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

import type { TabsInst } from 'naive-ui'

import { useConfigStore } from '@/store/config'

import MetaDataSchema from '../schema';
import MetaDataTable from '../table';

import styles from './index.module.scss'


export default defineComponent({
  name: 'MetadataTabs',
  setup() {
    const { t } = useLocaleHooks()
    const tabsInstRef = ref<TabsInst | null>(null)
    const configStore = useConfigStore()

    watch(() => configStore.getCurrentLocale, () => {
      nextTick(tabsInstRef.value?.syncBarPosition)
    })

    return {
      tabsInstRef,
      t,
    }
  },
  render() {
    return (
      <div class={styles.tabs}>
        <n-tabs ref="tabsInstRef" type='bar' animated default-value="table">
          <n-tab-pane name='table' tab={this.t('metadata.table_info')}>
            <MetaDataTable />
          </n-tab-pane>
          <n-tab-pane name='option' tab={this.t('metadata.option_info')}>
            Option 信息
          </n-tab-pane>
          <n-tab-pane name='schema' tab={this.t('metadata.schema_info')}>
            <MetaDataSchema />
          </n-tab-pane>
          <n-tab-pane name='snapshot' tab={this.t('metadata.snapshot_file')}>
            Snapshot 文件
          </n-tab-pane>
          <n-tab-pane name='manifest' tab={this.t('metadata.manifests_file')}>
            Manifest 文件
          </n-tab-pane>
          <n-tab-pane name='datafile' tab={this.t('metadata.data_file')}>
            数据文件
          </n-tab-pane>
        </n-tabs>
      </div>
    );
  },
});
