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

import httpRequest from '@/api/request'
import type {
  Catalog,
  Database,
  Table,
  Schema,
  Manifest,
  Snapshot,
  Datafile,
  TableQuery,
  CatalogDTO,
  DatabaseDTO,
  TableDTO
} from './types'
import type { ResponseOptions } from '@/api/types'

export * from './types'

// #region catalog-controller

/**
 * # Get all catalog
 */
export const getAllCatalogs = () => {
  return httpRequest.get<unknown, ResponseOptions<Catalog[]>>('/catalog/getAllCatalogs')
}

/**
 * # Create new Catalog
 */
export const createCatalog = () => {
  return httpRequest.createHooks!<unknown, CatalogDTO>({
    url: '/catalog/create',
    method: 'post'
  })
}

/**
 * # Get database by catalog id
 */
export const getDatabasesByCatalogId = (id: number) => {
  return httpRequest.get<unknown, ResponseOptions<Database[]>>(`/database/list?catalogId=${id}`)
}

/**
 * # Create new Database
 */
export const createDatabase = () => {
  return httpRequest.createHooks!<unknown, DatabaseDTO>({
    url: '/database/create',
    method: 'post'
  })
}

/**
 * # Get table by catalog id and database name
 */
export const getTables = (params: TableQuery) => {
  return httpRequest.post<TableQuery, ResponseOptions<Table[]>>(`/table/list`, params)
}


/**
 * # Create new Table
 */
export const createTable = () => {
  return httpRequest.createHooks!<unknown, TableDTO>({
    url: '/table/create',
    method: 'post'
  })
}


/**
 * # Get schema
 */
export const getSchema = () => {
  return httpRequest.createHooks!<Schema[]>({
    url: '/metadata/query/schema',
    method: 'post'
  })
}

/**
 * # Get manifest
 */
export const getManifest = () => {
  return httpRequest.createHooks!<Manifest[]>({
    url: '/metadata/query/manifest',
    method: 'post'
  })
}

/**
 * # Get data file
 */
export const getDataFile = () => {
  return httpRequest.createHooks!<Datafile[]>({
    url: '/metadata/query/dataFile',
    method: 'post'
  })
}

/**
 * # Get snapshot
 */
export const getSnapshot = () => {
  return httpRequest.createHooks!<Snapshot[]>({
    url: '/metadata/query/snapshot',
    method: 'post'
  })
}

// #endregion
