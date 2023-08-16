/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

import {useEffect, useState} from "react";
import {Card,} from '@douyinfe/semi-ui';
import styled from "styled-components";
import {useForm} from "antd/es/form/Form";
import {ProForm, ProFormCheckbox, ProFormText, SubmitterProps} from "@ant-design/pro-components";
import http from "@api/http.ts";
import {API_ENDPOINTS} from "@api/endpoints.ts";
import {Col, Row} from "antd";
import "@douyinfe/semi-foundation/lib/es/button/button.css?inline"
import {LoginParams} from "@src/types/User/data";


const Login = () => {
    // const dispatch = useDispatch();
    // const navigate = useNavigate();

    const [ldapEnabled, setLdapEnabled] = useState(false);
    const [submitting, setSubmitting] = useState(false);

    const [form] = useForm<LoginParams>();


    useEffect(() => {
        // todo: get ldap enabled status , the backend code is not ready yet
        // http.httpGet(API_ENDPOINTS.GET_LDAP_ENABLE).then(res => {
        //     setLdapEnabled(res.data);
        //     form.setFieldValue("ldapLogin",res.data)
        // }, err => console.error(err))
    }, []);


    const handleClickLogin = async () => {
        setSubmitting(true);
        const values = await form.validateFields();
        await http.httpPost(API_ENDPOINTS.LOGIN, {...values}, () => setSubmitting(true), () => setSubmitting(false))
    };

    const proFormSubmitter: SubmitterProps = {
        searchConfig: {submitText: 'Login'},
        resetButtonProps: false,
        submitButtonProps: {
            className: "semi-button-primary",
            type: 'primary',
            loading: submitting,
            autoFocus: true,
            htmlType: 'submit',
            size: "large",
            shape: "round",
            style: {
                width: '100%',
            }
        },
    };


    return <Container>
        <Card style={{width: 400}}>
            <h3>Paimon Manager</h3>
            <ProForm
                form={form}
                onFinish={handleClickLogin}
                initialValues={{rememberMe: true}}
                submitter={{...proFormSubmitter}}
            >
                <ProFormText
                    name="username"
                    label="Username"
                    placeholder="Please input username"
                    rules={[{required: true, message: 'Please input username'}]}
                />
                <ProFormText.Password
                    name="password"
                    label="Password"
                    placeholder="Please input password"
                    rules={[{required: true, message: 'Please input password'}]}
                />
                <Row>
                    <Col span={18}>
                        <ProFormCheckbox name="rememberMe">
                            {/*{l("login.rememberMe")}*/} Remember Me
                        </ProFormCheckbox>
                    </Col>
                    <Col span={6}>
                        <ProFormCheckbox name="ldapLogin" hidden={!ldapEnabled}>
                            {/*{l("login.ldapLogin")}*/} LDAP Login
                        </ProFormCheckbox>
                    </Col>
                </Row>
            </ProForm>
        </Card>
    </Container>
}

export default Login;

const Container = styled.div`
  background: url('https://mdn.alipayobjects.com/yuyan_qk0oxh/afts/img/V-_oS6r-i7wAAAAAAAAAAAAAFl94AQBr') no-repeat;
  background-size: 100% 100%;
  height: 100vh;
  overflow: hidden;
  width: 100vw;
  display: flex;
  flex-direction: row;
  flex-wrap: nowrap;
  align-content: center;
  justify-content: center;
  align-items: center;
  h3 {
    font-size: 24px;
    font-weight: 600;
    text-align: center;
    margin: 10px auto 20px;
  }
`