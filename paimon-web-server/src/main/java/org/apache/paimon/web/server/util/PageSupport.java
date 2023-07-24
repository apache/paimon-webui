/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.paimon.web.server.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/** Page support. */
public class PageSupport {

    private static final char SEPARATOR = '_';
    /** pageNum */
    public static final String PAGE_NUM = "pageNum";

    /** pageSize */
    public static final String PAGE_SIZE = "pageSize";

    /** order column */
    public static final String ORDER_BY_COLUMN = "orderByColumn";

    /** desc or asc */
    public static final String IS_ASC = "isAsc";

    /** page params reasonable */
    public static final String REASONABLE = "reasonable";

    public static <T> IPage<T> startPage() {
        Integer pageNum = ServletUtils.getParameterToInt(PAGE_NUM, 1);
        Integer pageSize = ServletUtils.getParameterToInt(PAGE_SIZE, 10);
        Page<T> page = new Page<>(pageNum, pageSize);
        String orderBy = getOrderBy();
        if (StringUtils.isNotEmpty(orderBy)) {
            OrderItem orderItem = new OrderItem(orderBy, isAsc());
            page.addOrder(orderItem);
        }
        return page;
    }

    public static String getOrderBy() {
        String orderByColumn = ServletUtils.getParameter(ORDER_BY_COLUMN);
        if (StringUtils.isEmpty(orderByColumn)) {
            return "";
        }
        return toUnderScoreCase(orderByColumn);
    }

    public static boolean isAsc() {
        String isAsc = ServletUtils.getParameter(IS_ASC);
        return isAsc.equals("asc") || isAsc.equals("ascending");
    }

    public static String toUnderScoreCase(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        boolean preCharIsUpperCase = true;
        boolean curreCharIsUpperCase = true;
        boolean nexteCharIsUpperCase = true;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (i > 0) {
                preCharIsUpperCase = Character.isUpperCase(str.charAt(i - 1));
            } else {
                preCharIsUpperCase = false;
            }

            curreCharIsUpperCase = Character.isUpperCase(c);

            if (i < (str.length() - 1)) {
                nexteCharIsUpperCase = Character.isUpperCase(str.charAt(i + 1));
            }

            if (preCharIsUpperCase && curreCharIsUpperCase && !nexteCharIsUpperCase) {
                sb.append(SEPARATOR);
            } else if ((i != 0 && !preCharIsUpperCase) && curreCharIsUpperCase) {
                sb.append(SEPARATOR);
            }
            sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }
}
