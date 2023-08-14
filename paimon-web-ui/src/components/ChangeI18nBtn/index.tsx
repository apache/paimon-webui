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

import { useMemo, useState } from "react";
import {Button, Tooltip} from "@douyinfe/semi-ui";
import {IconLanguage} from "@douyinfe/semi-icons";
import i18n from "i18next";


const ChangeI18nBtn = () => {

    const [preBtnName, setPreBtnName] = useState<string>()
    const [changeName, setChangeName] = useState<string>()

    useMemo(() => {
        switch (i18n.language) {
            case 'zh-CN':
                setPreBtnName('EN')
                setChangeName('Switch to English')
                break
            case 'en':
                setPreBtnName('中文')
                setChangeName('切换中文')
        }
        return ''
    }, [])

    const switchEnCh = () => {
        if(preBtnName === 'EN') {
            i18n.changeLanguage('en')
            setPreBtnName('中文')
            setChangeName('切换中文')
        }
        if (preBtnName === '中文') {
            i18n.changeLanguage('zh-CN')
            setPreBtnName('EN')
            setChangeName('Switch to English')
        }
    }


    return (
        <Tooltip content={changeName}
                 position='bottom'
        >
            <Button
                theme="borderless"
                icon={<IconLanguage size="extra-large" />}
                style={{
                    color: 'var(--semi-color-text-2)',
                    marginRight: '12px',
                }}
                aria-label={changeName}
                onClick={switchEnCh}
            >{preBtnName}</Button>
        </Tooltip>
    )
}

export default ChangeI18nBtn
