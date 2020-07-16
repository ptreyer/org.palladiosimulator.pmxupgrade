package de.kit.research.logic.filter.opentracing;

import de.kit.research.logic.filter.AbstractTraceProcessingFilter;
import de.kit.research.model.common.Configuration;
import de.kit.research.model.inputreader.opentracing.jaeger.Span;
import de.kit.research.model.inputreader.opentracing.jaeger.Trace;
import de.kit.research.model.inputreader.opentracing.jaeger.TraceRecord;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class TimestampFilter implements AbstractTraceProcessingFilter {

    public TraceRecord filter(Configuration configuration, TraceRecord traceRecord) {
        List<Trace> tracesToFilter = prepareFiltering(traceRecord).getData();

        if (configuration.getIgnoreBeforeTimestamp() != null)
            tracesToFilter.removeIf(p -> p.getStartTime() < configuration.getIgnoreBeforeTimestamp());

        if (configuration.getIgnoreAfterTimestamp() != null)
            tracesToFilter.removeIf(p -> p.getStartTime() > configuration.getIgnoreAfterTimestamp());

        return new TraceRecord(tracesToFilter);
    }

    private TraceRecord prepareFiltering(TraceRecord traceRecord) {
        traceRecord.getData().forEach(t -> {
            Optional<Span> minTimeSpan = t.getSpans().stream().min(Comparator.comparing(Span::getStartTime));
            minTimeSpan.ifPresent(span -> t.setStartTime(span.getStartTime()));
        });
        return traceRecord;
    }
}
