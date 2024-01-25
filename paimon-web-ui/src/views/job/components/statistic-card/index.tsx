/* Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
'License'); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
'AS IS' BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License. */

import { type JobCardProps, JobMapping, JobStatusProps } from '../../constant'

import styles from './index.module.scss'


const IconSize = {
  TOTAL: 30,
  RUNNING: 40,
  FINISH: 40,
  CANCEL: 40,
  FAIL: 30
}

export default defineComponent({
  name: 'JobStatisticCard',
  props: {
    type: {
      type: String as PropType<JobCardProps>,
      required: true,
    },
    value: {
      type: Number,
      required: true,
    },
  },
  setup(props) {
    const { t } = useLocaleHooks()

    return {
      t,
      ...toRefs(props),
    }
  },
  render() {
    return (
      <n-card>
        <n-space align='center' justify='start' size={30}>
          <div>
            <n-icon-wrapper color={JobStatusProps[this.type].bgColor} icon-color={JobStatusProps[this.type].primaryColor} size={54} border-radius={27}>
              <n-icon size={IconSize[this.type]}>
                {{
                  default: () => JobStatusProps[this.type].icon
                }}
              </n-icon>
            </n-icon-wrapper>
          </div>
          <div>
            <div>{this.t(`job.${JobMapping[this.type]}`)}</div>
            <div class={styles.value}>{this.value}</div>
          </div>
        </n-space>
      </n-card>
    )
  }
})
