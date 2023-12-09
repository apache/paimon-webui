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


import { RouterLink } from "vue-router"
import { UserOutlined,UserSwitchOutlined} from '@vicons/antd'
import { NIcon } from "naive-ui"


export const useData = () => {
  const { t } = useLocaleHooks()
  const state = reactive({
    isShowSided : true,
    sideMenuOptions:[]
  })
  const renderIcon = (icon: any) => {
    return () => h(NIcon, null, { default: () => h(icon) })
  }
  const renderLabel = (label: string, link: string) => {
    return h(
      RouterLink,
      {
        to: { name: link }
      },
      { default: () => label }
    )
  }
  const menuOptions = computed(() => ([
    {
      label: () => renderLabel(t('layout.playground'), 'playground'),
      key: 'playground',
    },
    {
      label: () => renderLabel(t('layout.metadata'), 'metadata'),
      key: 'metadata',
    },
    {
      label: () => renderLabel(t('layout.cdc_ingestion'), 'cdc_ingestion'),
      key: 'cdc_ingestion',
    },
    {
      label: () => renderLabel(t('layout.system'), 'system'),
      key: 'system',
      sideMenuOptions:[{
        label: ()=> renderLabel(t('layout.user'), 'system'),
        key: '/system/user',
        icon: renderIcon(UserOutlined)
      },{
        label: ()=> renderLabel(t('layout.role'), 'system'),
        key: '/system/role',
        icon: renderIcon(UserSwitchOutlined)
      }]
    },
  ]))
  return {
    menuOptions,
    state
  }
}

