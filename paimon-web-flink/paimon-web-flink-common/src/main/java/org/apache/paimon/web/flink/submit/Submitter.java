package org.apache.paimon.web.flink.submit;

import com.google.common.base.Preconditions;
import org.apache.paimon.web.flink.common.SubmitMode;
import org.apache.paimon.web.flink.submit.request.SubmitRequest;
import org.apache.paimon.web.flink.submit.result.SubmitResult;
import org.apache.paimon.web.flink.submit.yarn.YarnApplicationSubmitFactory;

/** This class provides a method to submit a Flink job based on the given SubmitRequest. */
public class Submitter {

    public static SubmitResult submit(SubmitRequest request) {
        FlinkSubmitFactory flinkSubmitFactory =
                submitRequest -> {
                    throw new UnsupportedOperationException("Factory not supported.");
                };

        SubmitMode mode = SubmitMode.of(request.getExecutionTarget());
        Preconditions.checkNotNull(mode, "execution target can not be null.");

        switch (mode) {
            case YARN_PRE_JOB:
                // TODO
                break;
            case YARN_SESSION:
                // TODO
                break;
            case YARN_APPLICATION:
                flinkSubmitFactory = YarnApplicationSubmitFactory.createFactory();
                break;
            case KUBERNETES_SESSION:
                // TODO
                break;
            case KUBERNETES_APPLICATION:
                // TODO
                break;
            default:
                throw new UnsupportedOperationException("Unsupported execution target: " + mode.getName());
        }

        return flinkSubmitFactory.createSubmit(request).submit();
    }
}
