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

export const API_ENDPOINTS = {

    // auth && login
    GET_LDAP_ENABLE: '/ldap/enable',
    LOGIN: '/login',

    // catalog
    CREATE_FILE_SYSTEM_CATALOG: '/catalog/createFilesystemCatalog',
    CREATE_HIVE_CATALOG: '/catalog/createHiveCatalog',
    GET_ALL_CATALOGS: '/catalog/getAllCatalogs',
    REMOVE_CATALOG: '/catalog/removeCatalog',

    // database
    CREATE_DATABASE: '/database/createDatabase',
    GET_ALL_DATABASES: '/database/getAllDatabases',
    REMOVE_DATABASE: '/database/delete',

    // table
    CREATE_TABLE: '/table/createTable',
    DROP_TABLE: '/table/dropTable',
    RENAME_TABLE: '/table/renameTable',
    GET_ALL_TABLES: '/table/getAllTables',
    ADD_COLUMN: '/table/addColumn',
    DROP_COLUMN: '/table/dropColumn',
    RENAME_COLUMN: '/table/renameColumn',
    UPDATE_COLUMN_TYPE: '/table/updateColumnType',
    UPDATE_COLUMN_COMMENT: '/table/updateColumnComment',
    ADD_OPTION: '/table/addOption',
    REMOVE_OPTION: '/table/removeOption',

};