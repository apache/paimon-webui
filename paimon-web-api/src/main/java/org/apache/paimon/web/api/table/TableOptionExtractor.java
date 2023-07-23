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

package org.apache.paimon.web.api.table;

import org.apache.paimon.CoreOptions;
import org.apache.paimon.options.ConfigOption;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/** table option extractor. */
public class TableOptionExtractor {

    public static List<String> keys() {
        List<OptionWithMetaInfo> optionWithMetaInfos = extractConfigOptions(CoreOptions.class);
        List<String> keys = new ArrayList<>();
        for (OptionWithMetaInfo optionWithMetaInfo : optionWithMetaInfos) {
            keys.add(optionWithMetaInfo.option.key());
        }
        return keys;
    }

    public static List<OptionWithMetaInfo> extractConfigOptions(Class<?> clazz) {
        try {
            List<OptionWithMetaInfo> configOptions = new ArrayList<>(8);
            Field[] fields = clazz.getFields();
            for (Field field : fields) {
                if (isConfigOption(field)) {
                    configOptions.add(
                            new OptionWithMetaInfo((ConfigOption<?>) field.get(null), field));
                }
            }
            return configOptions;
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to extract config options from class " + clazz + '.', e);
        }
    }

    private static boolean isConfigOption(Field field) {
        return field.getType().equals(ConfigOption.class);
    }

    static class OptionWithMetaInfo {
        final ConfigOption<?> option;
        final Field field;

        public OptionWithMetaInfo(ConfigOption<?> option, Field field) {
            this.option = option;
            this.field = field;
        }
    }
}
