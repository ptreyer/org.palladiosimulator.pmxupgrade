package de.kit.research.logic.dataprocessing.componentdetection;

import de.kit.research.model.inputreader.opentracing.jaeger.Process;
import de.kit.research.model.inputreader.opentracing.jaeger.Trace;
import de.kit.research.model.inputreader.opentracing.jaeger.TraceRecord;
import de.kit.research.model.systemmodel.repository.ExecutionEnvironmentRepository;
import de.kit.research.model.systemmodel.repository.SystemModelRepository;

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
