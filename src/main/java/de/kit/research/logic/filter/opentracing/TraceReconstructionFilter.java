package de.kit.research.logic.filter.opentracing;

import de.kit.research.logic.filter.AbstractTraceProcessingFilter;
import de.kit.research.model.common.Configuration;
import de.kit.research.model.exception.InvalidTraceException;
import de.kit.research.model.inputreader.opentracing.jaeger.Process;
import de.kit.research.model.inputreader.opentracing.jaeger.Span;
import de.kit.research.model.inputreader.opentracing.jaeger.Trace;
import de.kit.research.model.inputreader.opentracing.jaeger.TraceRecord;
import de.kit.research.model.systemmodel.component.AllocationComponent;
import de.kit.research.model.systemmodel.component.AssemblyComponent;
import de.kit.research.model.systemmodel.component.ComponentType;
import de.kit.research.model.systemmodel.component.ExecutionContainer;
import de.kit.research.model.systemmodel.repository.SystemModelRepository;
import de.kit.research.model.systemmodel.trace.Execution;
import de.kit.research.model.systemmodel.trace.ExecutionTrace;
import de.kit.research.model.systemmodel.trace.Operation;
import de.kit.research.model.systemmodel.util.Signature;
import org.codehaus.plexus.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TraceReconstructionFilter implements AbstractTraceProcessingFilter {

    private final String[] emptyArray = new String[0];
    // TODO change
    public SystemModelRepository systemModelRepository;


    @Override
    public TraceRecord filter(Configuration configuration, TraceRecord traceRecord) {
        systemModelRepository = new SystemModelRepository();

        List<ExecutionTrace> executionTrace = new ArrayList<>();
        traceRecord.getData().forEach(t -> executionTrace.add(mapExectionTraces(t)));

        return traceRecord;
    }

    private ExecutionTrace mapExectionTraces(Trace trace) {
        ExecutionTrace executionTrace = new ExecutionTrace(trace.getTraceID());

        HashMap<String, String> executionContainer = new HashMap<>();

        for (Map.Entry<String, Process> stringProcessEntry : trace.getProcesses().entrySet()) {
            Map.Entry pair = stringProcessEntry;
            Process p = (Process) pair.getValue();
            String executionContainerName = p.getServiceName().toUpperCase() + "-SRV";
            systemModelRepository.getExecutionEnvironmentFactory()
                    .lookupExecutionContainerByNamedIdentifier(executionContainerName);
            executionContainer.put((String) pair.getKey(), executionContainerName);
        }

        AtomicInteger number = new AtomicInteger(1);
        trace.getSpans().forEach(t -> {
            try {
                Execution execution = mapExecutions(number.get(), trace, t, executionContainer, executionTrace);
                if (execution != null)
                    executionTrace.add(execution);
                number.getAndIncrement();
            } catch (InvalidTraceException e) {
                // TODO Exception handling
                e.printStackTrace();
            }

        });

        return executionTrace;
    }

    private Execution mapExecutions(int eoi, Trace trace, Span span, HashMap<String, String> executionContainer, ExecutionTrace executionTrace) {
        // Execution execution = new Execution();

        String executionContainerId = span.getProcessID();

        final String executionContainerName = executionContainer.get(executionContainerId);
        final String assemblyComponentTypeName = span.getComponentType();
        final String allocationComponentName = executionContainerName + "::" + assemblyComponentTypeName;
        final String operationFactoryName = assemblyComponentTypeName + "." + span.getOperationName();

        // TODO handleQueries
        if(StringUtils.isEmpty(assemblyComponentTypeName))
            return null;

        AllocationComponent allocInst = systemModelRepository.getAllocationFactory()
                .lookupAllocationComponentInstanceByNamedIdentifier(allocationComponentName);

        if (allocInst == null) { // Allocation component instance doesn't exist
            AssemblyComponent assemblyComponent = systemModelRepository.getAssemblyFactory()
                    .lookupAssemblyComponentInstanceByNamedIdentifier(assemblyComponentTypeName);
            if (assemblyComponent == null) { // assembly instance doesn't exist
                ComponentType componentType = systemModelRepository.getTypeRepositoryFactory().lookupComponentTypeByNamedIdentifier(assemblyComponentTypeName);
                if (componentType == null) { // NOPMD NOCS (NestedIf)
                    // Component type doesn't exist
                    componentType = systemModelRepository.getTypeRepositoryFactory().createAndRegisterComponentType(assemblyComponentTypeName,
                            assemblyComponentTypeName);
                }
                assemblyComponent = systemModelRepository.getAssemblyFactory()
                        .createAndRegisterAssemblyComponentInstance(assemblyComponentTypeName, componentType);
            }
            ExecutionContainer execContainer = systemModelRepository.getExecutionEnvironmentFactory()
                    .lookupExecutionContainerByNamedIdentifier(executionContainerName);
            if (execContainer == null) { // doesn't exist, yet
                execContainer = systemModelRepository.getExecutionEnvironmentFactory()
                        .createAndRegisterExecutionContainer(executionContainerName);
            }
            allocInst = systemModelRepository.getAllocationFactory()
                    .createAndRegisterAllocationComponentInstance(allocationComponentName, assemblyComponent, execContainer);
        }

        Signature operationSignature = new Signature(span.getOperationName(), emptyArray, span.getReturnType(), span.getParameters());
        Operation op = systemModelRepository.getOperationFactory().lookupOperationByNamedIdentifier(operationFactoryName);
        if (op == null) { // Operation doesn't exist
            op = systemModelRepository.getOperationFactory()
                    .createAndRegisterOperation(operationFactoryName, allocInst.getAssemblyComponent().getType(), operationSignature);
            allocInst.getAssemblyComponent().getType().addOperation(op);
        }

        long tout = span.getStartTime() + span.getDuration();
        return new Execution(op, allocInst, trace.getTraceID(), Execution.NO_SESSION_ID, eoi, trace.getSpans().size(), span.getStartTime(), tout, true);
    }

    private Operation mapOperation(Span span) {
        return null;
    }

}
