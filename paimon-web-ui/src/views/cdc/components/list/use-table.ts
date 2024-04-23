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

import { useCDCStore } from '@/store/cdc';
import type { Router } from 'vue-router';
import TableAction from '@/components/table-action';
import { deleteCdcJobDefinition, getCdcJobDefinition, listAllCdcJob } from '@/api/models/cdc';
import { get } from 'lodash';
export const useTable = () => {
    const router: Router = useRouter()
    const { t } = useLocaleHooks()
    const tableVariables = reactive({
        columns: [
            {
                title: t('cdc.job_name'),
                key: 'name',
                resizable: true
            },
            {
                title: t('cdc.synchronization_type'),
                key: 'cdcType',
                resizable: true,
                render: (row:any)=>{
                    if(row.cdcType == 0){
                        return t('cdc.single_table_synchronization')
                    }else if(row.cdcType ==1){
                        return  t('cdc.whole_database_synchronization')
                    }
                }
            },
            {
                title: t('cdc.job_description'),
                key: 'description',
                resizable: true
            },
            {
                title: t('cdc.create_user'),
                key: 'createUser',
                resizable: true
            },
            {
                title: t('cdc.create_time'),
                key: 'createTime',
                resizable: true
            },
            {
                title: t('cdc.update_time'),
                key: 'updateTime',
                resizable: true
            },
            {
                title: t('cdc.operation'),
                key: 'actions',
                render: (row: any) =>
                    h(TableAction, {
                        row,
                        onHandleEdit: (row) => {
                            getCdcJobDefinition(row.id).then(res => {
                                const CDCStore = useCDCStore()
                                CDCStore.setModel({
                                    cells: JSON.parse(res.data.config).cells,
                                    name: res.data.name,
                                    editMode: 'edit',
                                    id:res.data.id
                                })
                                router.push({ path: '/cdc_ingestion/dag' })
                            })
                        },
                        onHandleDelete:(row)=>{
                            deleteCdcJobDefinition(row.id).then(()=>{
                                getTableData()
                            })
                        }
                    })

            }
        ],
        data: [],
        pagination: {
            showQuickJumper: true,
            showSizePicker: true,
            pageSize: 10,
            page: 1,
            count: 100,
            onUpdatePage: (page: number) => {
                tableVariables.pagination.page = page
                getTableData()
            },
            onUpdatePageSize: (pageSize: number) => {
                tableVariables.pagination.pageSize = pageSize
                tableVariables.pagination.page = 1
                getTableData()
            }
        }
    })
    const getTableData = () => {
        listAllCdcJob(false, tableVariables.pagination.page, tableVariables.pagination.pageSize).then(((res: any) => {
            tableVariables.data = res.data
            tableVariables.pagination.count = res.total
        }))
    }
    return {
        tableVariables,
        getTableData
    }
}
