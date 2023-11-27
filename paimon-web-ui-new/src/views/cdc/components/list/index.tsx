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

import { NDataTable, NPagination, NSpace } from 'naive-ui';
import styles from './index.module.scss';
import {useTable} from './use-table'

export default defineComponent({
  name: 'ListPage',
  setup() {
    const { t } = useLocaleHooks()
    
    const {tableVariables,getTableData} = useTable()
    getTableData()
    
    return {
      t,
      ...toRefs(tableVariables)
    }
  },
  render() {
    return (
      <div class={styles['list-page']}>
         <NSpace vertical>
            <NDataTable
              columns={this.columns}
              data={this.data}
            />
            <NSpace justify='center'>
              <NPagination
                v-model:page={this.pagination.page}
                v-model:page-size={this.pagination.pageSize}
                item-count={this.pagination.count}
                show-size-picker
                page-sizes={this.pagination.pageSizes}
                show-quick-jumper
                onUpdatePage={this.pagination.onChange}
                onUpdatePageSize={this.pagination.onUpdatePageSize}
              />
            </NSpace>
          </NSpace>
      </div>
    )
  }
})
