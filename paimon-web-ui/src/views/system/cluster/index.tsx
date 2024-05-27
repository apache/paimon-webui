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

import type { TableColumns } from 'naive-ui/es/data-table/src/interface'
import dayjs from 'dayjs'
import { EditOutlined } from '@vicons/antd'

import ClusterForm from './components/cluster-form'
import ClusterDelete from './components/cluster-delete'

import styles from './index.module.scss'

import { createCluster, getClusterList, updateCluster } from '@/api/models/cluster'

import type { ClusterDTO } from '@/api/models/cluster/types'

export default defineComponent({
  name: 'ClusterPage',
  setup() {
    const { t } = useLocaleHooks()
    const rowKey = (rowData: any) => rowData.id
    const message = useMessage()

    const columns: TableColumns<ClusterDTO> = [
      {
        title: () => t('system.cluster.cluster_name'),
        key: 'clusterName',
      },
      {
        title: () => t('system.cluster.cluster_host'),
        key: 'host',
      },
      {
        title: () => t('system.cluster.cluster_port'),
        key: 'port',
      },
      {
        title: () => t('system.cluster.cluster_type'),
        key: 'type',
      },
      {
        title: () => t('system.cluster.enabled'),
        key: 'enabled',
        render: (row: ClusterDTO) => {
          return row.enabled ? t('common.yes') : t('common.no')
        },
      },
      {
        title: () => t('common.create_time'),
        key: 'createTime',
        render: (row: ClusterDTO) => {
          return row?.createTime ? dayjs(row?.createTime).format('YYYY-MM-DD HH:mm') : '-'
        },
      },
      {
        title: () => t('common.update_time'),
        key: 'updateTime',
        render: (row: ClusterDTO) => {
          return row?.updateTime ? dayjs(row?.updateTime).format('YYYY-MM-DD HH:mm') : '-'
        },
      },
      {
        title: () => t('common.action'),
        key: 'actions',
        resizable: true,
        render: (row: ClusterDTO) => {
          return (
            <n-space>
              <n-button onClick={() => handleUpdateModal(row)} strong secondary circle>
                {{
                  icon: () => <n-icon component={EditOutlined} />,
                }}
              </n-button>
              <ClusterDelete clusterId={row?.id} onDelete={getTableData} />
            </n-space>
          )
        },
      },
    ]

    const [clusterList, useClusterList, { loading }] = getClusterList()
    const [, createFetch, { loading: createLoading }] = createCluster()
    const [, updateFetch, { loading: updateLoading }] = updateCluster()

    const formType = ref<'create' | 'update'>('create')
    const formVisible = ref(false)

    const formValue = ref<ClusterDTO>({
      clusterName: '',
      host: '',
      port: 0,
      type: '',
      enabled: true,
    })

    onMounted(getTableData)

    function handleCreateModal() {
      formType.value = 'create'
      formVisible.value = true
    }

    async function handleUpdateModal(cluster: ClusterDTO) {
      formType.value = 'update'

      delete cluster.createTime
      delete cluster.updateTime

      formValue.value = { ...cluster }
      formVisible.value = true
    }

    const tableVariables = reactive({
      searchForm: {
        clusterName: '',
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

    function getTableData() {
      const params = {
        pageNum: tableVariables.pagination.page,
        pageSize: tableVariables.pagination.pageSize,
      }
      useClusterList({ params })
    }

    const modelLoading = computed(() => createLoading.value || updateLoading.value)

    async function onConfirm() {
      const fn = formType.value === 'create' ? createFetch : updateFetch

      const params = { ...toRaw(formValue.value) }

      await fn({
        params,
      })

      message.success(t(`Successfully`))

      formVisible.value = false
      getTableData()
    }

    return {
      t,
      rowKey,
      modelLoading,
      columns,
      loading,
      clusterList,
      ...toRefs(tableVariables),

      formType,
      formVisible,
      formValue,
      handleCreateModal,
      onConfirm,
    }
  },
  render() {
    return (
      <n-space class={styles.container} vertical justify="center">
        <n-card>
          <n-space vertical>
            <n-space justify="space-between">
              <n-button onClick={this.handleCreateModal} type="primary">{this.t('system.user.add')}</n-button>
            </n-space>
            <n-data-table
              columns={this.columns}
              data={this.clusterList || []}
              pagination={this.pagination}
              loading={this.loading}
              remote
              rowKey={this.rowKey}
            />
          </n-space>
        </n-card>
        <ClusterForm modelLoading={this.modelLoading} formType={this.formType} v-model:visible={this.formVisible} v-model:formValue={this.formValue} onConfirm={this.onConfirm} />
      </n-space>
    )
  },
})
