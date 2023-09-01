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

import {Form} from "@douyinfe/semi-ui";
import {useTranslation} from "react-i18next";
import options from "@utils/options.ts";


// @ts-ignore
const EditColumnForm = ({ getFormApi, selectedColumnName, selectedColumnDataType, selectedColumnDescription}) => {
    const { t } = useTranslation();

    return(
        <>
            <Form
                getFormApi={getFormApi}
                initValues={{
                    field: selectedColumnName,
                    type: selectedColumnDataType,
                    description: selectedColumnDescription
                }}
            >
                {
                    ({}) => (
                        <>
                            <Form.Input
                                field="field"
                                label={t('metadata.column-table-column-name')}
                                trigger='blur'
                                rules={[
                                    { required: true, message: t('metadata.message') },
                                ]}
                                style={{ width: "100%" }}
                                showClear
                            />

                            <Form.Select
                                field="type"
                                label={t('metadata.column-table-column-type')}
                                style={{ width: "100%" }}
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

                            <Form.TextArea
                                field="description"
                                label={t('metadata.column-table-column-description')}
                                style={{ width: "100%" }}
                                showClear
                            />
                        </>
                    )
                }
            </Form>
        </>
    );
}

export default EditColumnForm;