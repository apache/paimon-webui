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

import type { Router } from 'vue-router';
import { listUser } from '@/api/models/user';
export const useTable = () => {
    const router: Router = useRouter()
    const { t } = useLocaleHooks()
    const tableVariables = reactive({
        columns: [
            {
                title: computed(() => t('system.user.username')),
                key: 'username',
                resizable: true
            },
            {
                title: computed(() => t('system.user.nickname')),
                key: 'nickname',
                resizable: true
            },
            {
                title: computed(() => t('system.user.mobile')),
                key: 'mobile',
                resizable: true
            },
            {
                title: computed(() => t('system.user.email')),
                key: 'email',
                resizable: true
            },
            {
                title: computed(() => t('common.create_time')),
                key: 'createTime',
                resizable: true
            },
            {
                title: computed(() => t('common.update_time')),
                key: 'updateTime',
                resizable: true
            },
            // {
            //     title: computed(() => t('cdc.operation')),
            //     key: 'actions',
            //     render: (row: any) =>
            //         h(TableAction, {
            //             row,
            //             onHandleEdit: (row) => {
            //                 getCdcJobDefinition(row.id).then(res => {
            //                     const CDCStore = useCDCStore()
            //                     CDCStore.setModel({
            //                         cells: JSON.parse(res.data.config).cells,
            //                         name: res.data.name,
            //                         editMode: 'edit',
            //                         id:res.data.id
            //                     })
            //                     router.push({ path: '/cdc_ingestion/dag' })
            //                 })
            //             },
            //             onHandleDelete:(row)=>{
            //                 deleteCdcJobDefinition(row.id).then(()=>{
            //                     window.$message.success(t('common.cdc.delete_success'))
            //                     getTableData()
            //                 })
            //             }
            //         })

            // }
        ],
        data: [],
        pagination: reactive({
            displayOrder: ['quick-jumper', 'pages', 'size-picker'],
            showQuickJumper: true,
            showSizePicker: true,
            pageSize: 10,
            pageSizes: [10, 20, 50, 100],
            page: 1,
            count: 100,
            onChange: (page: number) => {
                tableVariables.pagination.page = page
                getTableData()
            },
            onUpdatePageSize: (pageSize: number) => {
                tableVariables.pagination.pageSize = pageSize
                tableVariables.pagination.page = 1
                getTableData()
            }
        })
    })
    const getTableData = () => {
        let params = {
            currentPage: tableVariables.pagination.page,
            pageSize: tableVariables.pagination.pageSize
        }
        listUser(params).then(((res: any) => {
            tableVariables.data = res.data
            tableVariables.pagination.count = res.total
        }))
    }
    return {
        tableVariables,
        getTableData
    }
}