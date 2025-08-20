package org.acme.reservation.faulttolerance;

import io.smallrye.faulttolerance.api.BeforeRetryHandler;
import org.eclipse.microprofile.faulttolerance.ExecutionContext;
import org.jboss.logging.Logger;

import java.util.Arrays;

public class LogBeforeRetry implements BeforeRetryHandler {

    private static final Logger LOG = Logger.getLogger(LogBeforeRetry.class);

    @Override
    public void handle(ExecutionContext ctx) {
        final Throwable last = ctx.getFailure();
        LOG.warnf("Retrying %s(%s) due to: %s",
            ctx.getMethod().getName(),
            Arrays.toString(ctx.getParameters()),
            last == null ? "<no exception>" : last.toString());
    }
}
