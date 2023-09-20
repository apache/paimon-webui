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

export type Theme = 'vs' | 'vs-dark'
export type FoldingStrategy = 'auto' | 'indentation'
export type RenderLineHighlight = 'all' | 'line' | 'none' | 'gutter'
export interface Options {
  automaticLayout?: boolean // 自适应布局
  foldingStrategy?: FoldingStrategy // 折叠方式  auto | indentation
  renderLineHighlight?: RenderLineHighlight // 行亮
  selectOnLineNumbers?: boolean // 显示行号
  minimap?: {
    // 关闭小地图
    enabled: boolean
  }
  readOnly: boolean // 只读
  contextmenu: boolean
  fontSize?: number // 字体大小
  scrollBeyondLastLine?: boolean // 取消代码后面一大段空白
  overviewRulerBorder?: boolean // 不要滚动条的边框
}

export const editorProps = {
  modelValue: {
    type: String as PropType<string>,
    default: null
  },
  width: {
    type: [String, Number] as PropType<string | number>,
    default: '100%'
  },
  height: {
    type: [String, Number] as PropType<string | number>,
    default: '100%'
  },
  language: {
    type: String as PropType<string>,
    default: 'javascript'
  },
  theme: {
    type: String as PropType<Theme>,
    validator(value: string): boolean {
      return ['vs', 'vs-dark'].includes(value)
    },
    default: 'vs'
  },
  options: {
    type: Object as PropType<Options>,
    default() {
      return {
        automaticLayout: true,
        foldingStrategy: 'indentation',
        renderLineHighlight: 'line',
        selectOnLineNumbers: true,
        minimap: {
          enabled: true
        },
        readOnly: false,
        contextmenu: true,
        fontSize: 16,
        scrollBeyondLastLine: false,
        overviewRulerBorder: false
      }
    }
  }
}