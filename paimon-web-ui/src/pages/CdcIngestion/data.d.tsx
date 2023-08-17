
declare type CdcManagePagination = {
    data: (CdcManage & TableData)[] | []
    total: number | 0
}
declare type CdcManage = {
    id: number
    name: string
    type: string
    runArgs: string
    flinkConf: string
    submitStatus?: string
    missionStatus?: string
}
declare type TableData = {
    key: string | number
}
declare type TablePaginationData = {
    pageSize: number | 1
    currentPage: number | 10
}