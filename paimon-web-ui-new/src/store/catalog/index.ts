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
import { FileTrayOutline } from '@vicons/ionicons5'


import { getAllCatalogs, getDatabasesByCatalogId, getTables } from '@/api/models/catalog'
import type { Catalog, Database, SearchTable, Table, TableParams, TableQuery } from '@/api/models/catalog'

export interface CatalogState {
  catalogs: TreeOption[];
  _catalogLoading: boolean;
  _currentTable?: TableParams
}

export const useCatalogStore = defineStore('catalog', {
  state: (): CatalogState => ({
    catalogs: [],
    _catalogLoading: false,
    _currentTable: undefined
  }),
  persist: true,
  getters: {
    catalogLoading: (state): boolean => {
      return state._catalogLoading
    },
    currentTable: (state): TableParams | undefined => {
      return state._currentTable
    }
  },
  actions: {
    async getAllCatalogs(reload?: boolean): Promise<TreeOption[] | void> {
      if (!reload && this.catalogs.length !== 0) {
        return Promise.resolve(this.catalogs)
      } else {
        this._catalogLoading = true
        const res = await getAllCatalogs()
        this.catalogs = transformCatalog(res.data)
        this._catalogLoading = false
      }
    },
    async getDatabasesById(id: number): Promise<TreeOption[]> {
      const res = await getDatabasesByCatalogId(id)

      return Promise.resolve(transformDatabase(res.data))
    },
    async getTablesByDataBaseId(params: TableQuery): Promise<TreeOption[] | void> {
      const res = await getTables(params)

      if (params?.name) {
        this._catalogLoading = true
        this.catalogs = await transformSearchTable(res.data as SearchTable)
        this._catalogLoading = false
        return Promise.resolve()
      }

      return Promise.resolve(transformTable(res.data as Table[]))
    },
    async setCurrentTable(table: TableParams) {
      this._currentTable = table
    },
    async resetCurrentTable() {
      this._currentTable = undefined
    }
  }
})

const transformCatalog = (catalogs: Catalog[]): TreeOption[] => {
  return catalogs.map(catalog => ({
    label: catalog.catalogName,
    type: 'catalog',
    key: catalog.id,
    isLeaf: false,
  }))
}

const transformDatabase = (databases: Database[]): TreeOption[] => {
  return databases.map(database => ({
    label: database.name,
    type: 'database',
    key: `${database.catalogId} ${database.catalogName} ${database.name}`,
    isLeaf: false,
  }))
}

const transformTable = (tables: Table[]): TreeOption[] => {
  return tables.map(table => ({
    label: table.name,
    type: 'table',
    key: `${JSON.stringify(table)}`,
    isLeaf: true,
    prefix: () =>
      h(NIcon, null, {
        default: () => h(FileTrayOutline)
      }),
  }))
}

const transformSearchTable = (searchResult: SearchTable): TreeOption[] => {
  const result: TreeOption[] = []
  const effect: string[] = []

  const catalogIds = Object.keys(searchResult)
  catalogIds.forEach((catalogId: string) => {
    const databaseNames = Object.keys(searchResult[catalogId])
    databaseNames.forEach((databaseName: string) => {
      const tables = searchResult[catalogId][databaseName]

      const catalog = result.find(item => item.key === catalogId) || {
        label: tables[0].catalogName,
        type: 'catalog',
        key: catalogId,
        isLeaf: false,
        children: []
      }

      const database = catalog.children?.find(item => item.key === `${catalogId} ${databaseName}`) || {
        label: databaseName,
        type: 'database',
        key: `${catalogId} ${databaseName}`,
        isLeaf: false
      }

      database.children = transformTable(tables)
      catalog.children?.push(database)

      if (!effect.includes(catalogId)) {
        effect.push(catalogId)
        result.push(catalog)
      }
    })
  })

  return result
}
