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

import i18n from "@/locales"
import type { TreeOption } from "naive-ui";
import styles from './index.module.scss'

export default defineComponent({
  name: 'CataLogPage',
  setup() {
    const selectedValue= ref(null)

    const options= [
      {
        label: 'catalog1',
        value: 'catalog1'
      },
      {
        label: 'catalog2',
        value: 'catalog2'
      }
    ]

    const searchVal = ref('')

    const treeData: TreeOption[] = [
      {
        label: 'paimon',
        key: 'paimon',
        children: [
          {
            label: 'paimon-table-01',
            key: 'paimon-table-01',
            children: [
              { label: 'id', key: 'id' },
              { label: 'name', key: 'name' }
            ]
          },
          {
            label: 'paimon-table-02',
            key: 'paimon-table-02',
            children: [
              { label: 'Osaka', key: 'Osaka' },
            ]
          }
        ]
      }
    ]

    return { selectedValue, options, searchVal, treeData }
  },
  render() {
    return (
      <div class={styles.container}>
        <div class={styles['select-catalog']}>
          <n-select
            v-model:value={this.selectedValue}
            filterable
            placeholder={i18n.global.t('playground.select_catalog')}
            options={this.options}
          /> 
        </div>
        <n-space vertical size={12}>
          <n-input v-model:value={this.searchVal} placeholder={i18n.global.t('playground.search')} />
          <n-tree
            pattern={this.searchVal}
            data={this.treeData}
            block-line
          />
        </n-space>
      </div>
    );
  },
});
