package de.kit.research.logic.filter.opentracing;

import de.kit.research.logic.filter.AbstractTraceProcessingFilter;
import de.kit.research.model.common.Configuration;
import de.kit.research.model.inputreader.opentracing.jaeger.Span;
import de.kit.research.model.inputreader.opentracing.jaeger.Trace;
import de.kit.research.model.inputreader.opentracing.jaeger.TraceRecord;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class TraceIdFilter implements AbstractTraceProcessingFilter {

    public TraceRecord filter(Configuration configuration, TraceRecord traceRecord) {
        return null;
    }

}
