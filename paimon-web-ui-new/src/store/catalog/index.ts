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

import { NIcon, type TreeOption } from 'naive-ui'
import { FileTrayOutline, FolderOutline } from '@vicons/ionicons5'


import { getAllCatalogs, type Catalog } from '@/api/models/catalog'

export interface CatalogState {
  catalogs: TreeOption[];
  _catalogLoading: boolean;
}

export const useCatalogStore = defineStore('catalog', {
  state: (): CatalogState => ({
    catalogs: [],
    _catalogLoading: false
  }),
  persist: true,
  getters: {
    catalogLoading: (state): boolean => {
      return state._catalogLoading
    }
  },
  actions: {
    async getAllCatalogs(): Promise<void> {
      const [, useAllCatalog] = getAllCatalogs()

      this._catalogLoading = true
     const res = await useAllCatalog() 
      this.catalogs = transformCatalog(res.data)
      this._catalogLoading = false
    },
    getDatabasesByCatalogId(id: number): Promise<any> {
      // TODO: fetch database list by catalog id
      // Waiting for the deployment of the back end interface

      // const [, useDatabaseByCatalogId] = getDatabasesByCatalogId(id)
      // return useDatabaseByCatalogId()
      return new Promise((resolve) => {
        setTimeout(() => {
          resolve([
            {
              label: 'database',
              type: 'database',
              key: ++id,
              isLeaf: false,
              prefix: () =>
                h(NIcon, null, {
                  default: () => h(FolderOutline)
                }),
            }
          ])
        }, 1000)
      })
    },
    getTablesByDataBaseId(id: number): Promise<any> {
      // TODO: fetch table list by catalog id and database name
      // Waiting for the deployment of the back end interface

      // const [, useDatabaseByCatalogId] = getDatabasesByCatalogId(id)
      // return useDatabaseByCatalogId()
      return new Promise((resolve) => {
        setTimeout(() => {
          resolve([
            {
              label: 'table',
              type: 'table',
              key: ++id,
              isLeaf: true,
              prefix: () =>
                h(NIcon, null, {
                  default: () => h(FileTrayOutline)
                }),
            }
          ])
        }, 1000)
      })
    }
  }
})

const transformCatalog = (catalogs: Catalog[]): TreeOption[] => {
  return catalogs.map(catalog => ({
    label: catalog.name || 'paimon',
    type: catalog.type || 'catalog',
    key: catalog.id,
    isLeaf: false,
    prefix: () =>
      h(NIcon, null, {
        default: () => h(FolderOutline)
      }),
  }))
}
