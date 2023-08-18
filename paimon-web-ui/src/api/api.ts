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

export const createDatabase = async (databaseProp: Prop.DatabaseProp) => {
    try {
        return await http.httpPost<Result<any>, Prop.DatabaseProp>(API_ENDPOINTS.CREATE_DATABASE, databaseProp);
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

const Api = {
    createFileSystemCatalog,
    createHiveCatalog,
    getAllCatalogs,
    createDatabase,
    getAllDatabases
}

export default Api;

