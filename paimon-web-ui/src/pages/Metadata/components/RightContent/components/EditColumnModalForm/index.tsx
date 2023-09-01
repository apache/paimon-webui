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

import React, {useState} from "react";
import { Modal } from '@douyinfe/semi-ui';
import {useTranslation} from "react-i18next";
import {auto} from "@popperjs/core";
import EditColumnForm from "@pages/Metadata/components/RightContent/components/EditColumnModalForm/EditColumnForm";

type EditColumnModalFormProps = {
    visible: boolean;
    onClose: () => void;
    onOk: (formApi: any) => void;
    selectedColumnName: string | null;
    selectedColumnDataType: string | null;
    selectedColumnDescription: string | null;
};

const EditColumnModalForm: React.FC<EditColumnModalFormProps> =
    ({ visible , onClose, onOk, selectedColumnName, selectedColumnDataType, selectedColumnDescription  }) => {
    const [formApi, setFormApi] = useState(null);
    const { t } = useTranslation();

    const getFormApi = (api: any) => {
        setFormApi(api);
    };

    const handleOkClick = async () => {
        await onOk(formApi);
        onClose();
    };

    return(
        <Modal
            title = {t('metadata.modify-column')}
            visible = {visible}
            onCancel= {onClose}
            maskClosable={false}
            okText={t('metadata.modify')}
            cancelText={t('metadata.cancel')}
            width={'562px'}
            height={auto}
            onOk={() => handleOkClick()}
        >
            <EditColumnForm
                getFormApi={getFormApi}
                selectedColumnName={selectedColumnName}
                selectedColumnDataType={selectedColumnDataType}
                selectedColumnDescription={selectedColumnDescription}
            />
        </Modal>
    );
}

export default EditColumnModalForm;