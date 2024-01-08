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

export interface Table {
  catalogId: number
  catalogName: string
  databaseName: string
  name?: string
}

export interface TableQuery {
  catalogId?: number
  databaseName?: string
  name?: string
}

export interface SearchTable {
  [catalogId: string]: {
    [databaseName: string]: Table[]
  }
}

export interface TableParams extends Table {
  tableName: string
}

export interface TableDTO {
  catalogId?: number
  catalogName?: string
  databaseName?: string
  name?: string
  description?: string
  tableColumns?: ColumnDTO[]
  partitionKey?: string[]
  tableOptions?: OptionsDTO
  options?: TableOption[]
}

export interface ColumnDTO {
  field: string
  dataType: DataTypeDTO
  comment: string
  defaultValue: string
  pk: boolean
}

export interface DataTypeDTO {
  type: string
  precision?: number | null
  scale?: number | null
  nullable: boolean
}

export interface OptionsDTO {
  [key: string]: string
}

export interface TableOption {
  k: string;
  v: string;
}
