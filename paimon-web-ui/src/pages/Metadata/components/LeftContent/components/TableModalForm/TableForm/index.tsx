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

import {Button, Form} from '@douyinfe/semi-ui';
import {useTranslation} from "react-i18next";
import {IconPlus, IconMinus} from "@douyinfe/semi-icons";
import {useRef, useState} from "react";
import {useTableStore} from "@src/store/tableStore.ts";

// @ts-ignore
const TableForm = ({ getFormApi }) => {
    const formApiRef = useRef<any>(null);
    const { t } = useTranslation();
    const { inputs, configs, setInputs, setConfigs } = useTableStore();
    const [fieldOptions, setFieldOptions] = useState<string[]>([]);

    const handleAddInput = () => {
        const newInputs = inputs.concat({});
        setInputs(newInputs);
    }

    const handleRemoveInput = (index: any) => {
        if (formApiRef.current) {
            const newInputs = [...inputs];
            formApiRef.current.setValue(`field${index}`, null);
            formApiRef.current.setValue(`comment${index}`, null);
            formApiRef.current.setValue(`type${index}`, null);
            newInputs.splice(index, 1);
            setInputs(newInputs);
            const newFieldOptions = newInputs.map((_, index) => formApiRef.current.getValue(`field${index}`));
            setFieldOptions(newFieldOptions);
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

    const options = [
        { value: 'INT', label: 'INT' },
        { value: 'TINYINT', label: 'TINYINT' },
        { value: 'SMALLINT', label: 'SMALLINT' },
        { value: 'BIGINT', label: 'BIGINT' },
        { value: 'STRING', label: 'STRING' },
        { value: 'DOUBLE', label: 'DOUBLE' },
        { value: 'BOOLEAN', label: 'BOOLEAN' },
        { value: 'DATE', label: 'DATE' },
        { value: 'TIME', label: 'TIME' },
        { value: 'TIMESTAMP', label: 'TIMESTAMP' },
        { value: 'BYTES', label: 'BYTES' },
        { value: 'FLOAT', label: 'FLOAT' },
        { value: 'DECIMAL', label: 'DECIMAL' },
    ];

    return(
        <>
            <Form
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
                                            color: inputs.length > 0 ? 'black' : 'lightgray',}}
                                        onClick={handleAddInput}
                                    >
                                        <span>{t('metadata.form-group-add-column-ordinary')}</span>
                                        <IconPlus />
                                    </div>
                                }
                            >
                                {inputs.map((_, index) => (
                                    <div style={{ display: 'flex', alignItems: 'center', marginBottom: '10px'}} key={index}>
                                        <Form.Input
                                            noLabel={true}
                                            field={`field${index}`}
                                            placeholder={t('metadata.add-column-field')}
                                            style={{ width: "160px" }}
                                            onBlur={() => updateFieldOptions(index)}
                                            showClear />
                                        <Form.Select
                                            noLabel={true}
                                            field={`type${index}`}
                                            placeholder={t('metadata.add-column-type')}
                                            style={{ width: "130px", marginLeft: '10px' }}
                                            allowCreate={true}
                                            filter={true}
                                            showClear>
                                            {options.map((option, i) => (
                                                <Form.Select.Option key={i} value={option.value}>
                                                    {option.label}
                                                </Form.Select.Option>
                                            ))}
                                        </Form.Select>
                                        <Form.Input
                                            noLabel={true}
                                            field={`comment${index}`}
                                            placeholder={t('metadata.add-column-comment')}
                                            style={{ width: "160px", marginLeft: '10px' }}
                                            showClear />
                                        <Form.Checkbox
                                            field={`primaryKey${index}`}
                                            noLabel={true}
                                            style={{marginLeft: '10px'}}>
                                                {t('metadata.add-column-primary-key')}
                                        </Form.Checkbox>
                                        <Form.Input
                                            noLabel={true}
                                            field={`defaultValue${index}`}
                                            placeholder={t('metadata.add-column-default-value')}
                                            style={{ width: "126px", marginLeft: '10px'}}
                                            showClear />
                                        <Button
                                            onClick={() => handleRemoveInput(index)}
                                            icon={<IconMinus />}
                                            style={{ marginLeft: '10px', borderRadius: '50%' }}
                                        />
                                    </div>
                                ))}
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
                                            style={{ width: "329px"}}
                                            showClear />
                                        <Form.Input
                                            noLabel={true}
                                            field={`configValue${index}`}
                                            placeholder={t('metadata.add-config-value')}
                                            style={{ width: "329px", marginLeft: '10px' }}
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