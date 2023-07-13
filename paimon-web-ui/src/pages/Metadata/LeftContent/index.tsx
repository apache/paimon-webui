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

import { IconPlus, IconArticle } from "@douyinfe/semi-icons";
import CatalogTree from "@pages/Metadata/LeftContent/CatalogTree";
import {Divider} from "@douyinfe/semi-ui";

const MetadataSidebar = () => {
    return(
        <div id={"d1"} style={{display: "flex", flexWrap: "wrap", width: "380px", height: "100%", flexFlow: "1",
            marginLeft: "30px"}}>
            <div id={"d2"} style={{width: "100%",display: "flex", alignItems: "center", height: "40px",color: "var(--semi-color-text-0)"}}>
                <IconArticle style={{ marginLeft: "6px"}}/>
                <span style={{marginLeft: "10px"}}>Catalog Directory</span>
                <IconPlus style={{marginLeft: "113px"}}/>
            </div>
            <Divider layout="horizontal" margin='50px'  style={{position: 'fixed', width: "360px", marginLeft: "-30px"}}/>
            <div id={"d3"} style={{width: "100%",display: "flex",height: "calc(100% - 60px)", marginTop: "22px"}}>
                <CatalogTree/>
            </div>
        </div>
    )
}

export default MetadataSidebar;
