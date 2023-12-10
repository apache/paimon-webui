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

import styles from './index.module.scss';
import { useTable } from './use-table';

export default defineComponent({
  name: 'UserPage',
  setup() {
    const { t } = useLocaleHooks()
    const {tableVariables,getTableData,handleResetage} = useTable()
    getTableData()
    return {...toRefs(tableVariables),getTableData,t,handleResetage}
  },
  render() {
    return (
      <n-card class={styles.container} content-style="background:#FFF">
        <n-space vertical>
          <n-space justify="space-between">
            <n-space>
              <n-button type="primary">{this.t('system.user.add')}</n-button>
            </n-space>
            <n-space>
              <n-input v-model:value={this.searchForm.username} clearable></n-input>
              <n-button type="primary" onClick={this.handleResetage}>{this.t('common.search')}</n-button>
            </n-space>
          </n-space>
          <n-data-table
              columns={this.columns}
              data={this.data}
            />
            <n-space justify='center'>
              <n-pagination
                v-model:page={this.pagination.page}
                v-model:page-size={this.pagination.pageSize}
                item-count={this.pagination.count}
                show-size-picker
                page-sizes={this.pagination.pageSizes}
                show-quick-jumper
                onUpdatePage={this.pagination.onChange}
                onUpdatePageSize={this.pagination.onUpdatePageSize}
              />
            </n-space>
        </n-space>
      </n-card>
    )
  }
})
