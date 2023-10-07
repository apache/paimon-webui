package org.apache.paimon.web.flink.submit;

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
        if (mode == null) {
            throw new IllegalArgumentException(
                    "Invalid execution target: " + request.getExecutionTarget());
        }

        switch (mode) {
            case LOCAL:
                // TODO
                break;
            case REMOTE:
                // TODO
                break;
            case YARN_PRE_JOB:
                // TODO
                break;
            case YARN_SESSION:
                // TODO
                break;
            case APPLICATION:
                flinkSubmitFactory = YarnApplicationSubmitFactory.createFactory();
                break;
            case KUBERNETES_NATIVE_SESSION:
                // TODO
                break;
            case KUBERNETES_NATIVE_APPLICATION:
                // TODO
                break;
            default:
                throw new IllegalArgumentException("Unhandled execution target: " + mode);
        }

        return flinkSubmitFactory.createSubmit(request).submit();
    }
}
