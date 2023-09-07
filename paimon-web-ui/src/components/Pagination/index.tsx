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

import { Pagination } from '@douyinfe/semi-ui';
import React from "react";
import styles from './pagination.module.less';

interface PaginationWrapperProps {
    total: number;
    onPageChange: (page: number, pageSize: number) => void;
}

const PaginationWrapper: React.FC<PaginationWrapperProps> = ({ total, onPageChange }) => {
    const defaultPageSize = 10;

    const handlePageChange = (page: number, pageSize: number) => {
        if (onPageChange) {
            onPageChange(page, pageSize);
        }
    };

    return (
        <Pagination
            total={total}
            pageSize={defaultPageSize}
            className={styles.circlePagination}
            onChange={handlePageChange}
        />
    );
};


export default PaginationWrapper;