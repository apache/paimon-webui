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

import { RemoveCircleOutline, Warning } from '@vicons/ionicons5'
import { deleteUser } from '@/api/models/user'

export default defineComponent({
  props: {
    userId: {
      type: Number,
    },
    onDelete: Function,
  },
  setup(props) {
    const onDelete = async () => {
      (props.userId || props.userId === 0) && await deleteUser(props.userId)
      props.onDelete && props.onDelete()
    }

    return {
      onDelete,
    }
  },
  render() {
    return (
      <n-popconfirm onPositiveClick={this.onDelete}>
        {{
          default: () => 'Confirm to delete ? ',
          trigger: () => (
            <n-button strong secondary circle type="error">
              {{
                icon: () => <n-icon component={RemoveCircleOutline} />,
              }}
            </n-button>
          ),
          icon: () => <n-icon color="#EC4C4D" component={Warning} />,
        }}
      </n-popconfirm>
    )
  },
})
