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

import {Typography, Table, Tooltip, Toast} from '@douyinfe/semi-ui';
import {IconPlus} from "@douyinfe/semi-icons";
import { useTranslation } from 'react-i18next';
import {useEffect, useRef, useState} from "react";
import {useTableStore} from "@src/store/tableStore.ts";
import {TableItem} from "@src/types/Table/data";
import CircleDeleteBtn from "@components/Btn/CircleDeleteBtn";
import CircleEditBtn from "@components/Btn/CircleEditBtn";
import AddOptionModalForm from "@pages/Metadata/components/RightContent/components/AddOptionModalForm";
import EditOptionModalForm from "@pages/Metadata/components/RightContent/components/EditOptionModalForm";
import styles from './option-info-content.module.less';

const { Title } = Typography;

const OptionInfoContent = () => {
    const { t } = useTranslation()
    const tableNodeClicked = useTableStore((state) => state.tableNodeClicked);
    const tableItemList = useTableStore(state => state.tableItemList);
    const [showOptionModal, setShowOptionModal] = useState(false);
    const [showEditOptionModal, setShowEditOptionModal] = useState(false);
    const [selectedOptionKey, setSelectedOptionKey] = useState(null);
    const [selectedOptionValue, setSelectedOptionValue] = useState(null);
    const { fetchTables, optionInputs, setOptionInputs, removeOption, addOption} = useTableStore();

    const catalogName = tableNodeClicked.split("#")[0];
    const databaseName = tableNodeClicked.split("#")[1];
    const tableName = tableNodeClicked.split("#")[2];

    const handleOpenOptionModal = () => {
        setShowOptionModal(true);
    };

    const handleCloseOptionModal = () => {
        setOptionInputs([{}]);
        setShowOptionModal(false);
    };

    const handleOpenEditOptionModal = (record: any) => {
        setSelectedOptionKey(record.optionName);
        setSelectedOptionValue(record.optionValue);
        setShowEditOptionModal(true);
    };

    const handleCloseEditOptionModal = () => {
        setShowEditOptionModal(false);
    };

    const handleAddOptionOk = (formApi: any) => {
        return new Promise<void>((resolve, reject) => {
            formApi
                .validate()
                .then(() => {
                    const values = formApi.getValues();
                    let tableOptions: Map<string, string> =new Map<string, string>();

                    if (optionInputs.length > 0) {
                        optionInputs.map((_, index) => {
                            const key = values[`configKey${index}`];
                            const value = values[`configValue${index}`];
                            tableOptions.set(key, value);
                        });
                    }

                    const tableProp: TableItem = {
                        catalogName: catalogName,
                        databaseName: databaseName,
                        tableName: tableName,
                        description: null,
                        tableColumns: [],
                        partitionKey: [],
                        tableOptions: Object.fromEntries(tableOptions),
                    }
                    addOption(tableProp)
                        .then(() => {
                            Toast.success(t('metadata.add-option-success'));
                            fetchTables();
                            setOptionInputs([{}]);
                            resolve();
                        })
                        .catch((error: any) => {
                            console.log(error);
                            Toast.error(t('metadata.add-option-failed') + error.value);
                            setOptionInputs([{}]);
                            reject(error);
                        });
                })
                .catch((errors: any) => {
                    console.log(errors);
                    Toast.error(t('metadata.add-option-failed') + errors.value);
                    setOptionInputs([{}]);
                    reject(errors);
                });
        });
    }

    const handleModifyOptionOk = (formApi: any) => {
        return new Promise<void>((resolve, reject) => {
            formApi
                .validate()
                .then(() => {
                    const values = formApi.getValues();
                    let tableOptions: Map<string, string> =new Map<string, string>();

                    tableOptions.set(values.key, values.value);

                    const tableProp: TableItem = {
                        catalogName: catalogName,
                        databaseName: databaseName,
                        tableName: tableName,
                        description: null,
                        tableColumns: [],
                        partitionKey: [],
                        tableOptions: Object.fromEntries(tableOptions),
                    }
                    addOption(tableProp)
                        .then(() => {
                            Toast.success(t('metadata.modify-option-success'));
                            fetchTables();
                            resolve();
                        })
                        .catch((error: any) => {
                            console.log(error);
                            Toast.error(t('metadata.modify-option-failed') + error.value);
                            reject(error);
                        });
                })
                .catch((errors: any) => {
                    console.log(errors);
                    Toast.error(t('metadata.modify-option-failed') + errors.value);
                    reject(errors);
                });
        });
    }

    const handleRemoveOption = (key: string) => {
        const onConfirm = async () => {
            const numericKey = Number(key);
            const optionToRemove = tableOptionsDataSource.find((option) => option.key === numericKey);
            if (!optionToRemove) {
                console.error('Option not found:', key);
                return;
            }

            const tableProp: TableItem = {
                catalogName: catalogName,
                databaseName: databaseName,
                tableName: tableName,
                description: null,
                tableColumns: [],
                partitionKey: [],
                tableOptions: Object.fromEntries(new Map([[optionToRemove.optionName, optionToRemove.optionValue]])),
            };
            removeOption(tableProp)
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

    let tableOptionsDataSource: { key: number, optionName: string, optionValue: string }[] = [];
    matchingTableItems.forEach((item: TableItem, itemIndex: number) => {
        // If item.tableOptions is not a Map, convert it to a Map
        if (!(item.tableOptions instanceof Map)) {
            item.tableOptions = new Map(Object.entries(item.tableOptions));
        }
        const data = Array.from(item.tableOptions.entries()).map(([key, value], optionIndex) => ({
            key: itemIndex * 100 + optionIndex + 1, // To ensure the uniqueness of the key
            optionName: key,
            optionValue: value,
        }));
        tableOptionsDataSource = [...tableOptionsDataSource, ...data];
    });

    const optionsColumns = [
        { title: t('metadata.add-config-key'), dataIndex: 'optionName', key: 'optionName', fixed: true,  width: 625,},
        { title: t('metadata.add-config-value'), dataIndex: 'optionValue', key: 'optionValue',  fixed: true,  width: 625,},
        {
            title: t('metadata.table-column-operation'),
            dataIndex: 'operate',
            fixed: true,
            width: 140,
            render: (_: any, record: any) => (
                <>
                    <CircleEditBtn onClick={() => handleOpenEditOptionModal(record)}/>
                    {handleRemoveOption(record.key.toString())}
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
                            {t('metadata.form-group-add-column-config')}
                        </Title>
                        <div className={styles['icon-container']}>
                            <Tooltip content={t('metadata.add-option')}>
                                <IconPlus className={styles.iconPlus} onClick={handleOpenOptionModal}/>
                            </Tooltip>
                        </div>
                    </div>
                    <div className={styles['table-content']}>
                        <Table
                            dataSource={tableOptionsDataSource}
                            columns={optionsColumns}
                            scroll={scroll}
                            sticky={{ top: 0 }}
                            size={"small"} />
                    </div>
                </div>
            </div>

            {showOptionModal && (
                <AddOptionModalForm visible={showOptionModal} onClose={handleCloseOptionModal} onOk={handleAddOptionOk}/>
            )}

            {showEditOptionModal && (
                <EditOptionModalForm
                    visible={showEditOptionModal}
                    onClose={handleCloseEditOptionModal}
                    onOk={handleModifyOptionOk}
                    selectedOptionKey={selectedOptionKey}
                    selectedOptionValue={selectedOptionValue}
                />
            )}
        </>
    );
}

export default OptionInfoContent;