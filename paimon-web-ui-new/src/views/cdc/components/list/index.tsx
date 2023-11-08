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
import TableAction from '@/components/table-action';
import { useCDCStore } from '@/store/cdc';
import type { Router } from 'vue-router';

export default defineComponent({
  name: 'ListPage',
  setup() {
    const { t } = useLocaleHooks()
    const router: Router = useRouter()

    const tableVariables = reactive({
      columns: [
        {
          title: computed(() => t('cdc.job_name')),
          key: 'name',
          resizable: true
        },
        {
          title: computed(() => t('cdc.synchronization_type')),
          key: 'type',
          resizable: true
        },
        {
          title: computed(() => t('cdc.job_description')),
          key: 'description',
          resizable: true
        },
        {
          title: computed(() => t('cdc.create_user')),
          key: 'create_user',
          resizable: true
        },
        {
          title: computed(() => t('cdc.create_time')),
          key: 'create_time',
          resizable: true
        },
        {
          title: computed(() => t('cdc.update_time')),
          key: 'update_time',
          resizable: true
        },
        {
          title: computed(() => t('cdc.operation')),
          key: 'actions',
          render: (row: any) =>
            h(TableAction, {
              row,
              onHandleEdit: (row) => {
                const CDCStore = useCDCStore()
                CDCStore.setModel(row)
                router.push({ path: '/cdc_ingestion/dag' })
              },
            })
        }
      ],
      data: [
        {
          name: 1,
          type: 'Single table synchronization',
          create_user: 'admin',
          cells: [
            {
                "position": {
                    "x": 300,
                    "y": 40
                },
                "size": {
                    "width": 150,
                    "height": 40
                },
                "view": "vue-shape-view",
                "shape": "custom-node",
                "ports": {
                    "groups": {
                        "in": {
                            "position": "left",
                            "attrs": {
                                "circle": {
                                    "r": 4,
                                    "magnet": true,
                                    "stroke": "transparent",
                                    "strokeWidth": 1,
                                    "fill": "transparent"
                                }
                            }
                        },
                        "out": {
                            "position": {
                                "name": "right",
                                "args": {
                                    "dx": 5
                                }
                            },
                            "attrs": {
                                "circle": {
                                    "r": 4,
                                    "magnet": true,
                                    "stroke": "transparent",
                                    "strokeWidth": 1,
                                    "fill": "transparent"
                                }
                            }
                        }
                    },
                    "items": [
                        {
                            "id": "MySQL-out",
                            "group": "out",
                            "attrs": {
                                "circle": {
                                    "fill": "transparent",
                                    "stroke": "transparent"
                                }
                            }
                        }
                    ]
                },
                "id": "MySQL",
                "data": {
                    "name": "MySQL",
                    "value": "MYSQL",
                    "type": "INPUT",
                    "host": "1",
                    "port": "2",
                    "username": "3",
                    "password": "4",
                    "other_configs": "5",
                    "database": "",
                    "table_name": "",
                    "type_mapping": "",
                    "metadata_column": "",
                    "computed_column": ""
                },
                "zIndex": 1
            },
            {
                "position": {
                    "x": 640,
                    "y": 40
                },
                "size": {
                    "width": 150,
                    "height": 40
                },
                "view": "vue-shape-view",
                "shape": "custom-node",
                "ports": {
                    "groups": {
                        "in": {
                            "position": "left",
                            "attrs": {
                                "circle": {
                                    "r": 4,
                                    "magnet": true,
                                    "stroke": "transparent",
                                    "strokeWidth": 1,
                                    "fill": "transparent"
                                }
                            }
                        },
                        "out": {
                            "position": {
                                "name": "right",
                                "args": {
                                    "dx": 5
                                }
                            },
                            "attrs": {
                                "circle": {
                                    "r": 4,
                                    "magnet": true,
                                    "stroke": "transparent",
                                    "strokeWidth": 1,
                                    "fill": "transparent"
                                }
                            }
                        }
                    },
                    "items": [
                        {
                            "id": "Paimon-in",
                            "group": "in",
                            "attrs": {
                                "circle": {
                                    "fill": "transparent",
                                    "stroke": "transparent"
                                }
                            }
                        }
                    ]
                },
                "id": "Paimon",
                "data": {
                    "name": "Paimon",
                    "value": "PAIMON",
                    "type": "OUTPUT"
                },
                "zIndex": 2
            },
            {
                "shape": "dag-edge",
                "connector": {
                    "name": "smooth"
                },
                "id": "c3bec4f4-eea9-44ed-b98e-63605d619d50",
                "zIndex": 3,
                "source": {
                    "cell": "MySQL",
                    "port": "MySQL-out"
                },
                "target": {
                    "cell": "Paimon"
                }
            }
          ]
        },
      ],
      pagination: {
        pageSize: 10
      }
    })
    
    return {
      t,
      ...toRefs(tableVariables)
    }
  },
  render() {
    return (
      <div class={styles['list-page']}>
        <n-data-table
          columns={this.columns}
          data={this.data}
          pagination={this.pagination}
          bordered={false}
        />
      </div>
    )
  }
})
