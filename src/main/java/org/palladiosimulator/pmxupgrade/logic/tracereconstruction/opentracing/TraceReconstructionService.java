package org.palladiosimulator.pmxupgrade.logic.tracereconstruction.opentracing;

import org.palladiosimulator.pmxupgrade.logic.tracereconstruction.TraceReconstructionInterface;
import org.palladiosimulator.pmxupgrade.model.common.Configuration;
import org.palladiosimulator.pmxupgrade.model.exception.InvalidTraceException;
import org.palladiosimulator.pmxupgrade.model.inputreader.ProcessingObjectWrapper;
import org.palladiosimulator.pmxupgrade.model.inputreader.opentracing.jaeger.Process;
import org.palladiosimulator.pmxupgrade.model.inputreader.opentracing.jaeger.Span;
import org.palladiosimulator.pmxupgrade.model.inputreader.opentracing.jaeger.Trace;
import org.palladiosimulator.pmxupgrade.model.inputreader.opentracing.jaeger.TraceRecord;
import org.palladiosimulator.pmxupgrade.model.systemmodel.component.AllocationComponent;
import org.palladiosimulator.pmxupgrade.model.systemmodel.component.AssemblyComponent;
import org.palladiosimulator.pmxupgrade.model.systemmodel.component.ComponentType;
import org.palladiosimulator.pmxupgrade.model.systemmodel.component.ExecutionContainer;
import org.palladiosimulator.pmxupgrade.model.systemmodel.repository.AllocationRepository;
import org.palladiosimulator.pmxupgrade.model.systemmodel.repository.OperationRepository;
import org.palladiosimulator.pmxupgrade.model.systemmodel.repository.SystemModelRepository;
import org.palladiosimulator.pmxupgrade.model.systemmodel.trace.Execution;
import org.palladiosimulator.pmxupgrade.model.systemmodel.trace.ExecutionTrace;
import org.palladiosimulator.pmxupgrade.model.systemmodel.trace.Operation;
import org.palladiosimulator.pmxupgrade.model.systemmodel.util.Signature;
import org.codehaus.plexus.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TraceReconstructionService implements TraceReconstructionInterface {

    private final String[] emptyArray = new String[0];
    private AtomicInteger numberOfValidExecutions = new AtomicInteger(-1);

    private SystemModelRepository systemModelRepository;
    private List<ExecutionTrace> executionTraces = new ArrayList<>();

    // TODO unnecessary
    private List<Execution> invalidExecutions = new ArrayList<>();

    @Override
    public ProcessingObjectWrapper filter(Configuration configuration, TraceRecord traceRecord) {
        systemModelRepository = new SystemModelRepository();

        traceRecord.getData().forEach(t -> executionTraces.add(mapExectionTraces(t)));
        executionTraces.forEach(this::mapMessageTraces);

        return new ProcessingObjectWrapper(systemModelRepository, executionTraces, invalidExecutions);
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
                Execution execution = mapExecution(t, executionContainer);
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

    private Execution mapExecution(Span span, HashMap<String, String> executionContainer) {
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

                    // look if there is a omponent auto instrumented, if not use generic component
                    String component = span.getComponent();
                    if (StringUtils.isNotEmpty(span.getComponent()) && !component.startsWith("unknown")) {
                        String name = StringUtils.replace(component, "-", " ");
                        String name2 = StringUtils.capitaliseAllWords(name).replace(" ", "");
                        assemblyComponentTypeName = "org.palladiosimulatpr.pmxupgrade.generic." + name2;
                    } else {
                        assemblyComponentTypeName = "org.palladiosimulatpr.pmxupgrade.generic.HttpClient";
                    }
                    allocationComponentName = executionContainerName + "::" + assemblyComponentTypeName;
                    span.setOperationName("networkCall");
                    span.setOperationParameters(emptyArray);
                    operationFactoryName = assemblyComponentTypeName + "." + span.getOperationName();


                } else if (StringUtils.equalsIgnoreCase(span.getOperationName(), "QUERY") | StringUtils.equalsIgnoreCase(span.getOperationName(), "UPDATE")
                        | StringUtils.equalsIgnoreCase(span.getOperationName(), "DELETE") | StringUtils.equalsIgnoreCase(span.getOperationName(), "SELECT")) {
                    // if first and query

                    // look if there is a component auto instrumented, if not use generic component
                    String component = span.getComponent();
                    if (StringUtils.isNotEmpty(span.getComponent()) && !component.startsWith("unknown")) {
                        String name = StringUtils.replace(component, "-", " ");
                        String name2 = StringUtils.capitaliseAllWords(name).replace(" ", "");
                        assemblyComponentTypeName = "org.palladiosimulatpr.pmxupgrade.generic." + name2;
                    } else {
                        assemblyComponentTypeName = "org.palladiosimulatpr.pmxupgrade.generic.DatabaseDriver";
                    }
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

                    // look if there is a omponent auto instrumented, if not use generic component
                    String component = span.getComponent();
                    if (StringUtils.isNotEmpty(span.getComponent()) && !component.startsWith("unknown")) {
                        String name = StringUtils.replace(component, "-", " ");
                        String name2 = StringUtils.capitaliseAllWords(name).replace(" ", "");
                        assemblyComponentTypeName = "org.palladiosimulatpr.pmxupgrade.generic." + name2;
                    } else {
                        assemblyComponentTypeName = "org.palladiosimulatpr.pmxupgrade.generic.HttpClient";
                    }
                    allocationComponentName = executionContainerName + "::" + assemblyComponentTypeName;
                    span.setOperationName("networkCall");
                    span.setOperationParameters(emptyArray);
                    operationFactoryName = assemblyComponentTypeName + "." + span.getOperationName();


                } else if (StringUtils.equalsIgnoreCase(span.getOperationName(), "QUERY")) {
                    // if not first and query

                    // look if there is a omponent auto instrumented, if not use generic component
                    String component = span.getComponent();
                    if (StringUtils.isNotEmpty(span.getComponent()) && !component.startsWith("unknown")) {
                        String name = StringUtils.replace(component, "-", " ");
                        String name2 = StringUtils.capitaliseAllWords(name).replace(" ", "");
                        assemblyComponentTypeName = "org.palladiosimulatpr.pmxupgrade.generic." + name2;
                    } else {
                        assemblyComponentTypeName = "org.palladiosimulatpr.pmxupgrade.generic.DatabaseDriver";
                    }
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

    private void mapMessageTraces(ExecutionTrace executionTrace) {
        Execution rootExecution =
                new Execution(OperationRepository.ROOT_OPERATION, AllocationRepository.ROOT_ALLOCATION_COMPONENT, "-1", "-1", "-1", "-1", -1, -1, -1, -1, false);

        executionTrace.toMessageTrace(rootExecution);
    }

}
