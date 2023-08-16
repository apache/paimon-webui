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

declare namespace API {
    export interface Result<T> {
        code: number;
        msg: string;
        data: T;
    }

    export interface PageData<T> {
        records: T[];
        page: number;
        size: number;
        total: number;
    }

    export type PageResult<T> = Result<PageData<T>>
}

declare namespace Prop {
    // Catalog
    export interface CatalogProp {
        catalogName: string,
        catalogType: string,
        warehouse: string,
        hiveUri: string,
        hiveConfDir: string,
        isDelete: boolean
    }
}