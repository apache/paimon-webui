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

import type { IJsonItem } from '@/components/dynamic-form/types'

export function useMYSQL(item: any) {
  const { t } = useLocaleHooks()

  const tabType = item.data.tabType
  const data = item.data

  const model = reactive({
    host: data.host || '',
    port: data.port || '',
    username: data.username || '',
    password: data.password || '',
    other_configs: data.other_configs || '',
    database: data.database || '',
    table_name: data.table_name || '',
    type_mapping: data.type_mapping || '',
    metadata_column: data.metadata_column || '',
    computed_column: data.computed_column || '',
  })

  const TypeMappingOptions = [] as any

  return {
    json: [
      {
        type: 'input',
        field: 'host',
        name: t('cdc.host_name_and_ip_address'),
        props: {
          placeholder: '',
        },
        span: computed(() => tabType.value === 'connection_information' ? 24 : 0),
        validate: {
          trigger: ['input', 'blur'],
          required: true,
          message: 'error',
          validator: (validator: any, value: string) => {
            if (!value)
              return new Error('error')
          },
        },
      },
      {
        type: 'input',
        field: 'port',
        name: t('cdc.port'),
        props: {
          placeholder: '',
        },
        span: computed(() => tabType.value === 'connection_information' ? 24 : 0),
      },
      {
        type: 'input',
        field: 'username',
        name: t('cdc.username'),
        props: {
          placeholder: '',
        },
        span: computed(() => tabType.value === 'connection_information' ? 24 : 0),
        validate: {
          trigger: ['input', 'blur'],
          required: true,
          message: 'error',
          validator: (validator: any, value: string) => {
            if (!value)
              return new Error('error')
          },
        },
      },
      {
        type: 'input',
        field: 'password',
        name: t('cdc.password'),
        props: {
          placeholder: '',
        },
        span: computed(() => tabType.value === 'connection_information' ? 24 : 0),
        validate: {
          trigger: ['input', 'blur'],
          required: true,
          message: 'error',
          validator: (validator: any, value: string) => {
            if (!value)
              return new Error('error')
          },
        },
      },
      {
        type: 'input',
        field: 'other_configs',
        name: t('cdc.other_configs'),
        span: computed(() => tabType.value === 'connection_information' ? 24 : 0),
        props: {
          placeholder: '',
          type: 'textarea',
        },
      },
      {
        type: 'input',
        field: 'database',
        name: t('cdc.database'),
        props: {
          placeholder: '',
        },
        span: computed(() => tabType.value === 'synchronization_configuration' ? 24 : 0),
        validate: {
          trigger: ['input', 'blur'],
          required: true,
          message: 'error',
          validator: (validator: any, value: string) => {
            if (!value)
              return new Error('error')
          },
        },
      },
      {
        type: 'input',
        field: 'table_name',
        name: t('cdc.table_name'),
        props: {
          placeholder: '',
        },
        span: computed(() => tabType.value === 'synchronization_configuration' ? 24 : 0),
        validate: {
          trigger: ['input', 'blur'],
          required: true,
          message: 'error',
          validator: (validator: any, value: string) => {
            if (!value)
              return new Error('error')
          },
        },
      },
      {
        type: 'select',
        field: 'type_mapping',
        name: t('cdc.type_mapping'),
        options: TypeMappingOptions,
        span: computed(() => tabType.value === 'synchronization_configuration' ? 24 : 0),
      },
      {
        type: 'input',
        field: 'metadata_column',
        name: t('cdc.metadata_column'),
        span: computed(() => tabType.value === 'synchronization_configuration' ? 24 : 0),
        props: {
          placeholder: '',
        },
      },
      {
        type: 'input',
        field: 'computed_column',
        name: t('cdc.computed_column'),
        span: computed(() => tabType.value === 'synchronization_configuration' ? 24 : 0),
        props: {
          placeholder: '',
          type: 'textarea',
        },
      },
    ] as IJsonItem[],
    model,
  }
}
