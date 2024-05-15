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
import type { Role, RoleMenu } from "@/api/models/role/types/role"

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
    const { t } = useLocaleHooks()

    const rolePermissionDetail = ref<RoleMenu[]>([])
    const loading = ref(false)

    onMounted(initDetail)

    async function initDetail() {
      loading.value = true
      const res = await getPermissionByRoleId(props.roleRecord.id)
      rolePermissionDetail.value = res?.data?.menus
      loading.value = false
    }

    return {
      loading,
      rolePermissionDetail,
      t,
    }
  },
  render() {
    return <n-spin show={this.loading}>
      <n-list hoverable clickable>
        {
          this.rolePermissionDetail?.map((item, i) => (
            <n-list-item key={item.id}>
              <n-thing title={this.t(`system.roleKey.${item.label}`)} content-style="margin-top: 10px;">
                {
                  item.children?.map((child) => (
                    <n-thing description={this.t(`system.roleKey.${child.label}`)} style="margin-top: 20px;">
                      <n-space>
                        {
                          child.children?.map((buttonPermission) => (
                            <n-tag key={child.id} type="info">{this.t(`system.roleKey.${buttonPermission.label}`)}</n-tag>
                          ))
                        }
                      </n-space>
                    </n-thing>
                  ))
                }
              </n-thing>
            </n-list-item>
          ))
        }
      </n-list>
    </n-spin>
  }
})
