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

import dayjs from 'dayjs'
import { EditOutlined } from '@vicons/antd'
import styles from './index.module.scss'
import { useTable } from './use-table'
import RoleForm from './components/role-form'
import RoleDetail from './components/role-detail'
import RoleDelete from './components/role-delete'
import { createRole, getPermissionByRoleId, updateRole } from '@/api/models/role'
import type {Role, RoleDTO, RoleMenu} from '@/api/models/role/types'

export default defineComponent({
  name: 'RolePage',
  setup() {
    const { t } = useLocaleHooks()
    const { tableVariables, getTableData, roleList, loading } = useTable()
    const [, createFetch, { loading: createLoading }] = createRole()
    const [, updateFetch, { loading: updateLoading }] = updateRole()
    const message = useMessage()

    const columns = [
      {
        type: 'expand',
        resizable: true,
        renderExpand: (row: Role) => {
          return (
            <RoleDetail roleRecord={row} />
          )
        },
      },
      {
        title: () => t('system.role.role_name'),
        key: 'roleName',
        resizable: true,
      },
      {
        title: () => t('system.role.role_key'),
        key: 'roleKey',
        resizable: true,
      },
      {
        title: () => t('system.role.enabled'),
        key: 'enabled',
        resizable: true,
        render: (row: Role) => {
          return row.enabled ? t('common.yes') : t('common.no')
        },
      },
      {
        title: () => t('common.create_time'),
        key: 'createTime',
        resizable: true,
        render: (row: Role) => {
          return row?.createTime ? dayjs(row?.createTime).format('YYYY-MM-DD HH:mm') : '-'
        },
      },
      {
        title: () => t('common.update_time'),
        key: 'updateTime',
        resizable: true,
        render: (row: Role) => {
          return row?.updateTime ? dayjs(row?.updateTime).format('YYYY-MM-DD HH:mm') : '-'
        },
      },
      {
        title: () => t('common.action'),
        key: 'actions',
        resizable: true,
        render: (row: Role) => {
          if (!row?.admin) {
            return (
              <n-space>
                <n-button onClick={() => handleUpdateModal(row)} strong secondary circle>
                  {{
                    icon: () => <n-icon component={EditOutlined} />,
                  }}
                </n-button>
                <RoleDelete roleId={row?.id} onDelete={getTableData} />
              </n-space>
            )
          }

          return null
        },
      },
    ]

    const formType = ref<'create' | 'update'>('create')
    const formVisible = ref(false)
    const formValue = ref<RoleDTO>({
      roleName: '',
      roleKey: '',
      enabled: true,
      remark: '',
      menuIds: [],
      indeterminateKeys: [],
    })

    function inferIndeterminateKeys(checkedKeys: number[], allNodes: RoleMenu[]): { updatedCheckedKeys: number[], indeterminateKeys: number[] } {
      const indeterminateKeys = new Set<number>()
      const updatedCheckedKeys = new Set<number>(checkedKeys)

      function checkNode(node: RoleMenu): boolean {
        let childCheckedCount = 0
        let childCount = node.children ? node.children.length : 0

        if (childCount > 0) {
          let allChildrenChecked = true

          node.children.forEach(child => {
            const childIsChecked = checkNode(child)
            if (childIsChecked) {
              childCheckedCount++
            } else {
              allChildrenChecked = false
            }
          })

          if (childCheckedCount > 0 && childCheckedCount < childCount) {
            indeterminateKeys.add(node.id)
            updatedCheckedKeys.delete(node.id)
          }

          return allChildrenChecked
        }

        return checkedKeys.includes(node.id)
      }

      allNodes.forEach(node => {
        if (checkNode(node) && !checkedKeys.includes(node.id)) {
          indeterminateKeys.add(node.id)
        }
      })

      return {
        updatedCheckedKeys: Array.from(updatedCheckedKeys),
        indeterminateKeys: Array.from(indeterminateKeys)
      }
    }

    async function getDetail(role: Role) {
      const res = await getPermissionByRoleId(role.id)
      const { updatedCheckedKeys, indeterminateKeys } = inferIndeterminateKeys(res?.data?.checkedKeys, res?.data?.menus)
      formValue.value = {
        id: role.id,
        roleName: role.roleName,
        roleKey: role.roleKey,
        enabled: role.enabled,
        remark: role.remark,
        menuIds: updatedCheckedKeys,
        indeterminateKeys: indeterminateKeys,
      }
    }

    onMounted(getTableData)

    const rowKey = (rowData: Role) => rowData.id

    const handleCreateModal = () => {
      formType.value = 'create'
      formVisible.value = true
    }

    async function handleUpdateModal(role: Role) {
      formType.value = 'update'
      await getDetail(role)
      formVisible.value = true
    }

    const onConfirm = async () => {
      const fn = formType.value === 'create' ? createFetch : updateFetch

      await fn({
        params: toRaw(formValue.value),
      })

      message.success(t(`Successfully`))

      formVisible.value = false
      getTableData()
    }

    const modelLoading = computed(() => createLoading.value || updateLoading.value)

    return {
      ...toRefs(tableVariables),
      t,
      formVisible,
      formValue,
      columns: toRef([...columns]),
      roleList,
      loading,
      modelLoading,
      rowKey,
      onConfirm,
      handleCreateModal,
      formType,
    }
  },
  render() {
    return (
      <n-space class={styles.container} vertical justify="center">
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
              data={this.roleList || []}
              pagination={this.pagination}
              loading={this.loading}
              remote
              rowKey={this.rowKey}
            />
          </n-space>
        </n-card>
        <RoleForm
          modelLoading={this.modelLoading}
          v-model:visible={this.formVisible}
          v-model:formValue={this.formValue}
          onConfirm={this.onConfirm}
          formType={this.formType}
        />
      </n-space>
    )
  },
})
