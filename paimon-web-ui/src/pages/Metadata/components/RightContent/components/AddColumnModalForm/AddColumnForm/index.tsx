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

import {Button, Form, Table} from "@douyinfe/semi-ui";
import {IconMinus, IconPlus} from "@douyinfe/semi-icons";
import options from "@utils/options.ts";
import {useEffect, useRef, useState} from "react";
import {useTranslation} from "react-i18next";
import {useTableStore} from "@src/store/tableStore.ts";

// @ts-ignore
const AddColumnForm = ({ getFormApi }) => {
    const formApiRef = useRef<any>(null);
    const { t } = useTranslation();
    const { columnInputs,setColumnInputs,  } = useTableStore();
    const [tableKey, setTableKey] = useState(0);
    const [inputStates, setInputStates] = useState<{ nullable: boolean, primaryKey: boolean }[]>([{ nullable: true, primaryKey: false }]);

    const onTypeChange = () => {
        if (formApiRef.current) {
            // do something with typeValue...
            setTableKey(prevKey => prevKey + 1);
        }
    };

    const handleAddInput = () => {
        const newInputs = columnInputs.concat({});
        setColumnInputs(newInputs);

        const newInputStates = inputStates.concat({ nullable: true, primaryKey: false });
        setInputStates(newInputStates);
    }

    useEffect(() => {
        if (formApiRef.current) {
            formApiRef.current.setValue(`nullable${inputStates.length - 1}`, inputStates[inputStates.length - 1]?.nullable);
        }
    }, [inputStates]);


    const handleRemoveInput = (index: any) => {
        if (formApiRef.current) {
            const newInputs = [...columnInputs];
            formApiRef.current.setValue(`field${index}`, null);
            formApiRef.current.setValue(`comment${index}`, null);
            formApiRef.current.setValue(`type${index}`, null);
            newInputs.splice(index, 1);
            setColumnInputs(newInputs);
        }
    }

    const columns = [
        {
            title: <span>{t('metadata.add-column-field')}<span style={{ color: 'rgba(var(--semi-red-5), 1)', marginLeft: '4px' }}>*</span></span>,
            dataIndex: 'field',
            key: 'field',
            render: (_: any, __: any, index: any) => (
                <Form.Input
                    noLabel={true}
                    field={`field${index}`}
                    style={{ width: "160px" }}
                    showClear />
            ),
        },
        {
            title: <span>{t('metadata.add-column-type')}<span style={{ color: 'rgba(var(--semi-red-5), 1)', marginLeft: '4px' }}>*</span></span>,
            dataIndex: 'type',
            key: 'type',
            render: (_: any, __: any, index: any) => (
                <Form.Select
                    noLabel={true}
                    field={`type${index}`}
                    onChange={() => onTypeChange()}
                    filter={true}
                    style={{ width: "140px" }}
                    showClear>
                    {options.map((option, i) => (
                        <Form.Select.Option key={i} value={option.value}>
                            {option.label}
                        </Form.Select.Option>
                    ))}
                </Form.Select>
            ),
        },
        {
            title: t('metadata.column-table-column-length'),
            dataIndex: 'length',
            key: "length",
            render: (_: any, __: any, index: any) => {
                if (formApiRef.current) {
                    const typeValue = formApiRef.current.getValue(`type${index}`);
                    switch (typeValue) {
                        case 'CHAR':
                        case 'VARCHAR':
                        case 'BINARY':
                        case 'VARBINARY':
                        case 'TIME(precision)':
                        case 'TIMESTAMP(precision)':
                        case 'TIMESTAMP_WITH_LOCAL_TIME_ZONE(precision)':
                            return (
                                <Form.Input
                                    noLabel={true}
                                    field={`length${index}`}
                                    style={{ width: "120px" }}
                                    showClear />
                            );
                        case 'DECIMAL':
                            return (
                                <div style={{ display: 'flex', alignItems: 'center' }}>
                                    <Form.Input
                                        noLabel={true}
                                        field={`precision${index}`}
                                        style={{ width: "50px" }}/>
                                    <span style={{ margin: '0 5px' }}>-</span>
                                    <Form.Input
                                        noLabel={true}
                                        field={`scale${index}`}
                                        style={{ width: "50px" }}/>
                                </div>
                            );
                        default:
                            return null;
                    }
                }
                return null;
            },
        },
        {
            title: t('metadata.add-column-nullable'),
            dataIndex: 'nullable',
            key: 'nullable',
            width: 60,
            render: (_: any, __: any, index: any) => (
                <Form.Checkbox
                    field={`nullable${index}`}
                    noLabel={true}
                />
            ),
        },
        {
            title: t('metadata.add-column-default-value'),
            dataIndex: 'defaultValue',
            key: 'defaultValue',
            render: (_: any, __: any, index: any) => (
                <Form.Input
                    noLabel={true}
                    field={`defaultValue${index}`}
                    style={{ width: "120px" }}
                    showClear />
            ),
        },
        {
            title: t('metadata.add-column-comment'),
            dataIndex: 'comment',
            key: 'comment',
            render: (_: any, __: any, index: any) => (
                <Form.Input
                    noLabel={true}
                    field={`comment${index}`}
                    showClear />
            ),
        },
        {
            title: t('metadata.table-column-operation'),
            dataIndex: 'action',
            key: 'action',
            render: (_: any, __: any, index: any) => (
                <Button
                    onClick={() => handleRemoveInput(index)}
                    icon={<IconMinus />}
                    style={{ borderRadius: '50%' }}
                />
            ),
        },
    ];

    return(
        <>
            <Form
                initValues={{ nullable0: inputStates[0].nullable }}
                getFormApi={api => {
                    formApiRef.current = api;
                    if (getFormApi) {
                        getFormApi(api);
                    }
                }}
            >
                {
                    ({}) => (
                        <>
                            <Form.Section
                                text={
                                    <div
                                        style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', cursor: 'pointer',
                                            color: columnInputs.length > 0 ? 'rgba(var(--semi-grey-9), 1)' : 'lightgray',}}
                                        onClick={handleAddInput}
                                    >
                                        <IconPlus />
                                    </div>
                                }
                            >
                                <Table
                                    dataSource={columnInputs}
                                    columns={columns}
                                    pagination={false}
                                    size={"small"}
                                    key={tableKey}
                                />

                                <div style={{ textAlign: 'left', padding: '10px 0' }}>
                                    <span
                                        style={{ cursor: 'pointer', color: 'rgba(var(--semi-blue-5), 1)' }}
                                        onClick={handleAddInput}
                                    >
                                        {t('metadata.form-group-add-field')}
                                    </span>
                                </div>
                            </Form.Section>
                        </>
                    )
                }
            </Form>
        </>
    );
}

export default AddColumnForm;