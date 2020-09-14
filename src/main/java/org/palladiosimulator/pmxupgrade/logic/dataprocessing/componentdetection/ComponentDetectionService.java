package org.palladiosimulator.pmxupgrade.logic.dataprocessing.componentdetection;

import org.palladiosimulator.pmxupgrade.model.inputreader.opentracing.jaeger.Process;
import org.palladiosimulator.pmxupgrade.model.inputreader.opentracing.jaeger.Trace;
import org.palladiosimulator.pmxupgrade.model.inputreader.opentracing.jaeger.TraceRecord;
import org.palladiosimulator.pmxupgrade.model.systemmodel.repository.ExecutionEnvironmentRepository;
import org.palladiosimulator.pmxupgrade.model.systemmodel.repository.SystemModelRepository;

import java.util.Map.Entry;

public class ComponentDetectionService {

    public SystemModelRepository detectComponents(SystemModelRepository systemModelRepository, TraceRecord traceRecord){
        SystemModelRepository systemModel = detectExecutionEnvironment(systemModelRepository, traceRecord);

        return systemModel;
    }

    private SystemModelRepository detectExecutionEnvironment(SystemModelRepository systemModelRepository, TraceRecord traceRecord){
        ExecutionEnvironmentRepository executionEnvironmentRepository = systemModelRepository.getExecutionEnvironmentFactory();

        for (Trace trace : traceRecord.getData()) {
            for (Entry<String, Process> stringProcessEntry : trace.getProcesses().entrySet()) {
                Entry pair = stringProcessEntry;
                Process p = (Process) pair.getValue();
                if (!executionEnvironmentRepository.exists(p.getId())) {
                    executionEnvironmentRepository.createAndRegisterExecutionContainer(p.getServiceName());
                }
            }
        }
        return systemModelRepository;
    }


}
