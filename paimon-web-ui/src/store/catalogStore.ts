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

import {create} from 'zustand';
import {persist} from "zustand/middleware";
import {CatalogItemList} from "@src/types/Catalog/data";
import Api from "@api/api.ts";
import {Toast} from "@douyinfe/semi-ui";
import i18n from 'i18next';

type Store = {
    catalogItemList: CatalogItemList;
    createFileSystemCatalog: (catalogProp: Prop.CatalogProp) => Promise<void>;
    createHiveCatalog: (catalogProp: Prop.CatalogProp) => Promise<void>;
    fetchCatalogData: () => Promise<void>;
};

export const useCatalogStore = create<Store>()(persist(
    (set) => ({
        catalogItemList: [],
        createFileSystemCatalog: async (catalogProp) => {
            try {
                const response = await Api.createFileSystemCatalog(catalogProp);
                if (!response) {
                    throw new Error('No response from createFileSystemCatalog');
                }
                if (response.code === 200) {
                    Toast.success(i18n.t('metadata.create-catalog-success'));
                } else {
                    console.error('Failed to create catalog:', response.msg);
                    Toast.error(i18n.t('metadata.create-catalog-failed') +  response.msg);
                }
            } catch (error) {
                console.error('Failed to create catalog:', error);
                Toast.error(i18n.t('metadata.create-database-failed') + error);
            }
        },
        createHiveCatalog: async (catalogProp) => {
            try {
                const response = await Api.createHiveCatalog(catalogProp);
                if (!response) {
                    throw new Error('No response from createFileSystemCatalog');
                }
                if (response.code === 200) {
                    Toast.success(i18n.t('metadata.create-catalog-success'));
                } else {
                    console.error('Failed to create catalog:', response.msg);
                    Toast.error(i18n.t('metadata.create-database-failed') +  response.msg);
                }
            } catch (error) {
                console.error('Failed to create catalog:', error);
                Toast.error(i18n.t('metadata.create-database-failed') + error);
            }
        },
        fetchCatalogData: async () => {
            try {
                const result = await Api.getAllCatalogs();
                if (result && result.data) {
                    const newCatalogItemList = result.data.map((item) => {
                        return {
                            id: item.id,
                            catalogName: item.catalogName,
                            catalogType: item.catalogType,
                            warehouse: item.warehouse,
                            hiveUri: item.hiveUri,
                            hiveConfDir: item.hiveConfDir,
                            isDelete: item.isDelete,
                            createTime: item.createTime,
                            updateTime: item.updateTime
                        };
                    });
                    set((state) => ({ ...state, catalogItemList: newCatalogItemList }));
                }
            } catch (error) {
                console.error('Failed to get catalogs:', error);
            }
        },
    }),{
        name: 'catalog-storage'
    }
))