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
import de.kit.research.model.systemmodel.repository.AllocationRepository;
import de.kit.research.model.systemmodel.repository.OperationRepository;
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
    AtomicInteger numberOfValidExecutions = new AtomicInteger(-1);

    // TODO change
    public SystemModelRepository systemModelRepository;
    public List<ExecutionTrace> executionTraces = new ArrayList<>();

    public List<Execution> invalidExecutions = new ArrayList<>();

    @Override
    public TraceRecord filter(Configuration configuration, TraceRecord traceRecord) {
        systemModelRepository = new SystemModelRepository();

        traceRecord.getData().forEach(t -> executionTraces.add(mapExectionTraces(t)));

        executionTraces.forEach(t -> mapMessageTraces(t, t.getTraceId()));

        return traceRecord;
    }

    private ExecutionTrace mapExectionTraces(Trace trace) {
        numberOfValidExecutions.set(-1);

        ExecutionTrace executionTrace = new ExecutionTrace(trace.getTraceID());
        invalidExecutions = new ArrayList<>();

        HashMap<String, String> executionContainer = new HashMap<>();

        for (Map.Entry<String, Process> stringProcessEntry : trace.getProcesses().entrySet()) {
            Map.Entry pair = stringProcessEntry;
            Process p = (Process) pair.getValue();
            String executionContainerName = p.getServiceName().toUpperCase() + "-SRV";
            systemModelRepository.getExecutionEnvironmentFactory()
                    .lookupExecutionContainerByNamedIdentifier(executionContainerName);
            executionContainer.put((String) pair.getKey(), executionContainerName);
        }

        trace.getSpans().forEach(t -> {
            try {
                Execution execution = mapExecution(trace, t, executionContainer);
                if (execution != null)
                    executionTrace.add(execution);
            } catch (InvalidTraceException e) {
                // TODO Exception handling
                e.printStackTrace();
            }

        });
        executionTrace.getInvalidExecutions().addAll(invalidExecutions);
        return executionTrace;
    }

    private Execution mapExecution(Trace trace, Span span, HashMap<String, String> executionContainer) {
        String executionContainerId = span.getProcessID();

        final String executionContainerName = executionContainer.get(executionContainerId);
        String assemblyComponentTypeName = span.getComponentType();
        String allocationComponentName = executionContainerName + "::" + assemblyComponentTypeName;
        String operationFactoryName = assemblyComponentTypeName + "." + span.getOperationName();

        // TODO handleQueries
        if (StringUtils.isEmpty(assemblyComponentTypeName)) {

            long tout = span.getStartTime() + span.getDuration();

            if (StringUtils.equalsIgnoreCase(span.getSpanID(), span.getTraceID()) && span.getReferences() == null) {
                // if first

                if (StringUtils.equalsIgnoreCase(span.getOperationName(), "GET") | StringUtils.equalsIgnoreCase(span.getOperationName(), "POST") |
                        StringUtils.equalsIgnoreCase(span.getOperationName(), "PUT") | StringUtils.equalsIgnoreCase(span.getOperationName(), "DELETE")
                        | StringUtils.equalsIgnoreCase(span.getOperationName(), "HEAD") | StringUtils.equalsIgnoreCase(span.getOperationName(), "OPTIONS")) {
                    // if first and network

                    assemblyComponentTypeName = "de.kit.research.generic.HttpForwarder";
                    allocationComponentName = executionContainerName + "::" + assemblyComponentTypeName;
                    span.setOperationName("networkCall");
                    span.setOperationParameters(emptyArray);
                    operationFactoryName = assemblyComponentTypeName + "." + span.getOperationName();


                } else if (StringUtils.equalsIgnoreCase(span.getOperationName(), "QUERY")) {
                    // if first and query

                    assemblyComponentTypeName = "de.kit.research.generic.DatabaseForwarder";
                    allocationComponentName = executionContainerName + "::" + assemblyComponentTypeName;
                    span.setOperationName("databaseCall");
                    span.setOperationParameters(emptyArray);
                    operationFactoryName = assemblyComponentTypeName + "." + span.getOperationName();

                } else {
                    // invalid
                    Operation op = new Operation(-1, null, new Signature(span.getOperationName(), new String[0], null, new String[0]));
                    invalidExecutions.add(new Execution(op, new AllocationComponent(-1, null, null), span.getTraceID(), span.getSpanID(), Execution.NO_SESSION_ID, span.getChildOf(), -1, -1, span.getStartTime(), tout, false));
                    return null;

                }
            } else {
                // not first

                if (StringUtils.equalsIgnoreCase(span.getOperationName(), "GET") | StringUtils.equalsIgnoreCase(span.getOperationName(), "POST") |
                        StringUtils.equalsIgnoreCase(span.getOperationName(), "PUT") | StringUtils.equalsIgnoreCase(span.getOperationName(), "DELETE")
                        | StringUtils.equalsIgnoreCase(span.getOperationName(), "HEAD") | StringUtils.equalsIgnoreCase(span.getOperationName(), "OPTIONS")) {
                    // if not first and network

                    assemblyComponentTypeName = "de.kit.research.generic.HttpForwarder";
                    allocationComponentName = executionContainerName + "::" + assemblyComponentTypeName;
                    span.setOperationName("networkCall");
                    span.setOperationParameters(emptyArray);
                    operationFactoryName = assemblyComponentTypeName + "." + span.getOperationName();


                } else if (StringUtils.equalsIgnoreCase(span.getOperationName(), "QUERY")) {
                    // if not first and query

                    assemblyComponentTypeName = "de.kit.research.generic.DatabaseForwarder";
                    allocationComponentName = executionContainerName + "::" + assemblyComponentTypeName;
                    span.setOperationName("databaseCall");
                    span.setOperationParameters(emptyArray);
                    operationFactoryName = assemblyComponentTypeName + "." + span.getOperationName();

                } else {
                    // invalid
                    Operation op = new Operation(-1, null, new Signature(span.getOperationName(), new String[0], null, new String[0]));
                    invalidExecutions.add(new Execution(op, new AllocationComponent(-1, null, null), span.getTraceID(), span.getSpanID(), Execution.NO_SESSION_ID, span.getChildOf(), -1, -1, span.getStartTime(), tout, false));
                    return null;
                }

            }


        }
        numberOfValidExecutions.getAndIncrement();

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
        return new Execution(op, allocInst, span.getTraceID(), span.getSpanID(), Execution.NO_SESSION_ID, span.getChildOf(), numberOfValidExecutions.get(), numberOfValidExecutions.get(), span.getStartTime(), tout, false);
    }

    private void mapMessageTraces(ExecutionTrace executionTrace, String traceId) {
        Execution rootExecution =
                new Execution(OperationRepository.ROOT_OPERATION, AllocationRepository.ROOT_ALLOCATION_COMPONENT, "-1", "-1", "-1", "-1", -1, -1, -1, -1, false);

        executionTrace.toMessageTrace(rootExecution);
    }

}
