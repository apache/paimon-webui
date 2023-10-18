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

import type { GlobalThemeOverrides } from 'naive-ui'

const light: GlobalThemeOverrides = {
  common: {
    bodyColor: '#F4F5F6',

    primaryColor: '#2475FE',
    primaryColorHover: '#3a7bfa',
    primaryColorPressed: '#1a6efb',
    primaryColorSuppl: '#0060fa',

    infoColor: '#A6A6A6',
    successColor: '#00B69B',
    warningColor: '#FCBE2D',
    errorColor: '#EC4C4D'
  },
  Card: {
    color: '#FAFAFA',
    borderRadius: '8px'
  }
}

export default light
