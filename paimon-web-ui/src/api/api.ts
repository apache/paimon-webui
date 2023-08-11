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
import { API_ENDPOINTS } from '@api/endpoints';

export const createFileSystemCatalog = async (catalogProp: Prop.FileSystemCatalogProp) => {
    try {
        const response: API.Result<any> =
            await http.httpPost<API.Result<any>, Prop.FileSystemCatalogProp>(API_ENDPOINTS.CREATE_FILE_SYSTEM_CATALOG, catalogProp);
        console.log(response)
        if (response.code === 200) {
            console.log('Catalog was successfully created');
            return response.data;
        } else {
            console.log('There was a problem creating the catalog:', response.msg);
        }
    } catch (error) {
        console.error('Failed to create catalog:', error);
    }
};

const Api = {
    createFileSystemCatalog
}

export default Api;

