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

import styles from './index.module.scss';
import type { TabsProps } from 'naive-ui';
import { Layers, CodeSlashSharp, SyncCircleOutline } from '@vicons/ionicons5';
import CataLog from './components/catalog';
import * as monaco from 'monaco-editor'
import MonacoEditor from '@/components/monaco-editor';


export default defineComponent({
  name: 'PlaygroundPage',
  setup() {
    const type = ref<TabsProps['type']>('bar')

    const content = ref('')
    const language = ref('javascript')
    const editorMounted = (editor: monaco.editor.IStandaloneCodeEditor) => {
      console.log('editor实例加载完成', editor)
    }

    return {
      type,
      content,
      language,
      editorMounted
    }
  },
  render() {
    return (
      <div class={styles.container}>
        <n-tabs
          type={this.type}
          animated
          placement="left"
          default-value="oasis"
        >
          <n-tab-pane name="oasis"
            v-slots={{
              tab: () => (
                <n-icon size="24">
                  <Layers />
                </n-icon>
              )
            }}
          >
            <div class={styles.content}>
              <div class={styles.catalog}>
                <CataLog />
              </div>
              <div class={styles.editor}>
                <MonacoEditor
                  v-model={this.content}
                  language={this.language}
                  onEditorMounted={this.editorMounted}
                />
              </div>
            </div>
          </n-tab-pane>
          <n-tab-pane name="the beatles" tab="the Beatles"
            v-slots={{
              tab: () => (
                <n-icon size="24">
                  <CodeSlashSharp />
                </n-icon>
              )
            }}
          >
            Saved Queries
          </n-tab-pane>
          <n-tab-pane name="jay chou" tab="Jay Chou"
            v-slots={{
              tab: () => (
                <n-icon size="24">
                  <SyncCircleOutline />
                </n-icon>
              )
            }}
          >
            History
          </n-tab-pane>
        </n-tabs>
      </div>
    );
  },
});
