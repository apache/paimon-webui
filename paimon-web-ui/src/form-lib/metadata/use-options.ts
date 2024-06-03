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
import type { TableOption } from '@/api/models/catalog'

export function useOptions({ data }: { data?: TableOption }) {
  const model = reactive(data || { key: '', value: '' })

  return {
    json: [
      {
        type: 'input',
        field: 'key',
        name: 'Key',
        props: {
          placeholder: 'Key',
          disabled: true,
        },
        validate: {
          trigger: ['input', 'blur'],
          required: true,
          message: 'error',
          validator: (validator: any, value: string) => {
            if (!value)
              return new Error('Key is required')
          },
        },
      },
      {
        type: 'input',
        field: 'value',
        name: 'Value',
        props: {
          placeholder: 'Value',
        },
        validate: {
          trigger: ['input', 'blur'],
          required: true,
          message: 'error',
          validator: (validator: any, value: string) => {
            if (!value)
              return new Error('Value is required')
          },
        },
      },
    ] as IJsonItem[],
    model,
  }
}
