package de.kit.research.logic.filter;

import de.kit.research.model.common.Configuration;
import de.kit.research.model.inputreader.ProcessingObjectWrapper;
import de.kit.research.model.inputreader.opentracing.jaeger.TraceRecord;

public interface AbstractTraceReconstructionFilter {

    ProcessingObjectWrapper filter(Configuration configuration, TraceRecord traceRecord);

}
