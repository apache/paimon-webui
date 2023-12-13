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


import { getAllCatalogs, getDatabasesByCatalogId, getTables } from '@/api/models/catalog'
import type { Catalog, Database, Table, TableQuery } from '@/api/models/catalog'

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

      this._catalogLoading = true
      const res = await getAllCatalogs()
      this.catalogs = transformCatalog(res.data)
      this._catalogLoading = false
    },
    async getDatabasesById(id: number): Promise<TreeOption[]> {
      const res = await getDatabasesByCatalogId(id)

      return Promise.resolve(transformDatabase(res.data))
    },
    async getTablesByDataBaseId(params: TableQuery): Promise<TreeOption[]> {
      const res = await getTables(params)

      return Promise.resolve(transformTable(res.data))
    }
  }
})

const transformCatalog = (catalogs: Catalog[]): TreeOption[] => {
  return catalogs.map(catalog => ({
    label: catalog.catalogName,
    type: 'catalog',
    key: catalog.id,
    isLeaf: false,
    prefix: () =>
      h(NIcon, null, {
        default: () => h(FolderOutline)
      }),
  }))
}

const transformDatabase = (databases: Database[]): TreeOption[] => {
  return databases.map(database => ({
    label: database.name,
    type: 'database',
    key: `${database.catalogId} ${database.name}`,
    isLeaf: false,
    prefix: () =>
      h(NIcon, null, {
        default: () => h(FolderOutline)
      }),
  }))
}

const transformTable = (tables: Table[]): TreeOption[] => {
  return tables.map(table => ({
    label: table.name,
    type: 'table',
    key: `${table.catalogId} ${table.databaseName} ${table.name}`,
    isLeaf: true,
    prefix: () =>
      h(NIcon, null, {
        default: () => h(FileTrayOutline)
      }),
  }))
}
