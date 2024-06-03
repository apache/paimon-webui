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

import type {
  AlterTableDTO,
  Catalog,
  CatalogDTO,
  ColumnParams,
  Database,
  DatabaseDTO,
  Datafile,
  Manifest,
  Schema,
  SearchTable,
  Snapshot,
  Table,
  TableDTO,
  TableDetail,
  TableOption,
  TableParams,
  TableQuery,
} from './types'
import httpRequest from '@/api/request'
import type { RequestOptions, ResponseOptions } from '@/api/types'

export * from './types'

// #region catalog-controller

/**
 * # Get all catalog
 */
export function getAllCatalogs() {
  return httpRequest.get<unknown, ResponseOptions<Catalog[]>>('/catalog/list')
}

/**
 * # Create new Catalog
 */
export function createCatalog() {
  return httpRequest.createHooks!<unknown, CatalogDTO>({
    url: '/catalog/create',
    method: 'post',
  })
}

/**
 * # Get database by catalog id
 */
export function getDatabasesByCatalogId(id: number) {
  return httpRequest.get<unknown, ResponseOptions<Database[]>>(`/database/list?catalogId=${id}`)
}

/**
 * # Create new Database
 */
export function createDatabase() {
  return httpRequest.createHooks!<unknown, DatabaseDTO>({
    url: '/database/create',
    method: 'post',
  })
}

/**
 * # Get table by catalog id and database name
 */
export function getTables(params: TableQuery) {
  return httpRequest.post<TableQuery, ResponseOptions<Table[] | SearchTable>>(`/table/list`, params)
}

/**
 * # Create new Table
 */
export function createTable() {
  return httpRequest.createHooks!<unknown, TableDTO>({
    url: '/table/create',
    method: 'post',
  })
}

/**
 * # Alter Table
 */
export function alterTable() {
  return httpRequest.createHooks!<unknown, AlterTableDTO>({
    url: '/table/alter',
    method: 'post',
  })
}

/**
 * # Get options
 */
export function getOptions() {
  return httpRequest.createHooks!<TableOption[], TableParams>({
    url: '/metadata/query/options',
    method: 'post',
  })
}

/**
 * # Create new Option
 */
export function createOption() {
  return httpRequest.createHooks!<unknown, TableDTO>({
    url: '/table/option/add',
    method: 'post',
  })
}

/**
 * # Delete option
 */
export function deleteOption() {
  return httpRequest.createHooks!<TableOption[], TableQuery & { key: string }>({
    url: '/table/option/remove',
    method: 'post',
  })
}

/**
 * # Get columns
 */
export function getColumns(options?: RequestOptions<TableDetail, TableParams>) {
  return httpRequest.createHooks!<TableDetail, TableParams>({
    ...options,
    url: '/table/column/list',
    method: 'get',
  })
}

/**
 * # Create new Columns
 */
export function createColumns() {
  return httpRequest.createHooks!<unknown, TableDTO>({
    url: '/table/column/add',
    method: 'post',
  })
}

/**
 * # Delete columns
 */
export function deleteColumns(query: ColumnParams) {
  const { catalogName, databaseName, name, columnName } = query

  return httpRequest.delete!<unknown, unknown>(`/table/column/drop/${catalogName}/${databaseName}/${name}/${columnName}`)
}

/**
 * # Get schema
 */
export function getSchema() {
  return httpRequest.createHooks!<Schema[], TableParams>({
    url: '/metadata/query/schema',
    method: 'post',
  })
}

/**
 * # Get manifest
 */
export function getManifest() {
  return httpRequest.createHooks!<Manifest[], TableParams>({
    url: '/metadata/query/manifest',
    method: 'post',
  })
}

/**
 * # Get data file
 */
export function getDataFile() {
  return httpRequest.createHooks!<Datafile[], TableParams>({
    url: '/metadata/query/dataFile',
    method: 'post',
  })
}

/**
 * # Get snapshot
 */
export function getSnapshot() {
  return httpRequest.createHooks!<Snapshot[], TableParams>({
    url: '/metadata/query/snapshot',
    method: 'post',
  })
}

// #endregion
