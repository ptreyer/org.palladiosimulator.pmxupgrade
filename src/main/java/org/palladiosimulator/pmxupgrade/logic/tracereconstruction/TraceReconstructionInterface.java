package org.palladiosimulator.pmxupgrade.logic.tracereconstruction;

import org.palladiosimulator.pmxupgrade.model.common.Configuration;
import org.palladiosimulator.pmxupgrade.model.inputreader.ProcessingObjectWrapper;
import org.palladiosimulator.pmxupgrade.model.inputreader.opentracing.jaeger.TraceRecord;

/**
 * General interface for the reconstruction of the tracing data, which means the transfer of the data into the internal format.
 *
 * @author Patrick Treyer
 */
public interface TraceReconstructionInterface {

    ProcessingObjectWrapper filter(Configuration configuration, TraceRecord traceRecord);

}
