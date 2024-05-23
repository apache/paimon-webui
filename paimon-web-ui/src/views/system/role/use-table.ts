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

import { listRoles } from '@/api/models/role'

export function useTable() {
  const tableVariables = reactive({
    searchForm: {
      roleName: '',
    },
    pagination: {
      showQuickJumper: true,
      showSizePicker: true,
      pageSize: 10,
      page: 1,
      itemCount: 0,
      onUpdatePage: (page: number) => {
        tableVariables.pagination.page = page
        getTableData()
      },
    },
  })

  const [roleList, useRoleList, { loading }] = listRoles()

  const getTableData = () => {
    const params = {
      roleName: tableVariables.searchForm.roleName,
      currentPage: tableVariables.pagination.page,
      pageSize: tableVariables.pagination.pageSize,
    }
    useRoleList({ params })
  }

  const handleResetage = () => {
    tableVariables.pagination.page = 1
    getTableData()
  }

  return {
    tableVariables,
    roleList,
    loading,
    getTableData,
    handleResetage,
  }
}
