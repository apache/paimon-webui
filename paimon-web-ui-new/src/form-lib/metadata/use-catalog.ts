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

import type { IJsonItem } from "@/components/dynamic-form/types"

export function useCatalog() {
  const { t } = useLocaleHooks()

	const model = reactive({ })

	const catalogTypeOptions = [
		{
			label: 'FileSystem',
			value: 0
		},
		{
			label: 'Hive',
			value: 1
		},
	]

	return {
		json: [
			{
				type: 'input',
				field: 'name',
				name: t('metadata.catalog_name'),
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
				type: 'select',
				field: 'description',
				name: t('metadata.catalog_type'),
				options: catalogTypeOptions,
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
				field: 'name',
				name: t('metadata.catalog_warehouse'),
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
				field: 'name',
				name: t('metadata.catalog_hiveurl'),
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
				field: 'name',
				name: t('metadata.catalog_hive_conf_dir'),
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
		] as IJsonItem[], model
	}
}
