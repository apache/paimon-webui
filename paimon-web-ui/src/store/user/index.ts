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

export interface UserState {
  username: string | null
  nickname: string | null
  directories: Array<string>
  menus: Array<string>
  admin: boolean
}

export const useUserStore = defineStore({
  id: 'user',
  state: (): UserState => ({
    username: '',
    nickname: '',
    directories: [],
    menus: [],
    admin: false,
  }),
  persist: true,
  getters: {
    getUsername(): string | null {
      return this.username
    },
    getNickname(): string | null {
      return this.nickname
    },
    getMenus(): Array<string> | null {
      return this.menus
    },
    getDiresctoies(): Array<string> {
      return this.directories
    },
    getAdmin(): boolean | null {
      return this.admin
    },
  },
  actions: {
    setUsername(username: string): void {
      this.username = username
    },
    setNickname(nickname: string): void {
      this.nickname = nickname
    },
    setAdmin(admin: boolean): void {
      this.admin = admin
    },
    setMenus(menus: Array<string>): void {
      this.menus = menus
    },
    setDirectories(directories: Array<string>) {
      this.directories = directories
    },
  },
})
