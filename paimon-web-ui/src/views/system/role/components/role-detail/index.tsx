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

import { getPermissionByRoleId } from "@/api/models/role"
import type { Role } from "@/api/models/role/types/role"
import { async } from "@antv/x6/lib/registry/marker/async"

export default defineComponent({
  name: 'RoleDetail',
  props: {
    roleRecord: {
      type: Object as PropType<Role>,
      default: () => ({}),
      require: true
    }
  },
  setup(props) {
    const detail = ref()
    const loading = ref(false)
    onMounted(initDetail)

    async function initDetail() {
      loading.value = true
      const res = await getPermissionByRoleId(props.roleRecord.id)
      console.log('res', res);

      loading.value = false
    }

    return ({})
  },
  render() {
    return <div>
      <n-descriptions label-placement="top" title="描述">
        <n-descriptions-item>
          {{
            default: () => '苹果',
            label: () => '水果'
          }}
        </n-descriptions-item>
        <n-descriptions-item label="早午餐">
          苹果
        </n-descriptions-item>
      </n-descriptions>
    </div>
  }
})
