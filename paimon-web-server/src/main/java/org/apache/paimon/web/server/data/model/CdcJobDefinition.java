package org.apache.paimon.web.server.data.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @TableName cdc_job_definition
 */
@TableName(value ="cdc_job_definition")
@Data
public class CdcJobDefinition extends BaseModel implements Serializable {

    private String title;

    private String description;

    private Integer cdcType;

    private String config;

    private String createUser;


}