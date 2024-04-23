/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import * as Field from './index'
import { camelCase, upperFirst, isFunction } from 'lodash'
import type { IFormRules, IJsonItem } from '../types'

const TYPES = [
  'input',
  'radio',
  'switch',
  'select',
  'checkbox',
]

const getField = (
  item: IJsonItem,
  fields: { [field: string]: any },
  rules?: IFormRules
) => {
  const { type = 'input' } = isFunction(item) ? item() : item
  if (!TYPES.includes(type)) return null
  const renderTypeName = `render${upperFirst(camelCase(type))}`
  // @ts-ignore
  return Field[renderTypeName](item, fields)
}

export default getField
