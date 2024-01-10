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

import type { FormInst, FormItemRule, FormProps, FormRules, GridProps } from "naive-ui"

type ComponentType =
  | 'input'
  | 'radio'
  | 'switch'
  | 'select'
  | 'checkbox'


interface IOption {
  [key: string]: any
}

interface IFormItemRule extends Omit<FormItemRule, 'required'> {
  required?: boolean | Ref<boolean>
}

interface IJsonItemParams {
  type: ComponentType
  field: string
  name?: string
  value?: any
  props?: any
  options?: IOption[] | Ref<IOption[]>
  span?: number | Ref<number>
  children?: IJsonItem[]
  validate?: IFormItemRule
  slots?: object
}

type IJsonItem = IJsonItemParams | IJsonItemFn

type IJsonItemFn = (i?: number) => IJsonItemParams

interface IFormItem {
  showLabel?: boolean
  path: string
  label?: string
  widget: any
  span?: number | Ref<number>
  class?: string
}

type IFormRules =
  | {
      [path: string]: IFormItemRule | IFormItemRule[]
    }
  | FormRules

interface IMeta extends Omit<FormProps, 'model' | 'rules'> {
  elements?: IFormItem[]
  model: object
  rules: IFormRules
}

interface IFormInst extends FormInst {
  setValues: (initialValues: { [field: string]: any }) => void
  resetValues: (initialValues: { [field: string]: any }) => void
  getValues: () => { [field: string]: any }
  restoreValidation: () => void
  validate: (...args: []) => Promise<any>
  formatValidate: (validate?: IFormItemRule | FormRules) => IFormItemRule
}

export type {
  IJsonItem,
  IJsonItemParams,
  IJsonItemFn,
  IOption,
  ComponentType,
  IFormItem,
  IFormRules,
  IFormItemRule,
  IMeta,
  GridProps,
  FormRules,
  IFormInst
}
