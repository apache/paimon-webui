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

import { Folder, FileTray } from '@vicons/ionicons5'

import { useCatalogStore } from '@/store/catalog'

export default defineComponent({
  name: 'MetaDataBreadcrumb',
  setup() {
    const catalogStore = useCatalogStore()
    const catalogStoreRef = storeToRefs(catalogStore)

    return {
      currentTable: catalogStoreRef.currentTable
    }
  },
  render() {
    return (
      <n-breadcrumb>
        <n-breadcrumb-item>
          <n-button text>
            {{
              default: () => {
                return this.currentTable?.catalogName
              },
              icon: () => {
                return <n-icon>
                  <Folder />
                </n-icon>
              }
            }}
          </n-button>
        </n-breadcrumb-item>
        <n-breadcrumb-item>
          <n-button text>
            {{
              default: () => {
                return this.currentTable?.databaseName
              },
              icon: () => {
                return <n-icon>
                  <Folder />
                </n-icon>
              }
            }}
          </n-button>
        </n-breadcrumb-item>
        <n-breadcrumb-item>
          <n-button text>
            {{
              default: () => {
                return this.currentTable?.tableName
              },
              icon: () => {
                return <n-icon>
                  <FileTray />
                </n-icon>
              }
            }}
          </n-button>
        </n-breadcrumb-item>
      </n-breadcrumb>
    );
  }
});
