
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

import type { RoleDTO } from "@/api/models/role/types/role";
import { usePermissionStore } from "@/store/permission";
import type { FormRules, TreeOption } from "naive-ui";

const props = {
  visible: {
    type: Boolean as PropType<boolean>,
    default: false
  },
  'onUpdate:visible': [Function, Boolean] as PropType<((value: boolean) => void) | undefined>,

  modelLoading: {
    type: Boolean as PropType<boolean>,
  },
  formValue: {
    type: Object as PropType<RoleDTO>,
    default: () => ({
      roleName: '',
      roleKey: '',
      enabled: true,
      remark: '',
      menuIds: []
    })
  },
  'onUpdate:formValue': [Function, Object] as PropType<((value: RoleDTO) => void) | undefined>,
  onConfirm: Function
}

export default defineComponent({
  name: 'RoleForm',
  props,
  setup(props) {
    const rules = {
      roleName: {
        required: true,
        trigger: ['blur', 'input'],
        message: 'role name required'
      },
      roleKey: {
        required: true,
        trigger: ['blur', 'input'],
        message: 'role key required'
      },
      menuIds: {
        required: true,
        trigger: ['blur'],
        validator: (_: FormRules, value: string) => {
          return new Promise<void>((resolve, reject) => {
            if (!value?.length) {
              reject(Error('menu ids required'))
            } else {
              resolve()
            }
          })
        }
      }
    }

    const { t } = useLocaleHooks()
    const permissionStore = usePermissionStore()
    const { permissionList } = storeToRefs(permissionStore)

    const formRef = ref()
    
    const handleCloseModal = () => {
      props["onUpdate:visible"] && props["onUpdate:visible"](false)
      resetState()
    }

    const renderLabel = ({ option }: { option: TreeOption }) => {
      return t(`system.roleKey.${option.label}`)
    }

    const onUpdateMenuIds = (checkIds:Array<number>) => {
      props.formValue.menuIds = checkIds
    }

    const handleConfirm = async () => {
      await formRef.value.validate()
      props && props.onConfirm && props.onConfirm()
      handleCloseModal()
      resetState()
    }

    const resetState = () => {
      props["onUpdate:formValue"] && props["onUpdate:formValue"]({
        roleName: '',
        roleKey: '',
        enabled: true,
        remark: '',
        menuIds: []
      })
    }

    return {
      ...toRefs(props),
      formRef,
      rules,
      permissionTree: permissionList,
      handleCloseModal,
      renderLabel,
      onUpdateMenuIds,
      handleConfirm,
      t,
    }
  },
  render() {
    return (
        <n-modal v-model:show={this.visible} mask-closable={false}>
          <n-card bordered={false} title={this.t('system.role.create')} style="width: 600px">
            {{
              default: () => (
                  <n-form
                    ref="formRef"
                    label-placement="left"
                    label-width="auto"
                    label-align="left"
                    rules={this.rules}
                    model={this.formValue}
                  >
                    <n-form-item label={this.t('system.role.role_name')} path="roleName">
                      <n-input v-model:value={this.formValue.roleName} />
                    </n-form-item>
                    <n-form-item label={this.t('system.role.role_key')} path="roleKey">
                      <n-input v-model:value={this.formValue.roleKey} />
                    </n-form-item>
                    <n-form-item label={this.t('system.role.enabled')} path="enabled">
                      <n-switch v-model:value={this.formValue.enabled} />
                    </n-form-item>
                    <n-form-item label={this.t('system.role.remark')} path="remark">
                      <n-input 
                        v-model:value={this.formValue.remark} 
                        type="textarea"
                        autosize={{
                          minRows: 3,
                          maxRows: 5
                        }}
                      />
                    </n-form-item>
                    <n-form-item label={this.t('system.role.permission_setting')} path="menuIds">
                      <n-tree
                        key-field='id'
                        default-expand-all
                        block-line
                        cascade
                        renderLabel={this.renderLabel}
                        onUpdate:checkedKeys={this.onUpdateMenuIds}
                        checkedKeys={this.formValue.menuIds}
                        data={this.permissionTree}
                        expand-on-click
                        checkable
                      />
                    </n-form-item>
                  </n-form>
              ),
              action: () => (
                  <n-space justify="end">
                    <n-button onClick={this.handleCloseModal}>{this.t('layout.cancel')}</n-button>
                    <n-button loading={this.modelLoading} type="primary" onClick={this.handleConfirm}>
                      {this.t('layout.confirm')}
                    </n-button>
                  </n-space>
              )
            }}
          </n-card>
        </n-modal>
    );
  }
})
