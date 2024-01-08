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

import type { ColumnDTO } from '@/api/models/catalog'

const props = {
  data: {
    type: Array as PropType<ColumnDTO[]>,
    default: () => []
  },
  onUpdateColumns: [Function, Array] as PropType<((value: ColumnDTO[]) => void) | undefined>
}

export default defineComponent({
  name: 'OptionFormContent',
  props,
  setup(props) {
    const { t } = useLocaleHooks()

    const onDelete = (i: number) => {
      const _data = toRaw(props.data)
      if (_data.length <= 1) {
        return
      }
      _data.splice(i, 1)
      props.onUpdateColumns!(_data)
    }

    const columns: DataTableColumns<ColumnDTO> = [
      {
        title: t('metadata.column_field'),
        key: 'field',
        width: 150,
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
              <n-select v-model:value={row.dataType.type} />
            </n-form-item>
          )
        }
      },
      {
        title: t('metadata.column_length'),
        key: 'length',
        width: 150,
        render: (row, index) => {
          return (
            <n-form-item
              show-feedback={false}
              showLabel={false}
              path={`tableColumns[${index}].dataType`}
              rule={{
                required: true,
                trigger: ['input', 'blur']
              }}
            >
              <n-select v-model:value={row.dataType} />
            </n-form-item>
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
              <n-checkbox v-model:value={row.pk} />
            </n-form-item>
          )
        }
      },
      {
        title: t('metadata.column_nullable'),
        key: 'nullable',
        width: 80,
        render: (row, index) => {
          return (
            <n-form-item
              show-feedback={false}
              showLabel={false}
              path={`tableColumns[${index}].dataType.nullable`}
            >
              <n-checkbox v-model:value={row.dataType.nullable} />
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

      onDelete,
      ...toRefs(props)
    }
  },
  render() {
    return <n-data-table columns={this.columns} data={this.data} style={{ marginBottom: '24px' }} />
  }
})
