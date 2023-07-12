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

import { useEffect } from 'react';
import {useLocalStorage} from "@src/hooks/localStorage";

const useThemeSwitcher = () => {
    const [dark, setDark] = useLocalStorage("dark", false);

    const switchMode = () => {
        const body = document.body;
        if (body.hasAttribute('theme-mode')) {
            body.removeAttribute('theme-mode');
            const updatedValue = !dark;
            setDark(updatedValue);
            console.log("白")
        } else {
            body.setAttribute('theme-mode', 'dark');
            setDark(!dark);
            console.log("黑")
        }
    };

    useEffect(() => {
        const body = document.body;
        if (dark) {
            body.setAttribute('theme-mode', 'dark');
        } else {
            body.removeAttribute('theme-mode');
        }
    }, [dark]);

    return { dark, switchMode };
};

export default useThemeSwitcher;
