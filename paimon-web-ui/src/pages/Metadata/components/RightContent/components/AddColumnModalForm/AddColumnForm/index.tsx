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

import {Button, Form} from "@douyinfe/semi-ui";
import {IconMinus, IconPlus} from "@douyinfe/semi-icons";
import options from "@utils/options.ts";
import {useRef} from "react";
import {useTranslation} from "react-i18next";
import {useTableStore} from "@src/store/tableStore.ts";

// @ts-ignore
const AddColumnForm = ({ getFormApi }) => {
    const formApiRef = useRef<any>(null);
    const { t } = useTranslation();
    const { columnInputs,setColumnInputs,  } = useTableStore();

    const handleAddInput = () => {
        const newInputs = columnInputs.concat({});
        setColumnInputs(newInputs);
    }

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
                                {columnInputs.map((_, index) => (
                                    <div style={{ display: 'flex', alignItems: 'center', marginBottom: '10px'}} key={index}>
                                        <Form.Input
                                            noLabel={true}
                                            field={`field${index}`}
                                            placeholder={t('metadata.add-column-field')}
                                            style={{ width: "160px" }}
                                            rules={[
                                                { required: true, message: t('metadata.message') },
                                            ]}
                                            showClear />
                                        <Form.Select
                                            noLabel={true}
                                            field={`type${index}`}
                                            placeholder={t('metadata.add-column-type')}
                                            style={{ width: "130px", marginLeft: '10px' }}
                                            allowCreate={true}
                                            filter={true}
                                            rules={[
                                                { required: true, message: t('metadata.message') },
                                            ]}
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
                                            style={{ width: "200px", marginLeft: '10px' }}
                                            showClear />
                                        <Button
                                            onClick={() => handleRemoveInput(index)}
                                            icon={<IconMinus />}
                                            style={{ marginLeft: '10px', borderRadius: '50%' }}
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

export default AddColumnForm;