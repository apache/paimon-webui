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

import * as monaco from 'monaco-editor'
import jsonWorker from 'monaco-editor/esm/vs/language/json/json.worker?worker'
import cssWorker from 'monaco-editor/esm/vs/language/css/css.worker?worker'
import htmlWorker from 'monaco-editor/esm/vs/language/html/html.worker?worker'
import tsWorker from 'monaco-editor/esm/vs/language/typescript/ts.worker?worker'
import EditorWorker from 'monaco-editor/esm/vs/editor/editor.worker?worker'
import { editorProps } from './type'
import { useConfigStore } from '@/store/config'
import { format } from 'sql-formatter'

// @ts-ignore: worker
self.MonacoEnvironment = {
  getWorker(_: string, label: string) {
    if (label === 'json') {
      return new jsonWorker()
    }
    if (['css', 'scss', 'less'].includes(label)) {
      return new cssWorker()
    }
    if (['html', 'handlebars', 'razor'].includes(label)) {
      return new htmlWorker()
    }
    if (['typescript', 'javascript'].includes(label)) {
      return new tsWorker()
    }
    return new EditorWorker()
  }
}

export default defineComponent({
  name: 'MonacoEditor',
  props: editorProps,
  emits: ['update:modelValue', 'change', 'EditorMounted', 'EditorSave'],
  setup(props, { emit }) {
    const configStore = useConfigStore()
    const monacoEditorThemeRef = ref(configStore.getCurrentTheme === 'dark' ? 'vs-dark' : 'vs')
    let editor: monaco.editor.IStandaloneCodeEditor
    const codeEditBox = ref()
    const init = () => {
      monaco.languages.typescript.javascriptDefaults.setDiagnosticsOptions({
        noSemanticValidation: true,
        noSyntaxValidation: false
      })
      monaco.languages.typescript.javascriptDefaults.setCompilerOptions({
        target: monaco.languages.typescript.ScriptTarget.ES2020,
        allowNonTsExtensions: true
      })
      monaco.languages.registerCompletionItemProvider('sql', {
        provideCompletionItems: function(model, position) {
          const word = model.getWordUntilPosition(position);
          const range = {
            startLineNumber: position.lineNumber,
            endLineNumber: position.lineNumber,
            startColumn: word.startColumn,
            endColumn: word.endColumn
          };
          const suggestions = [];
          const sqlStr = ['select','from','where','and','or','limit','order by','group by'];
            for(const i in sqlStr){
              suggestions.push({
                label: sqlStr[i],
                kind: monaco.languages.CompletionItemKind['Function'],
                insertText: sqlStr[i],
                detail: '',
                range:range
              });
            }
          return {
            suggestions: suggestions
          };
        },
      });

      editor = monaco.editor.create(codeEditBox.value, {
        value: props.modelValue,
        language: props.language,
        theme: monacoEditorThemeRef.value,
        ...props.options
      })

      editor.addCommand(monaco.KeyMod.CtrlCmd | monaco.KeyCode.KeyS, function () {
        emit('EditorSave')
      })

      editor.setValue(format(toRaw(editor).getValue()))
      
      editor.onDidChangeModelContent(() => {
        const value = editor.getValue()
        emit('update:modelValue', value)
        emit('change', value)
      })
      emit('EditorMounted', editor)
    }
    watch(
      () => props.modelValue,
      newValue => {
        if (editor) {
          const value = editor.getValue()
          if (newValue !== value) {
            editor.setValue(newValue)
            editor.setValue(format(toRaw(editor).getValue()))
          }
        }
      }
    )
    watch(
      () => props.options,
      newValue => {
        editor.updateOptions(newValue)
      },
      { deep: true }
    )
    watch(
      () => props.language,
      newValue => {
        monaco.editor.setModelLanguage(editor.getModel()!, newValue)
      }
    )
    watch(
      () => configStore.getCurrentTheme,
      () => {
        editor?.dispose()
        monacoEditorThemeRef.value = configStore.getCurrentTheme === 'dark' ? 'vs-dark' : 'vs'
        init()
      }
    )
    
    onBeforeUnmount(() => {
      editor.dispose()
    })
    onMounted(() => {
      init()
    })
    return { codeEditBox }
  },
  render () {
    return (
      <div ref='codeEditBox' style={{
        height: '100%',
      }}/>
    )
  }
})
