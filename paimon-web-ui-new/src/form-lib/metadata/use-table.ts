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

import type { IJsonItem } from '@/components/dynamic-form/types'

export function useTable(item:any) {
  const { t } = useLocaleHooks()

	const model = reactive({
		name: item.name,
		description: item.description,
		synchronizationType: item.synchronizationType,
	})

	const synchronizationTypeOptions = [
		{
			label: t('cdc.single_table_synchronization'),
			value: 0
		},
		{
			label: t('cdc.whole_database_synchronization'),
			value: 1
		},
	]

	return {
		json: [
			{
				type: 'input',
				field: 'name',
				name: t('cdc.synchronization_job_name'),
				props: {
					placeholder: ''
				},
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
				field: 'description',
				name: t('cdc.task_description'),
				props: {
					placeholder: ''
				}
			},
			{
				type: 'radio',
				field: 'synchronizationType',
				name: t('cdc.synchronization_type'),
				options: synchronizationTypeOptions,
				value: 0,
			},
		] as IJsonItem[], model
	}
}
