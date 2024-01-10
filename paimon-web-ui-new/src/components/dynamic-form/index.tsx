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

import type { FormRules, GridProps, IFormInst, IMeta } from "./types"
import { useFormHooks } from "./use-form"

const props = {
  loading: {
    type: Boolean as PropType<boolean>,
    default: false
  },
  gridProps: {
    type: Object as PropType<GridProps>
  },
  meta: {
    type: Object as PropType<IMeta>,
    default: {},
    required: true
  },
}

export default defineComponent({
  name: 'DynamicForm',
  props,
  setup(_, { expose }) {
    const { state, ...rest } = useFormHooks()
    expose({
      ...rest
    }  as IFormInst)
    return { ...toRefs(state) }
  },
  render(props: { meta: IMeta; gridProps?: GridProps; loading?: boolean }) {
    const { loading, gridProps, meta } = props
    const { elements = [], ...restFormProps } = meta
    return (
      <n-spin show={loading}>
        <n-form {...restFormProps} rules={meta.rules as FormRules} ref='formRef'>
          <n-grid {...gridProps}>
            {elements.map((element) => {
              const { span = 24, path, widget, ...formItemProps } = element
              return (
                <n-form-item-gi
                  {...formItemProps}
                  span={unref(span) === void 0 ? 24 : unref(span)}
                  path={path}
                  key={path || String(Date.now() + Math.random())}
                >
                  {h(widget)}
                </n-form-item-gi>
              )
            })}
          </n-grid>
        </n-form>
      </n-spin>
    )
  }
})
