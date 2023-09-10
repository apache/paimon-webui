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
    columnInputs: Array<{}>;
    optionInputs: Array<{}>;
    configs: Array<{}>;
    tableItemList: TableItem[];
    tableNodeClicked: string;
    setTableNodeClicked: (newTableNodeClicked: string) => void;
    setInputs: (newInputs: Array<{}>) => void;
    setColumnInputs: (newInputs: Array<{}>) => void;
    setOptionInputs: (newInputs: Array<{}>) => void;
    setConfigs: (newConfigs: Array<{}>) => void;
    createTable: (tableProp: TableItem) => Promise<void>;
    dropTable: (catalogName: string, databaseName: string, tableName: string) => Promise<void>;
    renameTable: (catalogName: string, databaseName: string, fromTableName: string,  toTableName: string) => Promise<void>;
    addColumn: (tableProp: TableItem) => Promise<void>;
    modifyOption: (tableProp: TableItem) => Promise<void>;
    dropColumn: (catalogName: string, databaseName: string, tableName: string, columnName: string) => Promise<void>;
    renameColumn: (catalogName: string, databaseName: string, tableName: string, fromColumnName: string,  toColumnName: string) => Promise<void>;
    updateColumnType: (tableProp: TableItem) => Promise<void>;
    updateColumnComment: (tableProp: TableItem) => Promise<void>;
    addOption: (tableProp: TableItem) => Promise<void>;
    removeOption: (tableProp: TableItem) => Promise<void>;
    fetchTables: () => Promise<void>;
};

export const useTableStore = create<Store>((set) => ({
    inputs: [{}],
    columnInputs: [{}],
    optionInputs: [{}],
    configs: [],
    tableItemList: [],
    tableNodeClicked: "",
    setTableNodeClicked: (newTableNodeClicked) => set(() => ({ tableNodeClicked: newTableNodeClicked })),
    setInputs: (newInputs) => set(() => ({ inputs: newInputs })),
    setColumnInputs: (newInputs) => set(() => ({ columnInputs: newInputs })),
    setOptionInputs: (newInputs) => set(() => ({ optionInputs: newInputs })),
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
                console.error(i18n.t('metadata.create-table-failed'), response.msg);
                Toast.error(i18n.t('metadata.create-table-failed') +  response.msg);
            }
        } catch (error: any) {
            console.error(i18n.t('metadata.create-table-failed'), error.value);
            Toast.error(i18n.t('metadata.create-table-failed') + error.value);
        }
    },
    dropTable: async (catalogName: string, databaseName: string, tableName: string) => {
        try {
            const response = await Api.dropTable(catalogName, databaseName, tableName);
            if (!response) {
                throw new Error('No response from dropTable');
            }
            if (response.code === 200) {
                Toast.success(i18n.t('metadata.drop-table-success'));
            } else {
                console.error(i18n.t('metadata.drop-table-failed'), response.msg);
                Toast.error(i18n.t('metadata.drop-table-failed') +  response.msg);
            }
        } catch (error: any) {
            console.error(i18n.t('metadata.drop-table-failed'), error.value);
            Toast.error(i18n.t('metadata.drop-table-failed') + error.value);
        }
    },
    renameTable: async (catalogName, databaseName, fromTableName,  toTableName) => {
        try {
            const response = await Api.renameTable(catalogName, databaseName, fromTableName, toTableName);
            if (!response) {
                throw new Error('No response from renameTable');
            }
            if (response.code === 200) {
                Toast.success(i18n.t('metadata.rename-table-success'));
            } else {
                console.error(i18n.t('metadata.rename-table-failed'), response.msg);
                Toast.error(i18n.t('metadata.rename-table-failed') +  response.msg);
            }
        } catch (error: any) {
            console.error(i18n.t('metadata.rename-table-failed'), error.value);
            Toast.error(i18n.t('metadata.rename-table-failed') + error.value);
        }
    },
    addColumn: async (tableProp) => {
        try {
            const response = await Api.addColumn(tableProp);
            if (!response) {
                throw new Error('No response from addColumn');
            }
            if (response.code === 200) {
                Toast.success(i18n.t('metadata.add-column-success'));
            } else {
                console.error(i18n.t('metadata.add-column-failed'), response.msg);
                Toast.error(i18n.t('metadata.add-column-failed') +  response.msg);
            }
        } catch (error: any) {
            console.error(i18n.t('metadata.add-column-failed'), error.value);
            Toast.error(i18n.t('metadata.add-column-failed') + error.value);
        }
    },
    dropColumn: async (catalogName, databaseName, tableName, columnName) => {
        try {
            const response = await Api.dropColumn(catalogName, databaseName, tableName, columnName);
            if (!response) {
                throw new Error('No response from dropColumn');
            }
            if (response.code === 200) {
                Toast.success(i18n.t('metadata.drop-column-success'));
            } else {
                console.error(i18n.t('metadata.drop-column-failed'), response.msg);
                Toast.error(i18n.t('metadata.drop-column-failed') +  response.msg);
            }
        } catch (error: any) {
            console.error(i18n.t('metadata.drop-column-failed'), error.value);
            Toast.error(i18n.t('metadata.drop-column-failed') + error.value);
        }
    },
    renameColumn: async (catalogName, databaseName, tableName, fromColumnName, toColumnName) => {
        try {
            const response = await Api.renameColumn(catalogName, databaseName, tableName, fromColumnName, toColumnName);
            if (!response) {
                throw new Error('No response from renameColumn');
            }
            if (response.code === 200) {
                // Toast.success(i18n.t('metadata.modify-column-success'));
            } else {
                console.error(i18n.t('metadata.modify-column-failed', response.msg));
                // Toast.error(i18n.t('metadata.modify-column-failed') +  response.msg);
            }
        } catch (error: any) {
            console.error(i18n.t('metadata.modify-column-failed', error.value));
            // Toast.error(i18n.t('metadata.modify-column-failed') + error.value);
        }
    },
    updateColumnType: async (tableProp) => {
        try {
            const response = await Api.updateColumnType(tableProp);
            if (!response) {
                throw new Error('No response from updateColumnType');
            }
            if (response.code === 200) {
                // Toast.success(i18n.t('metadata.modify-column-success'));
            } else {
                console.error(i18n.t('metadata.modify-column-failed'), response.msg);
                // Toast.error(i18n.t('metadata.modify-column-failed') +  response.msg);
            }
        } catch (error: any) {
            console.error(i18n.t('metadata.modify-column-failed'), error.value);
            // Toast.error(i18n.t('metadata.modify-column-failed') + error.value);
        }
    },
    updateColumnComment: async (tableProp) => {
        try {
            const response = await Api.updateColumnComment(tableProp);
            if (!response) {
                throw new Error('No response from updateColumnComment');
            }
            if (response.code === 200) {
                // Toast.success(i18n.t('metadata.modify-column-success'));
            } else {
                console.error(i18n.t('metadata.modify-column-failed'), response.msg);
                // Toast.error(i18n.t('metadata.modify-column-failed') +  response.msg);
            }
        } catch (error: any) {
            console.error(i18n.t('metadata.modify-column-failed'), error.value);
            // Toast.error(i18n.t('metadata.modify-column-failed') + error.value);
        }
    },
    addOption: async (tableProp) => {
        try {
            const response = await Api.addOption(tableProp);
            if (!response) {
                throw new Error('No response from addOption');
            }
            if (response.code === 200) {
               Toast.success(i18n.t('metadata.add-option-success'));
            } else {
                console.error(i18n.t('metadata.add-option-failed'), response.msg);
                Toast.error(i18n.t('metadata.add-option-failed') +  response.msg);
            }
        } catch (error: any) {
            console.error(i18n.t('metadata.add-option-failed'), error.value);
            Toast.error(i18n.t('metadata.add-option-failed') + error.value);
        }
    },
    modifyOption: async (tableProp) => {
        try {
            const response = await Api.addOption(tableProp);
            if (!response) {
                throw new Error('No response from addOption');
            }
            if (response.code === 200) {
                Toast.success(i18n.t('metadata.modify-option-success'));
            } else {
                console.error(i18n.t('metadata.modify-option-failed'), response.msg);
                Toast.error(i18n.t('metadata.modify-option-failed') +  response.msg);
            }
        } catch (error: any) {
            console.error(i18n.t('metadata.modify-option-failed'), error.value);
            Toast.error(i18n.t('metadata.modify-option-failed') + error.value);
        }
    },
    removeOption: async (tableProp) => {
        try {
            const response = await Api.removeOption(tableProp);
            if (!response) {
                throw new Error('No response from removeOption');
            }
            if (response.code === 200) {
                Toast.success(i18n.t('metadata.remove-option-success'));
            } else {
                console.error(i18n.t('metadata.remove-option-failed'), response.msg);
                Toast.error(i18n.t('metadata.remove-option-failed') +  response.msg);
            }
        } catch (error: any) {
            console.error(i18n.t('metadata.remove-option-failed'), error.value);
            Toast.error(i18n.t('metadata.remove-option-failed') + error.value);
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
        } catch (error: any) {
            console.error('Failed to get tables:', error.value);
        }
    },
}));

