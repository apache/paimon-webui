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

import { create } from 'zustand';

type ThemeStore = {
    dark: boolean;
    switchMode: () => void;
};

export const useThemeStore = create<ThemeStore>((set) => ({
    dark: false,
    switchMode: () => set((state) => {
        const updatedValue = !state.dark;
        const body = document.body;

        if (updatedValue) {
            body.setAttribute('theme-mode', 'dark');
        } else {
            body.removeAttribute('theme-mode');
        }

        return { dark: updatedValue };
    }),
}));