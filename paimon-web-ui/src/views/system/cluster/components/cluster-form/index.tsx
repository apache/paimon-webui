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

import type { ClusterDTO } from '@/api/models/cluster/types'

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
    type: Object as PropType<ClusterDTO>,
    default: () => ({
      clusterName: '',
      host: '',
      port: 0,
      enabled: true,
      type: '',
      deploymentMode: '',
    }),
  },
  'onUpdate:formValue': [Function, Object] as PropType<((value: ClusterDTO) => void) | undefined>,
  'onConfirm': Function,
}

export default defineComponent({
  name: 'UserForm',
  props,
  setup(props) {
    interface DeploymentOption {
      label: string
      value: string
    }

    const typeOptions = [
      { label: 'Flink', value: 'Flink' },
      { label: 'Spark', value: 'Spark' },
    ]

    const deploymentModeFlink = [
      { label: 'Yarn Session', value: 'yarn-session' },
      { label: 'Flink SQL Gateway', value: 'flink-sql-gateway' },
    ]

    const deploymentModeSpark: DeploymentOption[] = []

    const deploymentModeOptions = computed(() => {
      if (props.formValue.type === 'Flink') {
        return deploymentModeFlink
      }
      else if (props.formValue.type === 'Spark') {
        return deploymentModeSpark
      }
      return []
    })

    watch(() => props.formValue.type, (newType) => {
      if (newType === 'Spark' && deploymentModeSpark.length === 0) {
        props['onUpdate:formValue']?.({ ...props.formValue, deploymentMode: '' })
      }
      else if (newType === 'Flink') {
        props['onUpdate:formValue']?.({ ...props.formValue, deploymentMode: deploymentModeFlink[0].value })
      }
    })

    const rules = {
      clusterName: {
        required: true,
        trigger: ['blur', 'input'],
        message: 'clusterName required',
      },
      host: {
        required: true,
        trigger: ['blur', 'input'],
        message: 'host required',
      },
      port: {
        required: true,
        type: 'number',
        trigger: ['blur', 'change'],
        message: 'port required',
      },
      type: {
        required: true,
        trigger: ['blur', 'input'],
        message: 'type required',
      },
      deploymentMode: {
        required: true,
        trigger: ['blur', 'input'],
        message: 'deploymentMode required',
      },
    }

    const { t } = useLocaleHooks()

    const formRef = ref()

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
        clusterName: '',
        host: '',
        port: 0,
        enabled: true,
        type: '',
        deploymentMode: '',
      })
    }

    return {
      ...toRefs(props),
      typeOptions,
      deploymentModeOptions,
      formRef,
      rules,
      handleCloseModal,
      handleConfirm,
      t,
    }
  },
  render() {
    return (
      <n-modal v-model:show={this.visible} mask-closable={false}>
        <n-card bordered={false} title={this.t(this.formType === 'create' ? 'system.cluster.create' : 'system.cluster.update')} style="width: 600px">
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
                <n-form-item label={this.t('system.cluster.cluster_name')} path="clusterName">
                  <n-input v-model:value={this.formValue.clusterName} />
                </n-form-item>
                <n-form-item label={this.t('system.cluster.cluster_host')} path="host">
                  <n-input v-model:value={this.formValue.host} />
                </n-form-item>
                <n-form-item label={this.t('system.cluster.cluster_port')} path="port">
                  <n-input-number min={0} showButton={false} style="width: 100%" v-model:value={this.formValue.port} />
                </n-form-item>
                <n-form-item label={this.t('system.user.enabled')} path="enabled">
                  <n-switch v-model:value={this.formValue.enabled} />
                </n-form-item>
                <n-form-item label={this.t('system.cluster.cluster_type')} path="type">
                  <n-select
                    v-model:value={this.formValue.type}
                    options={this.typeOptions}
                  />
                </n-form-item>
                <n-form-item label={this.t('system.cluster.deployment_type')} path="deploymentMode">
                  <n-select
                    v-model:value={this.formValue.deploymentMode}
                    options={this.deploymentModeOptions}
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
