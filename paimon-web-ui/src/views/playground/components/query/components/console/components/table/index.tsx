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

import styles from './index.module.scss'

export default defineComponent({
  name: 'TableResult',
  setup() {
    const columns = [
      {
        title: '#',
        key: 'key',
        render: (_:any, index:number) => {
          return `${index + 1}`
        }
      },
      {
        title: 'id',
        key: 'id',
        resizable: true
      },
      {
        title: 'name',
        key: 'name',
        resizable: true
      },
      {
        title: 'age',
        key: 'age',
        resizable: true
      },
      {
        title: 'address',
        key: 'address',
        resizable: true
      },
    ]

    type User = {
      id: number
      name: string
      age: number
      address: string
    }

    const data: User[] = [
      { id: 1, name: 'jack', age: 36, address: 'beijing' },
      { id: 2, name: 'li hua', age: 38, address: 'shanghai' },
      { id: 3, name: 'zhao ming', age: 27, address: 'hangzhou' },
      { id: 3, name: 'zhao ming', age: 27, address: 'hangzhou' }
    ]

    return {
      columns,
      data,
    }
  },
  render() {
    return(
      <div>
        <n-data-table
          class={styles.table}
          columns={this.columns}
          data={this.data}
          max-height={138}
        />
      </div>
    )
  }
})