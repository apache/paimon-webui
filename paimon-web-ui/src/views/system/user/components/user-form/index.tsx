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

import type { FormItemRule, TreeOption } from 'naive-ui'

import type { UserDTO } from '@/api/models/user/types'
import { listRoles } from '@/api/models/role'

const props = {
  'visible': {
    type: Boolean as PropType<boolean>,
    default: false,
  },
  'onUpdate:visible': [Function, Boolean] as PropType<((value: boolean) => void) | undefined>,

  'modelLoading': {
    type: Boolean as PropType<boolean>,
  },
  'formType': {
    type: String as PropType<'create' | 'update'>,
  },

  'formValue': {
    type: Object as PropType<UserDTO>,
    default: () => ({
      roleName: '',
      roleKey: '',
      enabled: true,
      remark: '',
      menuIds: [],
    }),
  },
  'onUpdate:formValue': [Function, Object] as PropType<((value: UserDTO) => void) | undefined>,
  'onConfirm': Function,
}

export default defineComponent({
  name: 'UserForm',
  props,
  setup(props) {
    const rules = {
      username: {
        required: true,
        trigger: ['blur', 'input'],
        message: 'username required',
      },
      password: {
        required: true,
        trigger: ['blur', 'input'],
        message: 'password required',
      },
      roleIds: {
        required: true,
        type: 'number',
        trigger: ['blur', 'change'],
        message: 'roleIds required',
      },
      mobile: {
        required: true,
        trigger: ['input'],
        validator: (rule: FormItemRule, value: string) => {
          return /^1+[3,8]+\d{9}$/.test(value)
        },
      },
    }

    const { t } = useLocaleHooks()
    const [roleList, useRoleList] = listRoles()

    const formRef = ref()

    watch(
      () => props.visible,
      (visible) => {
        if (visible)
          useRoleList()
      },
    )

    const handleCloseModal = () => {
      props['onUpdate:visible'] && props['onUpdate:visible'](false)
      resetState()
    }

    async function handleConfirm() {
      await formRef.value.validate()
      props && props.onConfirm && props.onConfirm()
      handleCloseModal()
      resetState()
    }

    function resetState() {
      props['onUpdate:formValue'] && props['onUpdate:formValue']({
        username: '',
        nickname: '',
        password: '',
        enabled: true,
        mobile: '',
        email: '',
        roleIds: undefined,
      })
    }

    return {
      ...toRefs(props),
      formRef,
      rules,
      roleList,
      handleCloseModal,
      handleConfirm,
      t,
    }
  },
  render() {
    return (
      <n-modal v-model:show={this.visible} mask-closable={false}>
        <n-card bordered={false} title={this.t(this.formType === 'create' ? 'system.role.create' : 'system.role.update')} style="width: 600px">
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
                <n-form-item label={this.t('system.user.username')} path="username">
                  <n-input v-model:value={this.formValue.username} />
                </n-form-item>
                <n-form-item label={this.t('system.user.nickname')} path="nickname">
                  <n-input v-model:value={this.formValue.nickname} />
                </n-form-item>
                {
                  this.formType === 'create' && (
                    <n-form-item label={this.t('system.user.password')} path="password">
                      <n-input type="password" show-password-on="click" v-model:value={this.formValue.password} />
                    </n-form-item>
                  )
                }
                <n-form-item label={this.t('system.user.mobile')} path="mobile">
                  <n-input v-model:value={this.formValue.mobile} />
                </n-form-item>
                <n-form-item label={this.t('system.user.email')} path="email">
                  <n-input v-model:value={this.formValue.email} />
                </n-form-item>
                <n-form-item label={this.t('system.user.enabled')} path="enabled">
                  <n-switch v-model:value={this.formValue.enabled} />
                </n-form-item>
                <n-form-item label={this.t('system.user.roleIds')} path="roleIds">
                  <n-select
                    v-model:value={this.formValue.roleIds}
                    options={this.roleList || []}
                    value-field="id"
                    label-field="roleName"
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
            ),
          }}
        </n-card>
      </n-modal>
    )
  },
})
