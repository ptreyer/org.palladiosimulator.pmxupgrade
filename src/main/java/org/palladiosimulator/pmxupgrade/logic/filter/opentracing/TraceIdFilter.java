package org.palladiosimulator.pmxupgrade.logic.filter.opentracing;

import org.palladiosimulator.pmxupgrade.logic.filter.AbstractTraceProcessingFilter;
import org.palladiosimulator.pmxupgrade.model.common.Configuration;
import org.palladiosimulator.pmxupgrade.model.inputreader.opentracing.jaeger.Trace;
import org.palladiosimulator.pmxupgrade.model.inputreader.opentracing.jaeger.TraceRecord;

import java.util.List;

public class TraceIdFilter implements AbstractTraceProcessingFilter {

    public TraceRecord filter(Configuration configuration, TraceRecord traceRecord) {
        List<Trace> tracesToFilter = traceRecord.getData();

        tracesToFilter.removeIf(p -> configuration.getTraceIDsToFilter().contains(p.getTraceID()));

        return new TraceRecord(tracesToFilter);
    }

}
