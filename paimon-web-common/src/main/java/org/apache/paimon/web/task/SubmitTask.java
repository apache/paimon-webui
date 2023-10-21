package org.apache.paimon.web.task;

import org.apache.paimon.web.common.data.vo.SubmitResult;

public interface SubmitTask {
    public SubmitResult execute(String statement) throws Exception;

    public boolean stop(String statement) throws Exception;
}
