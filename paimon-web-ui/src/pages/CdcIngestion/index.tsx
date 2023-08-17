import {Button, Form, Popconfirm, SideSheet, Space, Table,Notification} from '@douyinfe/semi-ui';
import {
    IconDelete,
    IconEdit,
    IconPause,
    IconPlay,
    IconPlus,
    IconRefresh
} from "@douyinfe/semi-icons";
import {useEffect, useState} from "react";
import {Editor} from "@monaco-editor/react";
import {ColumnProps} from "@douyinfe/semi-ui/lib/es/table/interface";
import axios from "axios";

const cdcMangeInitData: CdcManage = {id: 0, flinkConf: "", name: "", runArgs: "", type: "SESSION"};

export function CdcIngestion() {
    const [visible, setVisible] = useState(false);
    const [loading, setLoading] = useState(true);
    const [buttonSpin, setButtonSpin] = useState<Record<string, boolean>>({});
    const [data, setData] = useState<CdcManagePagination>({data: [], total: 0});
    const [formData, setFormData] = useState<CdcManage>({...cdcMangeInitData});
    const [tablePaginationData, setTablePaginationData] = useState<TablePaginationData>({currentPage: 1, pageSize: 10});
    const loadData = (params?: TablePaginationData) => {
        setLoading(true)
        axios.post('http://127.0.0.1:10036/flinkAction/query', {
            currentPage: params ? params.currentPage : tablePaginationData?.currentPage,
            pageSize: params ? params.pageSize : tablePaginationData?.pageSize
        }).then(res => {
            setData(res.data)
            setLoading(false)
        })
    }
    useEffect(() => {
        loadData()
    }, [])
    const formOnChange = (values: object) => {
        setFormData(v => {
            return {...v, ...values}
        });
    }
    const refreshTable = () => {
        loadData()
    }
    const addData = () => {
        setVisible(true)
        setFormData({...cdcMangeInitData})
    }
    const editorOnChange = (field: string, values: string | undefined) => {
        setFormData(v => {
            return {...v, [field]: values}
        });
    }
    const deleteById = (id: number) => {
        axios.delete('http://127.0.0.1:10036/flinkAction/deleteById?id=' + id).then(() => {
            loadData()
        })
    }
    const saveOrUpdateSubmitEvent = () => {
        console.log(formData)
    }
    const renderRunButton =(record:CdcManage&TableData,index:number)=>{
        const spin = buttonSpin[record.key]||record.missionStatus=="PROCESSING";
        return (
            <Button loading={spin} theme="borderless" icon={record.submitStatus=="NEVER"?<IconPlay style={{color:"green"}} />:<IconPause style={{color:"red"}} />}
            onClick={()=>{
                const operate = record.submitStatus=="NEVER"?"run":"stop";
                setButtonSpin(v=>{
                    return {...v,[record.key]:true}
                })
                axios.post('http://127.0.0.1:10036/flinkAction/'+operate+'?id='+record.id).then(res => {
                    setButtonSpin(v=>{
                        return {...v,[record.key]:false}
                    })
                    if (res.data.code==200){
                        setData(v=>{
                            v.data[index] = res.data.data;
                            return {...v}
                        })
                    }else {
                        Notification.open({
                            title: 'Run Error',
                            content: res.data.msg,
                            duration: 3,
                        })
                    }
                })

            }}
            />
        )
    }
    const change = () => {
        setVisible(!visible);
    };
    const columns: ColumnProps<CdcManage & TableData>[] = [
        {
            title: '任务名',
            dataIndex: 'name',
        },
        {
            title: '类型',
            dataIndex: 'type',
        },
        {
            title: 'flink配置',
            dataIndex: 'flinkConf',
        },
        {
            title: '操作',
            dataIndex: 'operate',
            render: (_, record,index) => (
                <>
                    {renderRunButton(record,index)}
                    <Button icon={<IconEdit/>} theme="borderless" onClick={() => {
                        setFormData(record)
                        setVisible(true)
                    }}/>
                    <Popconfirm
                        title="确定是否要删除此修改？"
                        content="此修改将不可逆"
                        onConfirm={() => deleteById(record.id)}
                    >
                        <Button icon={<IconDelete/>} theme="borderless"/>
                    </Popconfirm>
                </>
            ),
        },
    ];


    return (
        <div style={{padding: 20}}>
            <Space style={{float: "right"}}>
                <Button icon={<IconRefresh/>} onClick={refreshTable}/>
                <Button icon={<IconPlus/>} onClick={addData}/>
            </Space>
            <Table columns={columns} dataSource={data?.data.map(x => {
                return {...x, key: x.id}
            })}
                   loading={loading}
                   pagination={{
                       position: 'top',
                       currentPage: tablePaginationData?.currentPage,
                       pageSize: tablePaginationData?.pageSize,
                       total: data?.total,
                       showSizeChanger: true,
                       onChange: (page, size) => {
                           setTablePaginationData({pageSize: size, currentPage: page})
                           loadData({pageSize: size, currentPage: page})
                       }
                   }}/>
            <SideSheet
                title="Add"
                visible={visible}
                onCancel={change}
                footer={
                    <div style={{display: 'flex', justifyContent: 'flex-end'}}>
                        <Button theme="solid" onClick={saveOrUpdateSubmitEvent}>Submit</Button>
                    </div>
                }
            >
                <Form onValueChange={formOnChange} initValues={{
                    ...formData,
                }}>
                    <Form.Input field='name' label='Mission Name' style={{width: '100%'}}
                                placeholder='Enter your mission name' rules={[{required: true}]}></Form.Input>
                    <Form.Select field="type" label='Flink Run Type' style={{width: '100%'}} rules={[{required: true}]}>
                        <Form.Select.Option value="YARN_APPLICATION">yarn-application</Form.Select.Option>
                        <Form.Select.Option value="SESSION">session</Form.Select.Option>
                    </Form.Select>

                    <label className="semi-form-field-label semi-form-field-label-left" htmlFor="type" id="type-label">
                        <div className="semi-form-field-label-text">Flink Run Args</div>
                    </label>
                    <div style={{border: "1px solid var(--semi-color-border)"}}>
                        <Editor value={formData.runArgs} height={240} language={"shell"}
                                options={{scrollBeyondLastColumn: 0.1}}
                                onChange={(v) => editorOnChange("runArgs", v)}></Editor>
                    </div>

                    <label className="semi-form-field-label semi-form-field-label-left" htmlFor="type" id="type-label">
                        <div className="semi-form-field-label-text">Flink Conf</div>
                    </label>
                    <div style={{border: "1px solid var(--semi-color-border)"}}>
                        <Editor value={formData.flinkConf} height={240} language={"yaml"}
                                onChange={(v) => editorOnChange("flinkConf", v)}></Editor>
                    </div>
                </Form>
            </SideSheet>
        </div>
    );
}
