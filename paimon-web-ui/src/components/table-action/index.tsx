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

import { CreateOutline, PlayOutline, TrashOutline } from "@vicons/ionicons5"

const props = {
  row: {
    type: Object as PropType<any>,
    default: {}
  }
}

export default defineComponent({
  name: 'TableAction',
  props,
  emits: ['handleEdit', 'handleRun', 'handleDelete'],
  setup(props, { emit }) {
    const { t } = useLocaleHooks()

    const handleEdit = (row: any) => {
      emit('handleEdit', row)
    }

    const handleRun = (row: any) => {
      emit('handleRun', row)
    }

    const handleDelete = (row: any) => {
      emit('handleDelete', row)
    }

    return {
      t,
      handleEdit,
      handleRun,
      handleDelete
    }
  },
  render() {
    return (
      <n-space>
        <n-tooltip trigger={'hover'}>
          {{
            default: () => this.t('cdc.edit'),
            trigger: () => (
              <n-button
                size='small'
                type='primary'
                onClick={() =>
                  this.handleEdit(this.row)
                }
                circle
              >
                <n-icon component={CreateOutline} />
              </n-button>
            )
          }}
        </n-tooltip>
        <n-tooltip trigger={'hover'}>
          {{
            default: () => this.t('cdc.run'),
            trigger: () => (
              <n-button
                size='small'
                type='primary'
                onClick={() =>
                  this.handleRun(this.row)
                }
                circle
              >
                <n-icon component={PlayOutline} />
              </n-button>
            )
          }}
        </n-tooltip>
        <n-tooltip trigger={'hover'}>
          {{
            default: () => this.t('cdc.delete'),
            trigger: () => (
              <n-button
                size='small'
                type='error'
                onClick={() =>
                  this.handleDelete(this.row)
                }
                circle
              >
                <n-icon component={TrashOutline} />
              </n-button>
            )
          }}
        </n-tooltip>
      </n-space>
    )
  }
})
