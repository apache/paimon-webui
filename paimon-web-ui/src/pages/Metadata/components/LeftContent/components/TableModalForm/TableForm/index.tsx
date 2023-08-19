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

// @ts-ignore
const TableForm = ({ getFormApi }) => {
    const formApiRef = useRef<any>(null);
    const { t } = useTranslation();
    const [inputs, setInputs] = useState([{}]);
    const [configs, setConfigs] = useState<Array<{}>>([]);

    const handleAddInput = () => {
        setInputs(inputs.concat({})); // Add a new input to our array
    }

    const handleRemoveInput = (index: any) => {
        if (formApiRef.current) {
            const newInputs = [...inputs];
            formApiRef.current.setValue(`field${index}`, null);
            formApiRef.current.setValue(`comment${index}`, null);
            formApiRef.current.setValue(`type${index}`, null);
            newInputs.splice(index, 1);
            setInputs(newInputs);
        }
    }

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
                                        <span>{t('metadata.form-group-add-column')}</span>
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
                                            style={{ width: "276px" }}
                                            showClear />
                                        <Form.Select
                                            noLabel={true}
                                            field={`type${index}`}
                                            placeholder={t('metadata.add-column-type')}
                                            style={{ width: "156px", marginLeft: '10px' }}
                                            showClear>
                                            <Form.Select.Option value={"STRING"}>STRING</Form.Select.Option>
                                            <Form.Select.Option value={"INT"}>INT</Form.Select.Option>
                                        </Form.Select>
                                        <Form.Input
                                            noLabel={true}
                                            field={`comment${index}`}
                                            placeholder={t('metadata.add-column-comment')}
                                            style={{ width: "276px", marginLeft: '10px' }}
                                            showClear />
                                        <Button
                                            onClick={() => handleRemoveInput(index)}
                                            icon={<IconMinus />}
                                            style={{ marginLeft: '10px', borderRadius: '50%' }}
                                        />
                                    </div>
                                ))}
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
                                            style={{ width: "359px"}}
                                            showClear />
                                        <Form.Input
                                            noLabel={true}
                                            field={`configValue${index}`}
                                            placeholder={t('metadata.add-config-value')}
                                            style={{ width: "359px", marginLeft: '10px' }}
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