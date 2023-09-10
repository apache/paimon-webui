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

import {Typography, Table, Checkbox, Tooltip, Toast} from '@douyinfe/semi-ui';
import {IconPlus} from "@douyinfe/semi-icons";
import { useTranslation } from 'react-i18next';
import {useEffect, useRef, useState} from "react";
import {useTableStore} from "@src/store/tableStore.ts";
import {TableColumn, TableItem} from "@src/types/Table/data";
import CircleDeleteBtn from "@components/Btn/CircleDeleteBtn";
import CircleEditBtn from "@components/Btn/CircleEditBtn";
import styles from './table-info-content.module.less';
import AddColumnModalForm from "@pages/Metadata/components/RightContent/components/AddColumnModalForm";
import EditColumnModalForm from "@pages/Metadata/components/RightContent/components/EditColumnModalForm";

const { Title } = Typography;

const TableInfoContent = () => {
    const { t } = useTranslation()
    const tableNodeClicked = useTableStore((state) => state.tableNodeClicked);
    const tableItemList = useTableStore(state => state.tableItemList);
    const [showColumnModal, setShowColumnModal] = useState(false);
    const [showEditColumnModal, setShowEditColumnModal] = useState(false);
    const [selectedColumnName, setSelectedColumnName] = useState(null);
    const [selectedColumnDataType, setSelectedColumnDataType] = useState(null);
    const [selectedColumnDescription, setSelectedColumnDescription] = useState(null);
    const {columnInputs, setColumnInputs, addColumn, fetchTables,
        dropColumn, renameColumn, updateColumnType, updateColumnComment } = useTableStore();

    const catalogName = tableNodeClicked.split("#")[0];
    const databaseName = tableNodeClicked.split("#")[1];
    const tableName = tableNodeClicked.split("#")[2];

    const handleOpenColumnModal = () => {
        setShowColumnModal(true);
    };

    const handleCloseColumnModal = () => {
        setColumnInputs([{}]);
        setShowColumnModal(false);
    };

    const handleOpenEditColumnModal = (record: any) => {
        setSelectedColumnName(record.columnName);
        setSelectedColumnDataType(record.columnType);
        setSelectedColumnDescription(record.description);
        setShowEditColumnModal(true);
    };

    const handleCloseEditColumnModal = () => {
        setShowEditColumnModal(false);
    };

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

    const handleAddColumnOk = (formApi: any) => {
        return new Promise<void>((resolve, reject) => {
            formApi
                .validate()
                .then(() => {
                    const values = formApi.getValues();
                    let tableColumns: TableColumn[] = [];

                    if (columnInputs.length > 0) {
                        columnInputs.map((_, index) => {
                            const field = values[`field${index}`];
                            const dataType = values[`type${index}`];
                            const comment = values[`comment${index}`];
                            const nullable = values[`nullable${index}`];
                            const defaultValue = values[`defaultValue${index}`];

                            if (field === undefined || dataType === undefined) {
                                Toast.error(t('metadata.file-and-type-is-required'));
                                throw new Error(t('metadata.file-and-type-is-required'));
                            }

                            if (field !== undefined && dataType !== undefined) {
                                const tableColumn: TableColumn = {
                                    field: field,
                                    dataType: dataType,
                                    comment: comment === undefined ? null : comment,
                                    isPK: false,
                                    isNullable: nullable,
                                    defaultValue: defaultValue === undefined ? null : defaultValue,
                                    length0: getLength0(dataType, values, index),
                                    length1: getLength1(dataType, values, index),
                                }
                                tableColumns.push(tableColumn);
                            }
                        });
                    }

                    const tableProp: TableItem = {
                        catalogName: catalogName,
                        databaseName: databaseName,
                        tableName: tableName,
                        description: null,
                        tableColumns: tableColumns,
                        partitionKey: [],
                        tableOptions: new Map<string, string>(),
                    }
                    addColumn(tableProp)
                        .then(() => {
                            fetchTables();
                            setColumnInputs([{}]);
                            resolve();
                        })
                        .catch((error: any) => {
                            console.log(error);
                            setColumnInputs([{}]);
                            reject(error);
                        });
                })
                .catch((errors: any) => {
                    console.log(errors);
                    setColumnInputs([{}]);
                    reject(errors);
                });
        });
    }

    const handleModifyColumnOk = (formApi: any) => {
        return new Promise<void>((resolve, reject) => {
            formApi
                .validate()
                .then(() => {
                    const values = formApi.getValues();
                    let tableColumns: TableColumn[] = [];

                    if (values.field !== selectedColumnName) {
                        const tableColumnOld: TableColumn = {
                            field: selectedColumnName == null ? values.field : selectedColumnName,
                            dataType: values.type,
                            comment: values.description,
                            isPK: false,
                            defaultValue: null,
                            isNullable: true,
                            length0: 0,
                            length1: 0,
                        }

                        const tableColumnNew: TableColumn = {
                            field: values.field,
                            dataType: values.type,
                            comment: values.description,
                            isPK: false,
                            defaultValue: null,
                            isNullable: true,
                            length0: 0,
                            length1: 0,
                        }

                        tableColumns.push(tableColumnOld);
                        tableColumns.push(tableColumnNew);

                        const tableProp: TableItem = {
                            catalogName: catalogName,
                            databaseName: databaseName,
                            tableName: tableName,
                            description: values.description,
                            tableColumns: tableColumns,
                            partitionKey: [],
                            tableOptions: new Map<string, string>(),
                        }

                        renameColumn(catalogName, databaseName, tableName, selectedColumnName == null ? values.field : selectedColumnName, values.field)
                            .then(() => {
                                updateColumnType(tableProp)
                                    .then(() => {
                                        updateColumnComment(tableProp)
                                            .then(() => {
                                                Toast.success(t('metadata.modify-column-success'));
                                                fetchTables();
                                                resolve();
                                            })
                                            .catch((error: any) => {
                                                console.log(error);
                                                Toast.error(t('metadata.modify-column-failed') + error.value);
                                                reject(error);
                                            })
                                    })
                                    .catch((error: any) => {
                                        console.log(error);
                                        Toast.error(t('metadata.modify-column-failed') + error.value);
                                        reject(error);
                                    })
                                resolve();
                            })
                            .catch((error: any) => {
                                console.log(error);
                                Toast.error(t('metadata.modify-column-failed') + error.value);
                                reject(error);
                            });

                    } else {
                        const tableColumn: TableColumn = {
                            field: values.field,
                            dataType: values.type,
                            comment: values.description,
                            isPK: false,
                            defaultValue: null,
                            isNullable: true,
                            length0: 0,
                            length1: 0,
                        }

                        tableColumns.push(tableColumn);

                        const tableProp: TableItem = {
                            catalogName: catalogName,
                            databaseName: databaseName,
                            tableName: tableName,
                            description: values.description,
                            tableColumns: tableColumns,
                            partitionKey: [],
                            tableOptions: new Map<string, string>(),
                        }

                        updateColumnType(tableProp)
                            .then(() => {
                                updateColumnComment(tableProp)
                                    .then(() => {
                                        Toast.success(t('metadata.modify-column-success'));
                                        fetchTables();
                                        resolve();
                                    })
                                    .catch((error: any) => {
                                        console.log(error);
                                        Toast.error(t('metadata.modify-column-failed') + error.value);
                                        reject(error);
                                    })
                            })
                            .catch((error: any) => {
                                console.log(error);
                                Toast.error(t('metadata.modify-column-failed') + error.value);
                                reject(error);
                            })
                        resolve();
                    }
                })
                .catch((errors: any) => {
                    console.log(errors);
                    Toast.error(t('metadata.modify-column-failed') + errors.value);
                    reject(errors);
                });
        });
    }

    const handleDropColumn = (key: string) => {
        const onConfirm = async () => {
            const numericKey = Number(key);
            const columnToRemove = columnDataSource.find((column) => column.key === numericKey);
            if (!columnToRemove) {
                console.error('Column not found:', key);
                return;
            }

            dropColumn(catalogName, databaseName, tableName, columnToRemove.columnName)
                .then(() => {
                    fetchTables();
                })
                .catch((error: any) => {
                    console.log(error);
                });
        };

        return (
            <CircleDeleteBtn id={key.toString()} onConfirm={onConfirm} />
        );
    };

    const matchingTableItems: TableItem[] = tableItemList.filter((item: TableItem) =>
        item.catalogName === catalogName &&
        item.databaseName === databaseName &&
        item.tableName === tableName
    );

    const getType = (datatype: string, column: TableColumn) => {
        switch (datatype) {
            case 'CHAR':
            case 'VARCHAR':
            case 'BINARY':
            case 'VARBINARY':
            case 'TIME(precision)':
            case 'TIMESTAMP(precision)':
            case 'TIMESTAMP_WITH_LOCAL_TIME_ZONE(precision)':
                return datatype  + "(" + column.length0 + ")";
            case 'DECIMAL':
                return datatype  + "(" + column.length0 + "," + column.length1 + ")";
            default:
                return datatype;
        }
    }

    let columnDataSource: { key: number, columnName: string, columnType: string, isPk: boolean, partitionKey: boolean, description: string | null }[] = [];
    matchingTableItems.forEach((item: { partitionKey: string[] } & TableItem) => {
        const data = item.tableColumns.map((column: TableColumn, index: number) => {
            const isPartitionKey = item.partitionKey.includes(column.field);
            return {
                key: index + 1,
                columnName: column.field,
                columnType: getType(column.dataType, column),
                isPk: column.isPK,
                partitionKey: isPartitionKey,
                description: column.comment || '---',
                isNullable: column.isNullable,
                defaultValue: column.defaultValue || '---',
            };
        });
        columnDataSource = [...columnDataSource, ...data];
    });

    const columns = [
        { title: t('metadata.column-table-column-name'), dataIndex: 'columnName', key: 'columnName', fixed: true },
        { title: t('metadata.column-table-column-type'), dataIndex: 'columnType', key: 'columnType',  fixed: true },
        {
            title: t('metadata.add-column-nullable'),
            dataIndex: 'isNullable',
            key: 'isNullable',
            fixed: true,
            render: (_: any, record: any) => (
                <div style={{ pointerEvents: "none" }}>
                    <Checkbox checked={record.isNullable} />
                </div>
            ),
        },
        {
            title: t('metadata.column-table-column-primary-key'),
            dataIndex: 'isPk',
            key: 'isPk',
            fixed: true,
            render: (_: any, record: any) => (
                <div style={{ pointerEvents: "none" }}>
                    <Checkbox checked={record.isPk} />
                </div>
            ),
        },
        {
            title: t('metadata.column-table-column-partition-key'),
            dataIndex: 'partition',
            key: 'partition',
            fixed: true,
            render: (_: any, record: any) => (
                <div style={{ pointerEvents: "none" }}>
                    <Checkbox checked={record.partitionKey} />
                </div>
            ),
        },
        { title: t('metadata.add-column-default-value'), dataIndex: 'defaultValue', key: 'defaultValue',  fixed: true },
        { title: t('metadata.column-table-column-description'), dataIndex: 'description', key: 'description',  fixed: true },
        {
            title: t('metadata.table-column-operation'),
            dataIndex: 'operate',
            fixed: true,
            render: (_: any, record: any) => (
                <>
                    <CircleEditBtn onClick={() => handleOpenEditColumnModal(record)}/>
                    {handleDropColumn(record.key.toString())}
                </>
            ),
        },
    ];

    const containerRef = useRef<HTMLDivElement | null>(null);
    const [scroll, setScroll] = useState({ y: 0, x: 1200 });
    useEffect(() => {

        if (containerRef.current) {
            const height = containerRef.current.offsetHeight;
            setScroll({ y: height, x: 1200 });
        }
    }, []);

    return(
       <>
           <div ref={containerRef} className={styles.container}>
               <div className={styles.group}>
                   <div className={`${styles['group-title']} ${styles['group-title-container']}`}>
                       <Title
                           className={styles['group-title-text']}
                           heading={5}
                       >
                           {t('metadata.form-group-add-column-ordinary')}
                       </Title>
                       <div className={styles['icon-container']}>
                           <Tooltip content={t('metadata.add-column')}>
                               <IconPlus className={styles.iconPlus} onClick={handleOpenColumnModal}/>
                           </Tooltip>
                       </div>
                   </div>
                   <div className={styles['table-content']}>
                       <Table
                           dataSource={columnDataSource}
                           columns={columns}
                           scroll={scroll}
                           sticky={{ top: 0 }}
                           size={"small"}
                           className={styles['semi-table']}/>
                   </div>
               </div>
           </div>

           {showColumnModal && (
               <AddColumnModalForm visible={showColumnModal} onClose={handleCloseColumnModal} onOk={handleAddColumnOk}/>
           )}

           {showEditColumnModal && (
               <EditColumnModalForm
                   visible={showEditColumnModal}
                   onClose={handleCloseEditColumnModal}
                   onOk={handleModifyColumnOk}
                   selectedColumnName={selectedColumnName}
                   selectedColumnDataType={selectedColumnDataType}
                   selectedColumnDescription={selectedColumnDescription}
               />
           )}
       </>
    );
}

export default TableInfoContent;