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

import {Input, Toast, Tree} from '@douyinfe/semi-ui';
import { IconSearch, IconFile, IconPlus, IconFolder, IconFolderOpen } from "@douyinfe/semi-icons";
import {Tooltip} from "@douyinfe/semi-ui";
import {useEffect, useState} from "react";
import { useTranslation } from 'react-i18next';
import { useDatabaseStore } from "@src/store/databaseStore.ts";
import { useCatalogStore } from "@src/store/catalogStore.ts";
import {useTableStore} from "@src/store/tableStore.ts";
import DatabaseModalForm from "@pages/Metadata/components/LeftContent/components/DatabaseModalForm";
import TableModalForm from "@pages/Metadata/components/LeftContent/components/TableModalForm";
import {TableItem, TableColumn} from "@src/types/Table/data";
import CatalogIconMoreDropdown from "@components/IconMoreDropdown/CatalogIconMoreDropdown";
import DatabaseIconMoreDropdown from "@components/IconMoreDropdown/DatabaseIconMoreDropdown";
import TableIconMoreDropdown from "@components/IconMoreDropdown/TableIconMoreDropdown";
import {DatabaseItem} from "@src/types/Database/data";
import EditTableModalForm from "@pages/Metadata/components/LeftContent/components/EditTableModalForm";
import styles from "./catalog-tree.module.less"

type TreeDataItem = {
    label: string;
    value: string;
    key: string;
    type: "catalog" | "database" | "table";
    catalogId: number;
    parentId?: string;
    children?: TreeDataItem[];
};

const CatalogTree = () => {
    const { t } = useTranslation()
    const [hoveredNode, setHoveredNode] = useState(null);
    const [showModal, setShowModal] = useState(false);
    const [showEditTableModal, setShowEditTableModal] = useState(false);
    const [createType, setCreateType] =
        useState<"catalog" | "database" | "table" | null>(null);

    // Database
    const createDatabase = useDatabaseStore(state => state.createDatabase);
    const fetchDatabases= useDatabaseStore(state => state.fetchDatabases);
    const databaseItemList = useDatabaseStore(state => state.databaseItemList);
    const  removeDatabase = useDatabaseStore(state => state.removeDatabase);

    // Catalog
    const [treeData, setTreeData] = useState<TreeDataItem[]>([]);
    const fetchCatalogData = useCatalogStore(state => state.fetchCatalogData);
    const removeCatalog = useCatalogStore(state => state.removeCatalog);
    const catalogItemList = useCatalogStore(state => state.catalogItemList);

    // Table
    const { inputs, configs, setInputs, setConfigs, createTable, fetchTables, tableItemList, dropTable, renameTable} = useTableStore();

    const [selectedCatalogName, setSelectedCatalogName] = useState<string | null>(null);
    const [selectedDatabaseName, setSelectedDatabaseName] = useState<string | null>(null);

    const [expandedKeys, setExpandedKeys] = useState<string[]>([]);
    const [selectedKey, setSelectedKey] = useState(null);

    const setTableNodeClicked = useTableStore((state) => state.setTableNodeClicked);

    const [selectedTableKey, setSelectedTableKey] = useState<string | null>(null);

    useEffect(() => {
        // Fetch the catalog data when the component mounts
        fetchCatalogData();
    }, [fetchCatalogData]);

    useEffect(() => {
        // Fetch the catalog data when the component mounts
        fetchDatabases();
    }, [fetchDatabases]);

    useEffect(() => {
        // Fetch the catalog data when the component mounts
        fetchTables();
    }, [fetchTables]);

    const handleMouseEnter = (key: any) => {
        setHoveredNode(key);
    };

    const handleMouseLeave = () => {
        setHoveredNode(null);
    };

    const handleOpenRenameTableModal = (key: string) => {
        setSelectedTableKey(key);
        setShowEditTableModal(true);
    };

    const handleCloseRenameTableModal = () => {
        setShowEditTableModal(false);
    };

    const handleRenameTableOk = (key: string) => {
        return (formApi: any) => {
            const catalogName = key.split("#")[0];
            const databaseName = key.split("#")[1];
            const tableName = key.split("#")[2];

            return new Promise<void>((resolve, reject) => {
                formApi
                    .validate()
                    .then(() => {
                        const values = formApi.getValues();

                        const oldTableProp: TableItem = {
                            catalogName: catalogName,
                            databaseName: databaseName,
                            tableName: tableName,
                            description: "",
                            tableColumns: [],
                            partitionKey: [],
                            tableOptions: new Map,
                        }
                        const newTableProp: TableItem = {
                            catalogName: catalogName,
                            databaseName: databaseName,
                            tableName: values.tableName,
                            description: "",
                            tableColumns: [],
                            partitionKey: [],
                            tableOptions: new Map,
                        }

                        renameTable([oldTableProp, newTableProp])
                            .then(() => {
                                fetchTables();
                                resolve();
                            })
                            .catch((error: any) => {
                                console.log(error);
                                reject(error);
                            });
                    })
                    .catch((errors: any) => {
                        console.log(errors);
                        reject(errors);
                    });
            });
        }
    }

    const handleOpenModal = (type: "catalog" | "database" | "table",  name: string, parentId?: string) => {
        if (type === "catalog" || type === "database") {
            if (type === "catalog") {
                setSelectedCatalogName(name);
            } else if (type === "database") {
                const databaseItem =
                    databaseItemList.find(item => item.databaseName === name && item.catalogName === parentId);
                if (databaseItem) {
                    const catalogItem = catalogItemList.find(item => item.catalogName === databaseItem.catalogName);
                    if (catalogItem) {
                        setSelectedCatalogName(catalogItem.catalogName);
                    }
                }
                setSelectedDatabaseName(name);
            }
            setCreateType(type);
            setShowModal(true);
        }
    };

    const handleCloseDatabaseModal = () => {
        setShowModal(false);
    };

    const handleCloseTableModal = () => {
        setInputs([{}]);
        setConfigs([]);
        setShowModal(false);
    };

    useEffect(() => {
        // Update treeData when catalogItemList changes
        const transformedData = catalogItemList.map(item => ({
            label: item.catalogName,
            value: item.catalogName,
            type: "catalog" as "catalog",
            catalogId: item.id,
            key: item.catalogName,
            children: databaseItemList
                .filter(dbItem => dbItem.catalogName === item.catalogName)
                .map(dbItem => {
                    const databaseKey = item.catalogName + "-" + dbItem.databaseName;
                    const tableChildren = tableItemList
                        .filter(tableItem =>
                            tableItem.catalogName === item.catalogName && tableItem.databaseName === dbItem.databaseName)
                        .map(tableItem => ({
                            label: tableItem.tableName,
                            value: tableItem.tableName,
                            type: "table" as "table",
                            catalogId: item.id,
                            key: item.catalogName + "#" + dbItem.databaseName + "#" + tableItem.tableName
                        }));
                    return {
                        label: dbItem.databaseName,
                        value: dbItem.databaseName,
                        type: "database" as "database",
                        catalogId: item.id,
                        parentId: item.catalogName,
                        key: databaseKey,
                        children: tableChildren
                    };
                }),
        }));
        setTreeData(transformedData);
    }, [catalogItemList, databaseItemList, tableItemList]);

    const handleCreateDatabaseOk = (formApi: any) => {
        return new Promise<void>((resolve, reject) => {
            formApi
                .validate()
                .then(() => {
                    const values = formApi.getValues();
                    const catalogName = formApi.catalogName;
                    const databaseProp: DatabaseItem = {
                        databaseName: values.databaseName,
                        catalogId: null,
                        catalogName: catalogName,
                        description: values.description
                    };
                    createDatabase(databaseProp)
                        .then(() => {
                            fetchDatabases();
                            resolve();
                        })
                        .catch((error: any) => {
                            console.log(error);
                            reject(error);
                        });
                })
                .catch((errors: any) => {
                    console.log(errors);
                    reject(errors);
                });
        });
    }

    const getLength0 = (dataType: string, values: any, index: number) => {
        switch (dataType) {
            case 'CHAR':
            case 'VARCHAR':
            case 'BINARY':
            case 'VARBINARY':
            case 'TIME(precision)':
            case 'TIMESTAMP(precision)':
            case 'TIMESTAMP_WITH_LOCAL_TIME_ZONE(precision)':
                const length = values[`length${index}`];
                if (length !== undefined && length !== null) {
                    return length;
                } else {
                    return 0;
                }
            case 'DECIMAL':
                const precision = values[`precision${index}`];
                if (precision !== undefined && precision !== null) {
                    return precision;
                } else {
                    return 38;
                }
            default:
                return 0;
        }
    }

    const getLength1 = (dataType: string, values: any, index: number) => {
        if (dataType === "DECIMAL") {
            const scale = values[`scale${index}`];
            if (scale !== undefined && scale !== null) {
                return scale;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    const handleCreateTableOk = (formApi: any) => {
        return new Promise<void>((resolve, reject) => {
            formApi
                .validate()
                .then(() => {
                    const values = formApi.getValues();
                    const catalogName = formApi.catalogName;
                    const databaseName = formApi.databaseName;

                    let tableColumns: TableColumn[] = [];
                    let tableOptions: Map<string, string> =new Map<string, string>();
                    if (inputs.length > 0) {
                        inputs.map((_, index) => {
                            const field = values[`field${index}`];
                            const dataType = values[`type${index}`];
                            const comment = values[`comment${index}`];
                            const primaryKey = values[`primaryKey${index}`];
                            const defaultValue = values[`defaultValue${index}`];
                            const nullable = values[`nullable${index}`];

                            if (field === undefined || dataType === undefined) {
                                Toast.error(t('metadata.file-and-type-is-required'));
                                throw new Error(t('metadata.file-and-type-is-required'));
                            }

                            if (field !== undefined && dataType !== undefined) {
                                const tableColumn: TableColumn = {
                                    field: field,
                                    dataType: dataType,
                                    comment: comment === undefined ? null : comment,
                                    isPK: primaryKey !== undefined,
                                    defaultValue: defaultValue === undefined ? null : defaultValue,
                                    isNullable: nullable,
                                    length0: getLength0(dataType, values, index),
                                    length1: getLength1(dataType, values, index),
                                }
                                tableColumns.push(tableColumn);
                            }
                        });
                    }

                    if (configs.length > 0) {
                        configs.map((_, index) => {
                            const key = values[`configKey${index}`];
                            const value = values[`configValue${index}`];
                            if (key !== undefined && value === undefined) {
                                Toast.error(t('metadata.value-can-not-null'));
                                throw new Error(t('metadata.value-can-not-null'));
                            }
                            if (value !== undefined && key === undefined) {
                                Toast.error(t('metadata.key-can-not-null'));
                                throw new Error(t('metadata.key-can-not-null'));
                            }
                            tableOptions.set(key, value);
                        });
                    }

                    const tableProp: TableItem = {
                        catalogName: catalogName,
                        databaseName: databaseName,
                        tableName: values.tableName,
                        description: values.description === undefined ? null : values.description,
                        tableColumns: tableColumns,
                        partitionKey: values.partitionKey === undefined ? [] : values.partitionKey,
                        tableOptions: Object.fromEntries(tableOptions),
                    }


                    createTable(tableProp)
                        .then(() => {
                            fetchTables();
                            setInputs([{}]);
                            setConfigs([]);
                            resolve();
                        })
                        .catch((error: any) => {
                            console.log(error);
                            setInputs([{}]);
                            setConfigs([]);
                            reject(error);
                        });
                })
                .catch((errors: any) => {
                    console.log(errors);
                    setInputs([{}]);
                    setConfigs([]);
                    reject(errors);
                });
        });
    }

    const findNodeByKey = (key: string, nodes?: TreeDataItem[]): TreeDataItem | null => {
        if (!nodes) {
            return null;
        }

        for (let i = 0; i < nodes.length; i++) {
            if (nodes[i].key === key) {
                return nodes[i];
            } else if (nodes[i].children) {
                const foundNode = findNodeByKey(key, nodes[i].children);
                if (foundNode) {
                    return foundNode;
                }
            }
        }
        return null;
    };

    const onTreeNodeClick = (key: any) => {
        const node = findNodeByKey(key, treeData);
        if (node && node.type === 'table') {
            setTableNodeClicked(key);
        }
    };

    const onExpand = (_: string[], info: any) => {
        const key = info.node.key;
        const expanded = info.expanded;

        if (expanded) {
            setExpandedKeys(prevKeys => [...prevKeys, key]);
        } else {
            const nodeToCollapse = findNodeByKey(key, treeData);
            let keysToCollapse = [key];
            if (nodeToCollapse && nodeToCollapse.children) {
                keysToCollapse = [...keysToCollapse, ...nodeToCollapse.children.map(child => child.key)];
            }
            setExpandedKeys(prevKeys => prevKeys.filter(k => !keysToCollapse.includes(k)));
        }
    };

    const onConfirmRemoveCatalog = async (catalogName: string) => {

        const catalogProp: Prop.CatalogProp = {
            catalogName: catalogName,
            catalogType: "",
            warehouse: "",
            hiveUri: "",
            hiveConfDir: "",
            isDelete: false
        };

        removeCatalog(catalogProp)
            .then(() => {
                fetchCatalogData();
            })
            .catch((error: any) => {
                console.log(error);
            });
    };

    const onConfirmRemoveDatabase = async (databaseName: string, catalogName: string) => {

        const databaseProp: DatabaseItem = {
            databaseName: databaseName,
            catalogId: null,
            catalogName: catalogName,
            description: "",
        };

        removeDatabase(databaseProp)
            .then(() => {
                fetchDatabases();
            })
            .catch((error: any) => {
                console.log(error);
            });
    };

    const onConfirmDropTable = async (key: string) => {
        const catalogName = key.split("#")[0];
        const databaseName = key.split("#")[1];
        const tableName = key.split("#")[2];

        const tableProp: TableItem = {
            catalogName: catalogName,
            databaseName: databaseName,
            tableName: tableName,
            description: "",
            tableColumns: [],
            partitionKey: [],
            tableOptions: new Map,
        }

        dropTable(tableProp)
            .then(() => {
                fetchTables();
            })
            .catch((error: any) => {
                console.log(error);
            });
    };

    const renderLabel = (x: any) => {
        const { className, onExpand, onClick, data, expandIcon } = x;
        const { label, key, type } = data;
        const isLeaf = !(data.children && data.children.length);
        const hasChildren = data.children && data.children.length > 0;
        const isExpanded = expandedKeys.includes(key);
        const isSelected = selectedKey === key;

        let icon;
        if (type === 'table') {
            icon = <IconFile size={"small"} className={styles.icon}/>;
        } else if (hasChildren) {
            icon = isExpanded ? <IconFolderOpen size={"small"} className={styles.icon}/>
                : <IconFolder size={"small"} className={styles.icon}/>
        } else {
            icon = <IconFolder size={"small"} className={styles.icon}/>;
        }

        let iconMore;
        if (type === 'catalog') {
            iconMore = <CatalogIconMoreDropdown id={label} onConfirm={() => onConfirmRemoveCatalog(label)}/>;
        } else if (type === 'database') {
            iconMore = <DatabaseIconMoreDropdown id={label} onConfirm={() => onConfirmRemoveDatabase(label, data.parentId)}/>;
        } else {
            iconMore = <TableIconMoreDropdown id={label} onConfirm={() => onConfirmDropTable(key)} onRename={() => handleOpenRenameTableModal(key)}/>;
        }

        return (
            <li
                className={`${className} ${styles.liClass} ${isSelected ? styles['semi-tree-node-selected'] : ''}`}
                role="treeitem"
                onClick={(e) => {
                    if (isLeaf) {
                        onClick(e);
                    } else {
                        onExpand(e);
                    }
                    setSelectedKey(key);
                }}
                onMouseEnter={() => handleMouseEnter(key)}
                onMouseLeave={handleMouseLeave}
                tabIndex={0}
            >
                {expandIcon}
                {icon}
                <div style={{ flex: 1 }}>{label}</div>
                { key === hoveredNode && (type === "catalog" || type === "database") && (
                    <Tooltip content={type === "catalog" ? t('metadata.add-database') : t('metadata.add-table')}>
                        <IconPlus
                            className={styles.iconPlus}
                            onClick={(e) => {
                                e.stopPropagation();
                                handleOpenModal(type, label, data.parentId)
                            }}
                        />
                    </Tooltip>
                )}
                <div className={styles.iconMore}>{iconMore}</div>
            </li>
        );
    };

    return(
        <>
            <Tree
                className={styles.container}
                filterTreeNode
                treeData={treeData}
                expandedKeys={expandedKeys}
                onExpand={onExpand}
                selectedKey={selectedKey}
                onSelect={onTreeNodeClick}
                searchPlaceholder={t('common.filter')}
                searchRender={({ prefix, ...restProps }) => (
                    <Input
                        suffix={<IconSearch className={styles['catalog-tree-input-icon']}/>}
                        {...restProps}
                        className={styles['catalog-tree-input']}>
                    </Input>
                )}
                renderFullLabel={renderLabel}
            />
            {showModal && createType  === "catalog" && (
                <DatabaseModalForm
                    visible={showModal}
                    onClose={handleCloseDatabaseModal}
                    onOk={handleCreateDatabaseOk}
                    catalogName={selectedCatalogName}
                />
            )}
            {showModal && createType === "database" && (
                <TableModalForm
                    visible={showModal}
                    onClose={handleCloseTableModal}
                    onOk={handleCreateTableOk}
                    catalogName={selectedCatalogName}
                    databaseName={selectedDatabaseName}
                />
            )}
            {showEditTableModal && (
                <EditTableModalForm
                    visible={showEditTableModal}
                    onClose={handleCloseRenameTableModal}
                    onOk={handleRenameTableOk(selectedTableKey || '')}
                    initialValues={{ tableName: selectedTableKey?.split("#")[2] || '' }}
                />
            )}
        </>
    );
}

export default CatalogTree;
