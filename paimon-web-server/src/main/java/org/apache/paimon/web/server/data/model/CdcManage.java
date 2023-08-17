package org.apache.paimon.web.server.data.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import org.apache.paimon.web.server.data.enums.FlinkType;
import org.apache.paimon.web.server.data.enums.SubmitStatus;

@Getter
@Setter
@TableName("cdc_manage")
public class CdcManage extends BaseModel {
    /** mission name */
    private String name;
    /** flink mission submit type 0: yarn-application 1: session */
    private FlinkType type;

    private String runArgs;
    private String flinkConf;
    /**
     * mission submit status 0: not submit 1: submit success 2: submit failed 3: submit processing
     */
    private SubmitStatus submitStatus;
    /** flink mission status  0: not start1: success 2: failed 3: processing */
    private SubmitStatus missionStatus;

    private String runLog;
    /** example: 127.0.0.1:8080 */
    private String webAddr;

    private String yarnAddr;
    /** flink application_id */
    private String applicationId;
    private String jobId;
    /** */
    private String otherResult;
}
