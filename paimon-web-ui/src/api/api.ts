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

import http from '@api/http'
import {API_ENDPOINTS} from '@api/endpoints';
import Result = API.Result;
import {CatalogItemList} from "@src/types/Catalog/data";
import {DatabaseItem} from "@src/types/Database/data";
import {TableItem} from "@src/types/Table/data";

export const createFileSystemCatalog = async (catalogProp: Prop.CatalogProp) => {
    try {
        return await http.httpPost<Result<any>, Prop.CatalogProp>(API_ENDPOINTS.CREATE_FILE_SYSTEM_CATALOG, catalogProp);
    } catch (error) {
        console.error('Failed to create catalog:', error);
    }
};

export const createHiveCatalog = async (catalogProp: Prop.CatalogProp) => {
    try {
        return await http.httpPost<Result<any>, Prop.CatalogProp>(API_ENDPOINTS.CREATE_HIVE_CATALOG, catalogProp);
    } catch (error) {
        console.error('Failed to create catalog:', error);
    }
};

export const getAllCatalogs = async () => {
    try {
        return await http.httpGet<Result<CatalogItemList>, null>(API_ENDPOINTS.GET_ALL_CATALOGS)
    } catch (error: any) {
        console.error('Failed to get catalogs:', error);
    }
}

export const removeCatalog = async (catalogName: string) => {
    try {
        return await http.httpDelete<Result<any>, string>(API_ENDPOINTS.REMOVE_CATALOG, catalogName);
    } catch (error) {
        console.error('Failed to remove catalog:', error);
    }
};

export const createDatabase = async (databaseProp: DatabaseItem) => {
    try {
        return await http.httpPost<Result<any>, DatabaseItem>(API_ENDPOINTS.CREATE_DATABASE, databaseProp);
    } catch (error) {
        console.error('Failed to create database:', error);
    }
};

export const getAllDatabases = async () => {
    try {
        return await http.httpGet<Result<DatabaseItem[]>, null>(API_ENDPOINTS.GET_ALL_DATABASES)
    } catch (error: any) {
        console.error('Failed to get database:', error);
    }
}

export const removeDatabase = async (databaseName: string, catalogName: string) => {
    try {
        return await http.httpDelete<Result<any>, { databaseName: string, catalogName: string }>
        (API_ENDPOINTS.REMOVE_DATABASE, { databaseName, catalogName });
    } catch (error) {
        console.error('Failed to delete database:', error);
    }
};

export const createTable = async (tableProp: TableItem) => {
    try {
        return await http.httpPost<Result<any>, TableItem>(API_ENDPOINTS.CREATE_TABLE, tableProp);
    } catch (error) {
        console.error('Failed to create table:', error);
    }
};

export const dropTable = async (catalogName: string, databaseName: string, tableName: string) => {
    try {
        return await http.httpDelete<Result<any>, {catalogName: string, databaseName: string, tableName: string}>
        (API_ENDPOINTS.DROP_TABLE, {catalogName, databaseName, tableName});
    } catch (error) {
        console.error('Failed to drop table:', error);
    }
};
export const renameTable = async (catalogName: string, databaseName: string, fromTableName: string,  toTableName: string) => {
    try {
        const url = `${API_ENDPOINTS.RENAME_TABLE}?catalogName=${encodeURIComponent(catalogName)}&databaseName=${encodeURIComponent(databaseName)}&fromTableName=${encodeURIComponent(fromTableName)}&toTableName=${encodeURIComponent(toTableName)}`;
        return await http.httpPost<Result<any>, null>(url, null);
    } catch (error) {
        console.error('Failed to rename table:', error);
    }
};


export const getAllTables = async () => {
    try {
        return await http.httpGet<Result<TableItem[]>, null>(API_ENDPOINTS.GET_ALL_TABLES)
    } catch (error: any) {
        console.error('Failed to get tables:', error);
    }
}

export const addColumn = async (tableProp: TableItem) => {
    try {
        return await http.httpPost<Result<any>, TableItem>(API_ENDPOINTS.ADD_COLUMN, tableProp);
    } catch (error) {
        console.error('Failed to add column:', error);
    }
};

export const dropColumn = async (catalogName: string, databaseName: string, tableName: string, columnName: string) => {
    try {
        return await http.httpDelete<Result<any>, {catalogName: string, databaseName: string, tableName: string, columnName: string}>
        (API_ENDPOINTS.DROP_COLUMN, {catalogName, databaseName, tableName, columnName});
    } catch (error) {
        console.error('Failed to drop column:', error);
    }
};

export const renameColumn = async (catalogName: string, databaseName: string, tableName: string, fromColumnName: string, toColumnName: string) => {
    try {
        const url = `${API_ENDPOINTS.RENAME_COLUMN}?catalogName=${encodeURIComponent(catalogName)}&databaseName=${encodeURIComponent(databaseName)}&tableName=${encodeURIComponent(tableName)}&fromColumnName=${encodeURIComponent(fromColumnName)}&toColumnName=${encodeURIComponent(toColumnName)}`;
        return await http.httpPost<Result<any>, null>(url, null);
    } catch (error) {
        console.error('Failed to rename column:', error);
    }
};

export const updateColumnType = async (tableProp: TableItem) => {
    try {
        return await http.httpPost<Result<any>, TableItem>(API_ENDPOINTS.UPDATE_COLUMN_TYPE, tableProp);
    } catch (error) {
        console.error('Failed to update column type:', error);
    }
};

export const updateColumnComment = async (tableProp: TableItem) => {
    try {
        return await http.httpPost<Result<any>, TableItem>(API_ENDPOINTS.UPDATE_COLUMN_COMMENT, tableProp);
    } catch (error) {
        console.error('Failed to update column comment:', error);
    }
};

export const addOption = async (tableProp: TableItem) => {
    try {
        return await http.httpPost<Result<any>, TableItem>(API_ENDPOINTS.ADD_OPTION, tableProp);
    } catch (error) {
        console.error('Failed to add option:', error);
    }
};

export const removeOption = async (tableProp: TableItem) => {
    try {
        return await http.httpPost<Result<any>, TableItem>(API_ENDPOINTS.REMOVE_OPTION, tableProp);
    } catch (error) {
        console.error('Failed to remove option:', error);
    }
};

const Api = {
    createFileSystemCatalog,
    createHiveCatalog,
    getAllCatalogs,
    removeCatalog,
    createDatabase,
    getAllDatabases,
    createTable,
    dropTable,
    renameTable,
    getAllTables,
    removeDatabase,
    addColumn,
    dropColumn,
    renameColumn,
    updateColumnType,
    updateColumnComment,
    addOption,
    removeOption
}

export default Api;

