package de.kit.research.logic.filter.opentracing;

import de.kit.research.logic.filter.AbstractTraceProcessingFilter;
import de.kit.research.model.common.Configuration;
import de.kit.research.model.inputreader.opentracing.jaeger.TraceRecord;

public class TraceReconstructionFilter implements AbstractTraceProcessingFilter {

    @Override
    public TraceRecord filter(Configuration configuration, TraceRecord traceRecord) {
        return null;
    }

}
