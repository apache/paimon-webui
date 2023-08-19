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
import { IconSearch, IconFile, IconPlus } from "@douyinfe/semi-icons";
import {Tooltip} from "@douyinfe/semi-ui";
import {useEffect, useState} from "react";
import styles from "./catalog-tree.module.less"
import { useTranslation } from 'react-i18next';
import { useDatabaseStore } from "@src/store/databaseStore.ts";
import { useCatalogStore } from "@src/store/catalogStore.ts";
import DatabaseModalForm from "@pages/Metadata/components/LeftContent/components/DatabaseModalForm";
import TableModalForm from "@pages/Metadata/components/LeftContent/components/TableModalForm";

type TreeDataItem = {
    label: string;
    value: string;
    key: string;
    type: "catalog" | "database" | "table";
    catalogId: number;
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

    const handleOpenModal = (type: "catalog" | "database" | "table") => {
        if (type === "catalog" || type === "database") {
            setCreateType(type);
            setShowModal(true);
        }
    };

    const handleCloseModal = () => {
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
                .map(dbItem => ({
                    label: dbItem.databaseName,
                    value: dbItem.databaseName,
                    type: "database" as "database",
                    catalogId: item.id,
                    key: item.id.toString() + "-" + dbItem.databaseName
                })),
        }));
        setTreeData(transformedData);
    }, [catalogItemList, databaseItemList]);

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
                .then((values: any) => {
                    console.log(values);
                    resolve();
                })
                .catch((errors: any) => {
                    console.log(errors);
                    reject(errors);
                });
        });
    }

    const renderLabel = (x: any) => {
        const { className, onExpand, onClick, data, expandIcon } = x;
        const { label, key, type } = data;
        const isLeaf = !(data.children && data.children.length);

        return (
            <li
                className={`${className} ${styles.liClass}`}
                role="treeitem"
                onClick={isLeaf ? onClick : onExpand}
                onMouseEnter={() => handleMouseEnter(key)}
                onMouseLeave={handleMouseLeave}
                tabIndex={0}
            >
                {isLeaf ? <IconFile style={{marginRight: "8px", color: "var(--semi-color-text-2)"}}/> : expandIcon}
                <div style={{ flex: 1 }}>{label}</div>
                { key === hoveredNode && (type === "catalog" || type === "database") && (
                    <Tooltip content={type === "catalog" ? t('metadata.add-database') : t('metadata.add-table')}>
                        <IconPlus
                            className={styles.iconPlus}
                            onClick={() => handleOpenModal(type)}
                        />
                    </Tooltip>
                )}
            </li>
        );
    };

    return(
        <>
            <Tree
                filterTreeNode
                treeData={treeData}
                searchPlaceholder={t('common.filter')}
                searchRender={({ prefix, ...restProps }) => (
                    <Input suffix={<IconSearch className={styles['catalog-tree-input-icon']}/>} {...restProps} className={styles['catalog-tree-input']}></Input>
                )}
                renderFullLabel={renderLabel}
            />
            {showModal && createType  === "catalog" && (
                <DatabaseModalForm visible={showModal} onClose={handleCloseModal} onOk={handleCreateDatabaseOk}/>
            )}
            {showModal && createType === "database" && (
                <TableModalForm visible={showModal} onClose={handleCloseModal} onOk={handleCreateTableOk}/>
            )}
        </>
    );
}

export default CatalogTree;
