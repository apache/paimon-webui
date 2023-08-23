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

import { Form } from '@douyinfe/semi-ui';
import {useTranslation} from "react-i18next";
import {useCatalogStore} from "@src/store/catalogStore.ts";

// @ts-ignore
const DatabaseForm = ({ getFormApi }) => {
    const { t } = useTranslation();
    const catalogItemList = useCatalogStore(state => state.catalogItemList);

    return(
        <>
            <Form
                getFormApi={getFormApi}
            >
                {
                    ({}) => (
                        <>
                            <Form.Input
                                field="databaseName"
                                label={t('metadata.database-name')}
                                trigger='blur'
                                rules={[
                                    { required: true, message: t('metadata.message') },
                                ]}
                                style={{ width: "100%" }}
                                showClear/>

                            <Form.Select
                                field="catalogId"
                                label='Catalog'
                                rules={[
                                    { required: true, message: t('metadata.message') },
                                ]}
                                style={{ width: "100%" }}
                                showClear>
                                {catalogItemList.map(item => (
                                    <Form.Select.Option key={item.id} value={item.id}>
                                        {item.catalogName}
                                    </Form.Select.Option>
                                ))}
                            </Form.Select>

                            <Form.TextArea
                                field="description"
                                label={t('metadata.description')}
                                style={{ width: "100%" }}
                                placeholder={t('metadata.text-area-description')}
                                showClear/>
                        </>
                    )
                }
            </Form>
        </>
    );
}

export default DatabaseForm;
