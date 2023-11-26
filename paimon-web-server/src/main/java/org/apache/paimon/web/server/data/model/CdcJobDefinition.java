package org.apache.paimon.web.server.data.model;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;

/**
 * @TableName cdc_job_definition
 */
@TableName(value ="cdc_job_definition")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Builder
public class CdcJobDefinition extends BaseModel implements Serializable {

    private String title;

    private String description;

    private Integer cdcType;

    private String config;

    private String createUser;

    @TableLogic
    private boolean isDelete;
}