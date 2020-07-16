package de.kit.research.logic.filter.opentracing;

import de.kit.research.logic.filter.AbstractTraceProcessingFilter;
import de.kit.research.model.common.Configuration;
import de.kit.research.model.inputreader.opentracing.jaeger.Trace;
import de.kit.research.model.inputreader.opentracing.jaeger.TraceRecord;

import java.util.List;

public class TraceIdFilter implements AbstractTraceProcessingFilter {

    public TraceRecord filter(Configuration configuration, TraceRecord traceRecord) {
        List<Trace> tracesToFilter = traceRecord.getData();

        tracesToFilter.removeIf(p -> configuration.getTraceIDsToFilter().contains(p.getTraceID()));

        return new TraceRecord(tracesToFilter);
    }

}
