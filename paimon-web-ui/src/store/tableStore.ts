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
import i18n from 'i18next';
import {TableItem} from "@src/types/Table/data";

type Store = {
    inputs: Array<{}>;
    configs: Array<{}>;
    tableItemList: TableItem[];
    tableNodeClicked: string;
    setTableNodeClicked: (newTableNodeClicked: string) => void;
    setInputs: (newInputs: Array<{}>) => void;
    setConfigs: (newConfigs: Array<{}>) => void;
    createTable: (tableProp: TableItem) => Promise<void>;
    fetchTables: () => Promise<void>;
};

export const useTableStore = create<Store>((set) => ({
    inputs: [{}],
    configs: [],
    tableItemList: [],
    tableNodeClicked: "",
    setTableNodeClicked: (newTableNodeClicked) => set(() => ({ tableNodeClicked: newTableNodeClicked })),
    setInputs: (newInputs) => set(() => ({ inputs: newInputs })),
    setConfigs: (newConfigs) => set(() => ({ configs: newConfigs })),
    createTable: async (tableProp) => {
        try {
            const response = await Api.createTable(tableProp);
            if (!response) {
                throw new Error('No response from createTable');
            }
            if (response.code === 200) {
                Toast.success(i18n.t('metadata.create-table-success'));
            } else {
                console.error('Failed to create table:', response.msg);
                Toast.error(i18n.t('metadata.create-table-failed') +  response.msg);
            }
        } catch (error) {
            console.error('Failed to create table:', error);
            Toast.error(i18n.t('metadata.create-table-failed') + error);
        }
    },
    fetchTables: async () => {
        try {
            const result = await Api.getAllTables();
            if (result && result.data) {
                const newTableItemList = result.data.map((item) => {
                    return {
                        catalogName: item.catalogName,
                        databaseName: item.databaseName,
                        tableName: item.tableName,
                        description: item.description,
                        tableColumns: item.tableColumns,
                        partitionKey: item.partitionKey,
                        tableOptions: item.tableOptions,
                    };
                });
                set((state) => ({ ...state, tableItemList: newTableItemList }));
            }
        } catch (error) {
            console.error('Failed to get tables:', error);
        }
    },
}));

