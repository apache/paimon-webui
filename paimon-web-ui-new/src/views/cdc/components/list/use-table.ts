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
                title: computed(() => t('cdc.job_name')),
                key: 'name',
                resizable: true
            },
            {
                title: computed(() => t('cdc.synchronization_type')),
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
                title: computed(() => t('cdc.job_description')),
                key: 'description',
                resizable: true
            },
            {
                title: computed(() => t('cdc.create_user')),
                key: 'createUser',
                resizable: true
            },
            {
                title: computed(() => t('cdc.create_time')),
                key: 'createTime',
                resizable: true
            },
            {
                title: computed(() => t('cdc.update_time')),
                key: 'updateTime',
                resizable: true
            },
            {
                title: computed(() => t('cdc.operation')),
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
                                window.$message.success(t('delete_success'))
                                getTableData()
                            })
                        }
                    })

            }
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