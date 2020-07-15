package de.kit.research.logic.filter;

import de.kit.research.model.common.Configuration;
import de.kit.research.model.inputreader.opentracing.jaeger.TraceRecord;

public interface AbstractTraceProcessingFilter {

    TraceRecord filter(Configuration configuration, TraceRecord traceRecord);

}
