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

import { NTag, type DataTableColumns, NButton } from "naive-ui";

import styles from './index.module.scss'

type RowData = {
  key: number
  name: string
  age: number
  address: string
  tags: string[]
}


type Song = {
  no: number
  title: string
  length: string
}

export default defineComponent({
  name: 'MetadataSchema',
  setup() {
    const { t } = useLocaleHooks()

    const data: RowData[] = [
      {
        key: 0,
        name: 'John Brown',
        age: 32,
        address: 'New York No. 1 Lake Park',
        tags: ['nice', 'developer']
      },
      {
        key: 1,
        name: 'Jim Green',
        age: 42,
        address: 'London No. 1 Lake Park',
        tags: ['wow']
      },
      {
        key: 2,
        name: 'Joe Black',
        age: 32,
        address: 'Sidney No. 1 Lake Park',
        tags: ['cool', 'teacher']
      }
    ]

    const childColumns: DataTableColumns<Song> = [
      {
        title: 'No',
        key: 'no'
      },
      {
        title: 'Title',
        key: 'title'
      },
      {
        title: 'Length',
        key: 'length'
      }
    ]

    const columns: DataTableColumns<RowData> = [
      {
        title: '#',
        type: 'expand',
        renderExpand: () => {
          return <div>
            <div class={styles.schema_field_title}>Fields: </div>
            <n-data-table
              columns={childColumns}
              data={[
                { no: 3, title: 'Wonderwall', length: '4:18' },
                { no: 4, title: "Don't Look Back in Anger", length: '4:48' },
                { no: 12, title: 'Champagne Supernova', length: '7:27' }
              ]}
              bordered={false}
            />
          </div>
        }
      },
      {
        title: 'ID',
        key: 'id',
        render: (_, index) => {
          return `${index + 1}`
        }
      },
      {
        title: 'Name',
        key: 'name'
      },
      {
        title: 'Age',
        key: 'age'
      },
      {
        title: 'Address',
        key: 'address'
      },
      {
        title: 'Tags',
        key: 'tags',
        render(row) {
          const tags = row.tags.map((tagKey) => {
            return h(
              NTag,
              {
                style: {
                  marginRight: '6px'
                },
                type: 'info',
                bordered: false
              },
              {
                default: () => tagKey
              }
            )
          })
          return tags
        }
      },
      {
        title: 'Action',
        key: 'actions',
        render() {
          return h(
            NButton,
            {
              size: 'small',
            },
            { default: () => 'Send Email' }
          )
        }
      }
    ]

    return {
      columns,
      data,
      t,
    }
  },
  render() {
    return (
      <n-card>
        <n-data-table
          columns={this.columns}
          data={this.data}
        />
      </n-card>
    );
  },
});
