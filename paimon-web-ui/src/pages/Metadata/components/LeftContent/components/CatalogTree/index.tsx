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

import {Input, Tree} from '@douyinfe/semi-ui';
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
import styles from "./catalog-tree.module.less"

type TreeDataItem = {
    label: string;
    value: string;
    key: string;
    type: "catalog" | "database" | "table";
    catalogId: number;
    parentId?: number;
    children?: TreeDataItem[];
};

const CatalogTree = () => {
    const { t } = useTranslation()
    const [hoveredNode, setHoveredNode] = useState(null);
    const [showModal, setShowModal] = useState(false);
    const [createType, setCreateType] =
        useState<"catalog" | "database" | "table" | null>(null);

    // Database
    const createDatabase = useDatabaseStore(state => state.createDatabase);
    const fetchDatabases= useDatabaseStore(state => state.fetchDatabases);
    const databaseItemList = useDatabaseStore(state => state.databaseItemList);

    // Catalog
    const [treeData, setTreeData] = useState<TreeDataItem[]>([]);
    const fetchCatalogData = useCatalogStore(state => state.fetchCatalogData);
    const catalogItemList = useCatalogStore(state => state.catalogItemList);

    // Table
    const { inputs, configs, setInputs, setConfigs} = useTableStore();
    const createTable = useTableStore(state => state.createTable);
    const fetchTables= useTableStore(state => state.fetchTables);
    const tableItemList = useTableStore(state => state.tableItemList);

    const [selectedCatalogName, setSelectedCatalogName] = useState<string | null>(null);
    const [selectedDatabaseName, setSelectedDatabaseName] = useState<string | null>(null);

    const [expandedKeys, setExpandedKeys] = useState<string[]>([]);
    const [selectedKey, setSelectedKey] = useState(null);

    useEffect(() => {
        // Fetch the catalog data when the component mounts
        fetchTables();
    }, [fetchTables]);

    useEffect(() => {
        // Fetch the catalog data when the component mounts
        fetchDatabases();
    }, [fetchDatabases]);

    const handleMouseEnter = (key: any) => {
        setHoveredNode(key);
    };

    const handleMouseLeave = () => {
        setHoveredNode(null);
    };

    const handleOpenModal = (type: "catalog" | "database" | "table",  name: string, parentId?: number) => {
        if (type === "catalog" || type === "database") {
            if (type === "catalog") {
                setSelectedCatalogName(name);
            } else if (type === "database") {
                const databaseItem =
                    databaseItemList.find(item => item.databaseName === name && item.catalogId === parentId);
                if (databaseItem) {
                    const catalogItem = catalogItemList.find(item => item.id === databaseItem.catalogId);
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
        // Fetch the catalog data when the component mounts
        fetchCatalogData();
    }, [fetchCatalogData]);

    useEffect(() => {
        // Update treeData when catalogItemList changes
        const transformedData = catalogItemList.map(item => ({
            label: item.catalogName,
            value: item.catalogName,
            type: "catalog" as "catalog",
            catalogId: item.id,
            key: item.id.toString(),
            children: databaseItemList
                .filter(dbItem => dbItem.catalogId === item.id)
                .map(dbItem => {
                    const databaseKey = item.id.toString() + "-" + dbItem.databaseName;
                    const tableChildren = tableItemList
                        .filter(tableItem => tableItem.catalogName === item.catalogName && tableItem.databaseName === dbItem.databaseName)
                        .map(tableItem => ({
                            label: tableItem.tableName,
                            value: tableItem.tableName,
                            type: "table" as "table",
                            catalogId: item.id,
                            key: databaseKey + "-" + tableItem.tableName
                        }));
                    return {
                        label: dbItem.databaseName,
                        value: dbItem.databaseName,
                        type: "database" as "database",
                        catalogId: item.id,
                        parentId: item.id,
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
                .then((values: any) => {
                    const formData = values;
                    const databaseProp: Prop.DatabaseProp = {
                        databaseName: formData.databaseName,
                        catalogId: formData.catalogId,
                        description: formData.description
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
                            if (field !== undefined && dataType !== undefined) {
                                const tableColumn: TableColumn = {
                                    field: field,
                                    dataType: dataType,
                                    comment: comment === undefined ? null : comment,
                                    isPK: primaryKey !== undefined,
                                    defaultValue: defaultValue === undefined ? null : defaultValue,
                                }
                                tableColumns.push(tableColumn);
                            }
                        });
                    }

                    if (configs.length > 0) {
                        configs.map((_, index) => {
                            const key = values[`configKey${index}`];
                            const value = values[`configValue${index}`];
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
                        tableOptions: tableOptions,
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
                    console.log(tableProp)
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
            iconMore = <CatalogIconMoreDropdown/>;
        } else if (type === 'database') {
            iconMore = <DatabaseIconMoreDropdown/>;
        } else {
            iconMore = <TableIconMoreDropdown/>;
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
                filterTreeNode
                treeData={treeData}
                expandedKeys={expandedKeys}
                onExpand={onExpand}
                selectedKey={selectedKey}
                searchPlaceholder={t('common.filter')}
                searchRender={({ prefix, ...restProps }) => (
                    <Input suffix={<IconSearch className={styles['catalog-tree-input-icon']}/>} {...restProps} className={styles['catalog-tree-input']}></Input>
                )}
                renderFullLabel={renderLabel}
            />
            {showModal && createType  === "catalog" && (
                <DatabaseModalForm visible={showModal} onClose={handleCloseDatabaseModal} onOk={handleCreateDatabaseOk}/>
            )}
            {showModal && createType === "database" && (
                <TableModalForm
                    visible={showModal}
                    onClose={handleCloseTableModal}
                    onOk={handleCreateTableOk}
                    catalogName={selectedCatalogName}
                    databaseName={selectedDatabaseName}/>
            )}
        </>
    );
}

export default CatalogTree;
