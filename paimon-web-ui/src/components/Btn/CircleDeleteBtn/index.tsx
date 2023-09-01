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

import { DeleteOutlined } from '@ant-design/icons';
import {Button, Popconfirm} from "@douyinfe/semi-ui";
import { useTranslation } from 'react-i18next';
import styles from "./circle-delete-btn.module.less";

const CircleDeleteBtn = ({ onConfirm }: { onConfirm?: () => void; id: string }) => {
    const { t } = useTranslation()
    return (
        <Popconfirm
            title={t('component.delete-btn-confirm-title')}
            content={t('component.delete-btn-confirm-content')}
            onConfirm={onConfirm}
            cancelText={t('metadata.cancel')}
            okText={t('metadata.submit')}
            style={{ width: 320 }}
            position={"topRight"}
        >
            <Button
                className={styles['delete-btn']}
                icon={<DeleteOutlined style={{ color: '#000', fontSize: '16px' }} />}
            />
        </Popconfirm>
    );
};

export default CircleDeleteBtn;