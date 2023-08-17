package org.apache.paimon.web.server.data.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum SubmitStatus implements IEnum<Integer> {
    NEVER(0,"not submit"),SUCCESS(1,"success"),FAILED(2,"failed"),PROCESSING(3,"processing"),RUNNING(4,"running");
    private final int value;
    private final String desc;
    @Override
    public Integer getValue() {
        return this.value;
    }
}