package org.palladiosimulator.pmxupgrade.logic.filter;

import org.palladiosimulator.pmxupgrade.model.common.Configuration;
import org.palladiosimulator.pmxupgrade.model.inputreader.opentracing.jaeger.TraceRecord;

public interface AbstractTraceProcessingFilter {

    TraceRecord filter(Configuration configuration, TraceRecord traceRecord);

}
