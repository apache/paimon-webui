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

import UserForm from './components/user-form'
import UserDelete from './components/user-delete'

import styles from './index.module.scss'

import { createUser, getUserList, updateUser } from '@/api/models/user'

import type { User, UserDTO } from '@/api/models/user/types'

export default defineComponent({
  name: 'UserPage',
  setup() {
    const { t } = useLocaleHooks()
    const rowKey = (rowData: any) => rowData.id
    const message = useMessage()

    const columns: TableColumns<UserDTO> = [
      {
        title: () => t('system.user.username'),
        key: 'username',
      },
      {
        title: () => t('system.user.nickname'),
        key: 'nickname',
      },
      {
        title: () => t('system.user.mobile'),
        key: 'mobile',
      },
      {
        title: () => t('system.user.enabled'),
        key: 'enabled',
        render: (row: UserDTO) => {
          return row.enabled ? t('common.yes') : t('common.no')
        },
      },
      {
        title: () => t('common.create_time'),
        key: 'createTime',
        render: (row: UserDTO) => {
          return row?.createTime ? dayjs(row?.createTime).format('YYYY-MM-DD HH:mm') : '-'
        },
      },
      {
        title: () => t('common.update_time'),
        key: 'updateTime',
        render: (row: UserDTO) => {
          return row?.updateTime ? dayjs(row?.updateTime).format('YYYY-MM-DD HH:mm') : '-'
        },
      },
      {
        title: () => t('common.action'),
        key: 'actions',
        resizable: true,
        render: (row: UserDTO) => {
          return (
            <n-space>
              <n-button onClick={() => handleUpdateModal(row)} strong secondary circle>
                {{
                  icon: () => <n-icon component={EditOutlined} />,
                }}
              </n-button>
              <UserDelete userId={row?.id} onDelete={getTableData} />
            </n-space>
          )
        },
      },
    ]

    const [userList, useAccountList, { loading }] = getUserList()
    const [, createFetch, { loading: createLoading }] = createUser()
    const [, updateFetch, { loading: updateLoading }] = updateUser()

    const formType = ref<'create' | 'update'>('create')
    const formVisible = ref(false)

    const formValue = ref<UserDTO>({
      username: '',
      password: '',
      nickname: '',
      enabled: true,
      mobile: '',
      email: '',
      roleIds: [],
    })

    onMounted(getTableData)

    function handleCreateModal() {
      formType.value = 'create'
      formVisible.value = true
    }

    async function handleUpdateModal(user: UserDTO) {
      formType.value = 'update'

      delete user.createTime
      delete user.updateTime
      delete user.roles

      formValue.value = { ...user }
      formVisible.value = true
    }

    const tableVariables = reactive({
      searchForm: {
        username: '',
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
        username: tableVariables.searchForm.username,
        pageNum: tableVariables.pagination.page,
        pageSize: tableVariables.pagination.pageSize,
      }
      useAccountList({ params })
    }

    const modelLoading = computed(() => createLoading.value || updateLoading.value)

    async function onConfirm() {
      const fn = formType.value === 'create' ? createFetch : updateFetch

      const params = { ...toRaw(formValue.value) }

      if (!Array.isArray(params.roleIds))
        params.roleIds = [params.roleIds || '']

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
      userList,
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
      <div class={styles.container}>
        <n-card>
          <n-space vertical>
            <n-space justify="space-between">
              <n-space>
                <n-button onClick={this.handleCreateModal} type="primary">{this.t('system.user.add')}</n-button>
              </n-space>
              <n-space>
                <></>
              </n-space>
            </n-space>
            <n-data-table
              columns={this.columns}
              data={this.userList || []}
              pagination={this.pagination}
              loading={this.loading}
              remote
              rowKey={this.rowKey}
            />
          </n-space>
        </n-card>
        <UserForm modelLoading={this.modelLoading} formType={this.formType} v-model:visible={this.formVisible} v-model:formValue={this.formValue} onConfirm={this.onConfirm} />
      </div>
    )
  },
})
