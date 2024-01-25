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

import tasks from '@/form-lib'
import getElementByJson from '@/components/dynamic-form/get-elements-by-json'
import type {
  IFormItem,
  IJsonItem,
  FormRules
} from '@/components/dynamic-form/types'

export function useTask({
  data,
  formType
}: {
  data: any
  formType: string
}): {
  elementsRef: Ref<IFormItem[]>
  rulesRef: Ref<FormRules>
  model: any
} {
  const jsonRef = ref([]) as Ref<IJsonItem[]>
  const elementsRef = ref([]) as Ref<IFormItem[]>
  const rulesRef = ref({})

  const params = {
    data,
    jsonRef,
    updateElements: () => {
      getElements()
    }
  }

  const task = tasks[formType as keyof typeof tasks]
  const { model, json } = task(params)
  jsonRef.value = json
  const getElements = () => {
    const { rules, elements } = getElementByJson(jsonRef.value, model)
    elementsRef.value = elements
    rulesRef.value = rules
  }

  getElements()

  return { elementsRef, rulesRef, model }
}
