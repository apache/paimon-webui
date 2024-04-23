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

import type { DataTableColumns } from 'naive-ui'
import { VueDraggable } from 'vue-draggable-plus'
import { UnorderedListOutlined } from '@vicons/antd'

import type { ColumnDTO } from '@/api/models/catalog'

import { hasLength, hasEndLength, dataTypeOptions } from './constant'

const props = {
  data: {
    type: Array as PropType<ColumnDTO[]>,
    default: () => []
  },
  'onUpdate:data': [Function, Array] as PropType<((value: ColumnDTO[]) => void) | undefined>,
}

export const newField: ColumnDTO = {
  field: '',
  dataType: {
    nullable: true,
    type: undefined
  },
  comment: '',
  defaultValue: '',
  pk: false
}

export default defineComponent({
  name: 'ColumnFormContent',
  props,
  setup(props) {
    const { t } = useLocaleHooks()

    const onDelete = (i: number) => {
      const _data = toRaw(props.data)
      if (_data.length <= 1) {
        return
      }
      props.data.splice(i, 1)
    }

    const columns: DataTableColumns<ColumnDTO> = [
      {
        title: '#',
        key: 'field',
        render: () => {
          return (
            <n-icon class="drag-handle">
              <UnorderedListOutlined />
            </n-icon>
          )
        }
      },
      {
        title: t('metadata.column_field'),
        key: 'field',
        width: 120,
        render: (row, index) => {
          return (
            <n-form-item
              show-feedback={false}
              showLabel={false}
              path={`tableColumns[${index}].field`}
              rule={{
                required: true,
                message: 'Field is required',
                trigger: ['input', 'blur']
              }}
            >
              <n-input v-model:value={row.field} />
            </n-form-item>
          )
        }
      },
      {
        title: t('metadata.column_type'),
        key: 'dataType',
        width: 150,
        render: (row, index) => {
          return (
            <n-form-item
              show-feedback={false}
              showLabel={false}
              path={`tableColumns[${index}].dataType.type`}
              rule={{
                required: true,
                message: 'DateType is required',
                trigger: ['input', 'blur']
              }}
            >
              <n-select
                placeholder={t('metadata.column_type')}
                v-model:value={row.dataType.type}
                options={dataTypeOptions}
              />
            </n-form-item>
          )
        }
      },
      {
        title: t('metadata.column_length'),
        key: 'length',
        width: 190,
        render: (row, index) => {
          const hasStartLength = hasLength.includes(row.dataType.type || '')
          const hasScaleLength = hasEndLength.includes(row.dataType.type || '')
          return (
            <n-space wrap={false}>
              {hasStartLength && (
                <n-form-item
                  show-feedback={false}
                  showLabel={false}
                  path={`tableColumns[${index}].dataType.precision`}
                  rule={{
                    type: 'number',
                    required: true,
                    trigger: ['input', 'blur']
                  }}
                >
                  <n-input-number v-model:value={row.dataType.precision} show-button={false} />
                </n-form-item>
              )}
              {hasScaleLength && (
                <n-form-item
                  show-feedback={false}
                  showLabel={false}
                  path={`tableColumns[${index}].dataType.scale`}
                  rule={{
                    type: 'number',
                    required: true,
                    trigger: ['input', 'blur']
                  }}
                >
                  <n-input-number v-model:value={row.dataType.scale} show-button={false} />
                </n-form-item>
              )}
            </n-space>
          )
        }
      },
      {
        title: t('metadata.column_pk'),
        key: 'pk',
        width: 60,
        render: (row, index) => {
          return (
            <n-form-item show-feedback={false} showLabel={false} path={`tableColumns[${index}].pk`}>
              <n-checkbox v-model:checked={row.pk} />
            </n-form-item>
          )
        }
      },
      {
        title: t('metadata.column_nullable'),
        key: 'dataType.nullable',
        width: 80,
        render: (row, index) => {
          return (
            <n-form-item
              show-feedback={false}
              showLabel={false}
              path={`tableColumns[${index}].dataType.nullable`}
            >
              <n-checkbox v-model:checked={row.dataType.nullable} />
            </n-form-item>
          )
        }
      },
      {
        title: t('metadata.column_default'),
        key: 'defaultValue',
        width: 150,
        render: (row, index) => {
          return (
            <n-form-item
              show-feedback={false}
              showLabel={false}
              path={`tableColumns[${index}].defaultValue`}
            >
              <n-input v-model:value={row.defaultValue} />
            </n-form-item>
          )
        }
      },
      {
        title: t('metadata.column_comment'),
        key: 'comment',
        width: 150,
        render: (row, index) => {
          return (
            <n-form-item
              show-feedback={false}
              showLabel={false}
              path={`tableColumns[${index}].defaultValue`}
            >
              <n-input v-model:value={row.comment} />
            </n-form-item>
          )
        }
      },
      {
        title: t('metadata.column_action'),
        key: 'Operate',
        render: (_, index) => {
          return (
            <n-button onClick={() => onDelete(index)} tertiary type="error">
              Remove
            </n-button>
          )
        }
      }
    ]

    return {
      columns,

      ...toRefs(props)
    }
  },
  render() {
    return (
      <VueDraggable v-model:value={this.data} animation={150} handle='.drag-handle' target='.n-data-table-tbody'>
        <n-data-table columns={this.columns} data={this.data} style={{ marginBottom: '24px' }} />
      </VueDraggable>
    )
  }
})
