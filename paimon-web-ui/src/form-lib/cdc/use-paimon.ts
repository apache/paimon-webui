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

import { getAllCatalogs } from "@/api/models/catalog"
import type { IJsonItem } from "@/components/dynamic-form/types"
import { useCatalogStore } from "@/store/catalog"

export function usePaimon(item: any) {
	const { t } = useLocaleHooks()
	const tabType = item.data.tabType
	let catalogOptions: any = ref([{value:1}])
	const catalogStore = useCatalogStore()
	catalogStore.getAllCatalogs().then(()=>{
		catalogOptions.value = catalogStore.catalogs
	})
	const model = reactive({
		warehouse: '',
		metastore: '',
		url: '',
		other_configs: '',
		database: '',
		table_name: '',
		primary_key: '',
		partition_column: '',
		other_configs2: '',
	})

	return {
		json: [
			{
				type: 'select',
				field: 'catalog',
				name: 'catalog',
				span: computed(() => tabType.value === 'catalog_configuration' ? 24 : 0),
				props: {
					placeholder: '',
				},
				options: catalogOptions

			},
			{
				type: 'input',
				field: 'database',
				name: t('cdc.database'),
				props: {
					placeholder: ''
				},
				span: computed(() => tabType.value === 'synchronization_configuration' ? 24 : 0),
				validate: {
					trigger: ['input', 'blur'],
					required: true,
					message: 'error',
					validator: (validator: any, value: string) => {
						if (!value) {
							return new Error('error')
						}
					}
				}
			},
			{
				type: 'input',
				field: 'table_name',
				name: t('cdc.table_name'),
				props: {
					placeholder: ''
				},
				span: computed(() => tabType.value === 'synchronization_configuration' ? 24 : 0),
				validate: {
					trigger: ['input', 'blur'],
					required: true,
					message: 'error',
					validator: (validator: any, value: string) => {
						if (!value) {
							return new Error('error')
						}
					}
				}
			},
			{
				type: 'input',
				field: 'primary_key',
				name: t('cdc.primary_key'),
				span: computed(() => tabType.value === 'synchronization_configuration' ? 24 : 0),
				props: {
					placeholder: ''
				}
			},
			{
				type: 'input',
				field: 'partition_column',
				name: t('cdc.partition_column'),
				span: computed(() => tabType.value === 'synchronization_configuration' ? 24 : 0),
				props: {
					placeholder: ''
				}
			},
			{
				type: 'input',
				field: 'other_configs2',
				name: t('cdc.other_configs'),
				span: computed(() => tabType.value === 'synchronization_configuration' ? 24 : 0),
				props: {
					placeholder: '',
					type: 'textarea',
				}
			},
		] as IJsonItem[], model
	}
}
