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

import TabMenuSidebar from "@src/pages/Playground/TabMenuSidebar";
import RightContent from "@pages/Playground/RightContent";
import {Divider} from "@douyinfe/semi-ui";

export default function  Index() {
    return(
        <div style={{height: 'auto'}}>
            <span>
                 <TabMenuSidebar/>
            </span>
            <span><Divider layout="vertical" margin='388px' style={{position: 'fixed', height: '100%', marginTop: '-25px'}}/></span>
            <span>
               <RightContent/>
            </span>
        </div>
    )
}
