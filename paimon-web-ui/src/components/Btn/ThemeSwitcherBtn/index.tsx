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

import { useMemo } from "react";
import {Button, Tooltip} from "@douyinfe/semi-ui";
import {IconMoon, IconSun} from "@douyinfe/semi-icons";
import useThemeSwitcher from '@utils/mode.ts';
import i18n from 'i18next';

const ThemeSwitcherBtn = () => {
    const {dark, switchMode } = useThemeSwitcher();

    const [tooltipContent, icon] = useMemo(() => {
        let tooltipContent;
        if(i18n.language === 'zh-CN') {
            tooltipContent = dark ? '切换到亮色模式' : '切换到暗色模式';
        } else {
            tooltipContent = dark ? 'Switch to Light Mode' : 'Switch to Dark Mode';
        }
        const icon = dark ? <IconSun size={"extra-large"}/> : <IconMoon size={"extra-large"}/>;
        return [tooltipContent, icon];
    }, [dark, i18n.language]);

    return (
        <Tooltip content={tooltipContent} position='bottom'>
            <Button
                theme="borderless"
                icon={icon}
                style={{
                    color: 'var(--semi-color-text-2)',
                    marginRight: '12px',
                }}
                aria-label={tooltipContent}
                onClick={switchMode}
            />
        </Tooltip>
    )
}

export default ThemeSwitcherBtn;