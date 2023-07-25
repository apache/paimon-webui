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

import {Editor as MonacoEditor} from "@monaco-editor/react";

import React from "react";
import useThemeSwitcher from "@utils/mode.ts";

export type EditorProps = {
    value?: string;
    theme?: string;
}
const Editor:React.FC<EditorProps> = (props) => {
    const {dark} = useThemeSwitcher();

    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    const handleEditorChange = (value, event) => {
        //console.log(value);
    };

    const editorOptions = {
        selectOnLineNumbers: true,
    };

    return (
        <div>
            <MonacoEditor
                value={props.value}
                width="auto"
                height="500px"
                language="java"
                theme={dark ? 'vs-dark' : 'light'}
                options={editorOptions}
                onChange={handleEditorChange}
            />
        </div>
    );
};

export default Editor;

