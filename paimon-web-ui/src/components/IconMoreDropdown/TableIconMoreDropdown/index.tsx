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

import { IconMore, IconDelete, IconEdit } from "@douyinfe/semi-icons";
import { Dropdown, Popconfirm } from '@douyinfe/semi-ui';
import { useTranslation } from 'react-i18next';
import {useEffect, useState} from "react";

const TableIconMoreDropdown = ({ onConfirm, onRename }: { onConfirm?: () => void; onRename?: () => void; id: string }) => {
    const { t } = useTranslation();
    const [dropdownVisible, setDropdownVisible] = useState(false);
    const [popconfirmVisible, setPopconfirmVisible] = useState(false);

    useEffect(() => {
        const handleClickOutside = () => {
            if (dropdownVisible || popconfirmVisible) {
                setDropdownVisible(false);
                setPopconfirmVisible(false);
            }
        };

        document.addEventListener('mousedown', handleClickOutside);
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, [dropdownVisible, popconfirmVisible]);

    const handleRename = (e: any) => {
        e.stopPropagation();
        onRename && onRename();
    };

    const handleDelete = (e: any) => {
        e.stopPropagation();
        setPopconfirmVisible(true);
    };

    return (
        <>
            <Dropdown
                trigger={'click'}
                position={'bottomLeft'}
                visible={dropdownVisible}
                onVisibleChange={setDropdownVisible}
                render={
                    <Dropdown.Menu>
                        <Dropdown.Item onClick={handleRename}>
                            <IconEdit/> {t('component.icon-more-dropdown-rename')}
                        </Dropdown.Item>
                        <Dropdown.Item onClick={handleDelete}>
                            <IconDelete/>
                            {t('component.icon-more-dropdown-delete')}
                        </Dropdown.Item>
                    </Dropdown.Menu>
                }
            >
                <IconMore onClick={(e) => e.stopPropagation()}/>
            </Dropdown>
            <Popconfirm
                title={t('component.delete-btn-confirm-title')}
                content={t('component.delete-btn-confirm-content')}
                onConfirm={onConfirm}
                cancelText={t('metadata.cancel')}
                okText={t('metadata.submit')}
                style={{ width: 320 }}
                position={"topLeft"}
                visible={popconfirmVisible}
                onVisibleChange={setPopconfirmVisible}
            />
        </>
    );
}

export default TableIconMoreDropdown;