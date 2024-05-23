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

import { JobMapping, JobStatusProps, type JobStatusType } from '../../constant'

export default defineComponent({
  name: 'JobStatusTag',
  props: {
    type: {
      type: String as PropType<JobStatusType>,
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
      <n-tag
        color={{
          color: JobStatusProps[this.type].bgColor,
          textColor: JobStatusProps[this.type].primaryColor,
        }}
        bordered={false}
        round
      >
        {{
          default: () => this.t(`job.${JobMapping[this.type]}`),
          icon: () => (
            <n-icon color={JobStatusProps[this.type].primaryColor}>
              {{
                default: () => JobStatusProps[this.type].icon,
              }}
            </n-icon>
          ),
        }}
      </n-tag>
    )
  },
})
