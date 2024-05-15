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

import type { Role } from '@/api/models/role/types/role';
import styles from './index.module.scss';
import { useConstants } from './use-constants';
import { useTable } from './use-table';

export default defineComponent({
  name: 'RolePage',
  setup() {
    const { t } = useLocaleHooks()
    const { tableVariables, getTableData, roleList, loading } = useTable()
    const { columns } = useConstants()

    onMounted(getTableData)

    const rowKey = (rowData: Role) => rowData.id

    return {
      ...toRefs(tableVariables),
      t,
      columns: toRef([...columns]),
      roleList,
      loading,
      rowKey,
    }
  },
  render() {
    return (
      <n-space class={styles.container} vertical justify="center">
        <n-card >
          <n-space vertical>
            <n-space justify="space-between">
              <n-space>
                <n-button type="primary">{this.t('system.user.add')}</n-button>
              </n-space>
              <n-space>
                <></>
              </n-space>
            </n-space>
            <n-data-table
              columns={this.columns}
              data={this.roleList || []}
              pagination={this.pagination}
              loading={this.loading}
              remote
              rowKey={this.rowKey}
            />
          </n-space>
        </n-card>
      </n-space>
    )
  }
})
