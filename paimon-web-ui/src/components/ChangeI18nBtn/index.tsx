import { useMemo, useState } from "react";
import {Button, Tooltip} from "@douyinfe/semi-ui";
import {IconLanguage} from "@douyinfe/semi-icons";
import i18n from "i18next";


const ChangeI18nBtn = () => {

    const [preBtnName, setPreBtnName] = useState<string>()
    const [changeName, setChangeName] = useState<string>()

    useMemo(() => {
        console.log('useMemO:', i18n.language)
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
        console.log('swith....', preBtnName)
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
