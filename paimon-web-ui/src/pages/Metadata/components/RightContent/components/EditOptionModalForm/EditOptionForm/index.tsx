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


// @ts-ignore
const EditOptionForm = ({ getFormApi, selectedOptionKey, selectedOptionValue}) => {
    const { t } = useTranslation();

    return(
        <>
            <Form
                getFormApi={getFormApi}
                initValues={{
                    key: selectedOptionKey,
                    value: selectedOptionValue
                }}
            >
                {
                    ({}) => (
                        <>
                            <Form.Input
                                field="key"
                                label={t('metadata.add-config-key')}
                                style={{ width: "100%" }}
                                disabled={true}
                            />

                            <Form.Input
                                field="value"
                                label={t('metadata.add-config-value')}
                                trigger='blur'
                                rules={[
                                    { required: true, message: t('metadata.message') },
                                ]}
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

export default EditOptionForm;