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
import { FolderOpenOutline } from '@vicons/ionicons5'


import { getAllCatalogApi, type Catalog } from "@/api/models/catalog"

export interface CatalogState {
  _catalogs: Catalog[];
  _catalogLoading: boolean;
}

export const useCatalogStore = defineStore('catalog', {
  state: (): CatalogState => ({
    _catalogs: [],
    _catalogLoading: false
  }),
  persist: true,
  getters: {
    catalogs: (state): TreeOption[] => {
      return transformCatalog(state._catalogs)
    },
    catalogLoading: (state): boolean => {
      return state._catalogLoading
    }
  },
  actions: {
    async getAllCatalog(): Promise<void> {
      const [, useAllCatalog] = getAllCatalogApi()

      this._catalogLoading = true
      const res = await useAllCatalog()
      this._catalogs = res.data
      this._catalogLoading = false
    }
  }
})

const transformCatalog = (catalogs: Catalog[]): TreeOption[] => {
  return catalogs.map(catalog => ({
    label: catalog.catalogName,
    type: catalog.catalogType,
    key: catalog.id,
    isLeaf: false,
    children: [],
    prefix: () =>
      h(NIcon, null, {
        default: () => h(FolderOpenOutline)
      }),
  }))
}
