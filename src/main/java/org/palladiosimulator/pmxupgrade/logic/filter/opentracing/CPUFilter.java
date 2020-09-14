package org.palladiosimulator.pmxupgrade.logic.filter.opentracing;

import org.palladiosimulator.pmxupgrade.logic.filter.AbstractTraceProcessingFilter;
import org.palladiosimulator.pmxupgrade.model.common.Configuration;
import org.palladiosimulator.pmxupgrade.model.inputreader.opentracing.jaeger.TraceRecord;

public class CPUFilter implements AbstractTraceProcessingFilter {

    @Override
    public TraceRecord filter(Configuration configuration, TraceRecord traceRecord) {
        return traceRecord;
    }
}
