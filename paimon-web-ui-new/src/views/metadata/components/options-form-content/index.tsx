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

import type { TableOption } from '@/api/models/catalog'

const props = {
  options: {
    type: Array as PropType<TableOption[]>,
    default: () => []
  },
  onUpdateOptions: [Function, Array] as PropType<((value: TableOption[]) => void) | undefined>
}

export const newOption: TableOption = {
  k: '',
  v: ''
}

export default defineComponent({
  name: 'OptionFormContent',
  props,
  setup(props) {
    const onDelete = (i: number) => {
      const _options = [...toRaw(props.options)]
      _options.splice(i, 1)
      props.onUpdateOptions!(_options)
    }

    return {
      onDelete,
      ...toRefs(props)
    }
  },
  render() {
    return (
      <n-grid cols={24} x-gap={12}>
        {this.options.map((item, index) => (
          <>
            <n-form-item-gi
              span={11}
              showLabel={false}
              path={`options[${index}].k`}
              rule={{
                required: true,
                message: 'Key is required',
                trigger: ['input', 'blur']
              }}
            >
              <n-input v-model:value={item.k} placeholder="Key" />
            </n-form-item-gi>
            <n-form-item-gi
              span={11}
              showLabel={false}
              path={`options[${index}].v`}
              rule={{
                required: true,
                message: 'Value is required',
                trigger: ['input', 'blur']
              }}
            >
              <n-input v-model:value={item.v} placeholder="Value" />
            </n-form-item-gi>
            <n-gi suffix block span={2} class="suffix">
              <n-button onClick={() => this.onDelete(index)} tertiary type="error">
                Remove
              </n-button>
            </n-gi>
          </>
        ))}
      </n-grid>
    )
  }
})
