export interface User {
  id: number
  username: string
  nickname: string
  userType: string
  mobile?: string
  email?: string
  enabled: boolean
  createTime: string
  updateTime: string
  roles?: []
}

export interface UserParams {
  username?: string
  pageNum: number
  pageSize: number
}

export interface UserDTO {
  id?: number
  username: string
  password: string
  nickname?: string
  mobile?: string
  email?: string
  enabled: true
  roleIds?: string[]
  createTime?: string
  updateTime?: string
  roles?: []
}
