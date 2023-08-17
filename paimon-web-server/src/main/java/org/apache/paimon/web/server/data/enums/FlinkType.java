package org.apache.paimon.web.server.data.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum FlinkType implements IEnum<Integer> {
    YARN_APPLICATION(0, "yarn-application"),
    SESSION(1, "session");
    private final int value;
    private final String desc;

    @Override
    public Integer getValue() {
        return this.value;
    }
}
