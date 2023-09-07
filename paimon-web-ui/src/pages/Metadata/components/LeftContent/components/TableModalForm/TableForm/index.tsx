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

import {Button, Form, Table} from '@douyinfe/semi-ui';
import {useTranslation} from "react-i18next";
import {IconPlus, IconMinus} from "@douyinfe/semi-icons";
import {useCallback, useEffect, useRef, useState} from "react";
import {useTableStore} from "@src/store/tableStore.ts";
import options from "@utils/options.ts";

// @ts-ignore
const TableForm = ({ getFormApi }) => {
    const formApiRef = useRef<any>(null);
    const { t } = useTranslation();
    const { inputs, configs, setInputs, setConfigs } = useTableStore();
    const [fieldOptions, setFieldOptions] = useState<string[]>([]);
    const [tableKey, setTableKey] = useState(0);
    const [inputStates, setInputStates] = useState<{ nullable: boolean, primaryKey: boolean }[]>([{ nullable: true, primaryKey: false }]);

    const onTypeChange = () => {
        if (formApiRef.current) {
            // do something with typeValue...
            setTableKey(prevKey => prevKey + 1);
        }
    };

    const handleAddInput = () => {
        const newInputs = inputs.concat({});
        setInputs(newInputs);

       /* if (formApiRef.current) {
            formApiRef.current.setValue(`nullable${newInputs.length - 1}`, true);
        }*/

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
            const newInputs = [...inputs];
            formApiRef.current.setValue(`field${index}`, null);
            formApiRef.current.setValue(`comment${index}`, null);
            formApiRef.current.setValue(`type${index}`, null);
            newInputs.splice(index, 1);
            setInputs(newInputs);
            const newFieldOptions =
                newInputs.map((_, index) =>
                    formApiRef.current.getValue(`field${index}`));
            setFieldOptions(newFieldOptions);

            const newInputStates = [...inputStates];
            newInputStates.splice(index, 1);
            setInputStates(newInputStates);
        }
    }

    const updateFieldOptions = (index: any) => {
        if (formApiRef.current) {
            const fieldValue = formApiRef.current.getValue(`field${index}`);
            if (fieldValue !== undefined) {
                const newFieldOptions = [...fieldOptions];
                newFieldOptions[index] = fieldValue;
                setFieldOptions(newFieldOptions);
            }
        }
    };

    const handleAddConfig = () => {
        setConfigs(configs.concat({}));
    }

    const handleRemoveConfig = (index: any) => {
        if (formApiRef.current) {
            const newConfigs = [...configs];
            formApiRef.current.setValue(`configKey${index}`, null);
            formApiRef.current.setValue(`configValue${index}`, null);
            newConfigs.splice(index, 1);
            setConfigs(newConfigs);
        }
    }

    const handlePrimaryKeyChange = useCallback((event: any, index: any) => {
        const newInputStates = [...inputStates];
        newInputStates[index] = {
            ...newInputStates[index],
            primaryKey: event.target.checked,
            nullable: !event.target.checked
        };
        setInputStates(newInputStates);
        formApiRef.current.setValue(`nullable${index}`, !event.target.checked);
    }, [inputStates, formApiRef]);

    const handleNullableChange = useCallback((event: any, index: any) => {
        if (!inputStates[index]?.primaryKey) {
            const newInputStates = [...inputStates];
            newInputStates[index] = {
                ...newInputStates[index],
                nullable: event.target.checked,
            };
            setInputStates(newInputStates);
            formApiRef.current.setValue(`nullable${index}`, event.target.checked);
        }
    }, [inputStates, formApiRef]);


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
                    onBlur={() => updateFieldOptions(index)}
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
            title: t('metadata.add-column-primary-key'),
            dataIndex: 'primaryKey',
            key: 'primaryKey',
            width: 60,
            render: (_: any, __: any, index: any) => (
                <Form.Checkbox
                    field={`primaryKey${index}`}
                    noLabel={true}
                    checked={inputStates[index]?.primaryKey || false}
                    onChange={(event) => handlePrimaryKeyChange(event, index)}
                />
            ),
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
                    checked={inputStates[index]?.nullable || false}
                    disabled={inputStates[index]?.primaryKey || false}
                    onChange={(event) => handleNullableChange(event, index)}
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
                            <Form.Section text={t('metadata.form-group-basic-information')}>
                                <Form.Input
                                    field="tableName"
                                    label={t('metadata.table-name')}
                                    trigger='blur'
                                    rules={[
                                        { required: true, message: t('metadata.message') },
                                    ]}
                                    style={{ width: "100%" }}
                                    showClear/>
                                <Form.TextArea
                                    field="description"
                                    label={t('metadata.description')}
                                    style={{ width: "100%" }}
                                    placeholder={t('metadata.text-area-description')}
                                    autosize rows={2}
                                    showClear/>
                            </Form.Section>

                            <Form.Section
                                text={
                                    <div
                                        style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', cursor: 'pointer',
                                            color: inputs.length > 0 ? 'rgba(var(--semi-grey-9), 1)' : 'lightgray',}}
                                        onClick={handleAddInput}
                                    >
                                        <span>{t('metadata.form-group-add-column-ordinary')}</span>
                                        <IconPlus />
                                    </div>
                                }
                            >
                                <Table
                                    dataSource={inputs}
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

                            <Form.Section text={t('metadata.form-group-add-column-partition')}>
                                <Form.Select
                                    noLabel={true}
                                    field="partitionKey"
                                    style={{ width: "100%" }}
                                    filter={true}
                                    showClear
                                    multiple
                                >
                                    {fieldOptions.map((field, index) => (
                                        <Form.Select.Option key={index} value={field}>{field}</Form.Select.Option>
                                    ))}
                                </Form.Select>
                            </Form.Section>

                            <Form.Section
                                text={
                                    <div
                                        style={{
                                            display: 'flex', justifyContent: 'space-between', alignItems: 'center',
                                            cursor: 'pointer',
                                            color: configs.length > 0 ? 'black' : 'lightgray',
                                            }}
                                        onClick={handleAddConfig}
                                    >
                                        <span>{t('metadata.form-group-add-config')}</span>
                                        <IconPlus />
                                    </div>
                                }
                            >
                                {configs.map((_, index) => (
                                    <div style={{ display: 'flex', alignItems: 'center', marginBottom: '10px' }} key={index}>
                                        <Form.Input
                                            noLabel={true}
                                            field={`configKey${index}`}
                                            placeholder={t('metadata.add-config-key')}
                                            style={{ width: "499px"}}
                                            showClear />
                                        <Form.Input
                                            noLabel={true}
                                            field={`configValue${index}`}
                                            placeholder={t('metadata.add-config-value')}
                                            style={{ width: "499px", marginLeft: '10px' }}
                                            showClear />
                                        <Button
                                            onClick={() => handleRemoveConfig(index)}
                                            icon={<IconMinus />}
                                            style={{ marginLeft: '10px', borderRadius: '50%'}}
                                        />
                                    </div>
                                ))}
                            </Form.Section>
                        </>
                    )
                }
            </Form>
        </>
    );
}

export default TableForm;