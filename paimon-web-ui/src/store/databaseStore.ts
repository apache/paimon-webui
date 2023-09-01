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
import Api from "@api/api.ts";
import {Toast} from "@douyinfe/semi-ui";
import {DatabaseItem} from "@src/types/Database/data";
import i18n from 'i18next';

type Store = {
    databaseItemList: DatabaseItem[];
    createDatabase: (databaseProp: DatabaseItem) => Promise<void>;
    fetchDatabases: () => Promise<void>;
};

export const useDatabaseStore = create<Store>((set) => ({
    databaseItemList: [],
    createDatabase: async (databaseProp) => {
        try {
            const response = await Api.createDatabase(databaseProp);
            if (!response) {
                throw new Error('No response from createDatabase');
            }
            if (response.code === 200) {
                Toast.success(i18n.t('metadata.create-database-success'));
            } else {
                console.error('Failed to create database:', response.msg);
                Toast.error(i18n.t('metadata.create-database-failed') +  response.msg);
            }
        } catch (error) {
            console.error('Failed to create database:', error);
            Toast.error(i18n.t('metadata.create-database-failed') + error);
        }
    },
    fetchDatabases: async () => {
        try {
            const result = await Api.getAllDatabases();
            if (result && result.data) {
                const newDatabaseItemList = result.data.map((item) => {
                    return {
                        databaseName: item.databaseName,
                        catalogName: item.catalogName,
                        catalogId: null,
                        description: item.description,
                    };
                });
                set((state) => ({ ...state, databaseItemList: newDatabaseItemList }));
            }
        } catch (error) {
            console.error('Failed to get databases:', error);
        }
    },
}));