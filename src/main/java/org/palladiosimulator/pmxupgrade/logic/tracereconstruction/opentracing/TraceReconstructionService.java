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

/**
 * Service for reconstructing the traces. This refers to the transformation of the input-dependent
 * tracing data into an uniform internal data format. In addition, the system architecture is extracted as this
 * is also an input-dependent step.
 *
 * @author Patrick Treyer
 */
public class TraceReconstructionService implements TraceReconstructionInterface {

    private final String[] emptyArray = new String[0];
    private AtomicInteger numberOfValidExecutions = new AtomicInteger(-1);

    private SystemModelRepository systemModelRepository;
    private List<ExecutionTrace> executionTraces = new ArrayList<>();
    private List<Execution> invalidExecutions = new ArrayList<>();

    private final String GENERIC_ASSEMBLY_COMPONENT_TYPE = "org.palladiosimulatpr.pmxupgrade.generic.";
    private final String UNKNOWN_ASSEMBLY_COMPONENT_TYPE = "unknown";
    private final String DATABASE_CALL = "databaseCall";
    private final String NETWORK_CALL = "networkCall";
    private final String HTTP_CLIENT = "HttpClient";
    private final String DATABASE_DRIVER = "DatabaseDriver";
    private final String SRV_INDICATION = "-SRV";


    @Override
    public ProcessingObjectWrapper reconstructTrace(Configuration configuration, TraceRecord traceRecord) throws InvalidTraceException {
        systemModelRepository = new SystemModelRepository();

        for (Trace t : traceRecord.getData()) {
            executionTraces.add(mapExectionTraces(t));
        }
        executionTraces.forEach(this::mapMessageTraces);

        return new ProcessingObjectWrapper(systemModelRepository, executionTraces, invalidExecutions);
    }

    /**
     * Transforms the trace into an internal data format and extracts information about the system architecture.
     *
     * @param trace, the {@link Trace} which has to be transformed.
     * @return the transformed @{@link Execution}
     * @throws InvalidTraceException, if the trace is invalid.
     */
    private ExecutionTrace mapExectionTraces(Trace trace) throws InvalidTraceException {
        numberOfValidExecutions.set(-1);

        ExecutionTrace executionTrace = new ExecutionTrace(trace.getTraceID());
        invalidExecutions = new ArrayList<>();

        HashMap<String, String> executionContainer = new HashMap<>();

        for (Map.Entry<String, Process> stringProcessEntry : trace.getProcesses().entrySet()) {
            Map.Entry pair = stringProcessEntry;
            Process p = (Process) pair.getValue();
            String executionContainerName = p.getServiceName().toUpperCase() + SRV_INDICATION;
            systemModelRepository.getExecutionEnvironmentFactory()
                    .lookupExecutionContainerByNamedIdentifier(executionContainerName);
            executionContainer.put((String) pair.getKey(), executionContainerName);
        }

        for (Span t : trace.getSpans()) {
            Execution execution = mapExecution(t, executionContainer);
            if (execution != null)
                executionTrace.add(execution);
        }
        executionTrace.getInvalidExecutions().addAll(invalidExecutions);
        return executionTrace;
    }

    private Execution mapExecution(Span span, HashMap<String, String> executionContainer) {
        String executionContainerId = span.getProcessID();

        final String executionContainerName = executionContainer.get(executionContainerId);
        String assemblyComponentTypeName = span.getComponentType();
        String allocationComponentName = executionContainerName + "::" + assemblyComponentTypeName;
        String operationFactoryName = assemblyComponentTypeName + "." + span.getOperationName();

        if (StringUtils.isEmpty(assemblyComponentTypeName)) {

            long tout = span.getStartTime() + span.getDuration();

            if (StringUtils.equalsIgnoreCase(span.getOperationName(), "GET") | StringUtils.equalsIgnoreCase(span.getOperationName(), "POST") |
                    StringUtils.equalsIgnoreCase(span.getOperationName(), "PUT") | StringUtils.equalsIgnoreCase(span.getOperationName(), "DELETE")
                    | StringUtils.equalsIgnoreCase(span.getOperationName(), "HEAD") | StringUtils.equalsIgnoreCase(span.getOperationName(), "OPTIONS")) {
                // if network call

                // look if there is a component auto instrumented, if not use generic component
                String component = span.getComponent();
                if (StringUtils.isNotEmpty(span.getComponent()) && !component.startsWith(UNKNOWN_ASSEMBLY_COMPONENT_TYPE)) {
                    String name = StringUtils.replace(component, "-", " ");
                    String capitalizedName = StringUtils.capitaliseAllWords(name).replace(" ", "");
                    assemblyComponentTypeName = GENERIC_ASSEMBLY_COMPONENT_TYPE + capitalizedName;
                } else {
                    assemblyComponentTypeName = GENERIC_ASSEMBLY_COMPONENT_TYPE + HTTP_CLIENT;
                }
                allocationComponentName = executionContainerName + "::" + assemblyComponentTypeName;
                span.setOperationName(NETWORK_CALL);
                span.setOperationParameters(emptyArray);
                operationFactoryName = assemblyComponentTypeName + "." + span.getOperationName();


            } else if (StringUtils.equalsIgnoreCase(span.getOperationName(), "QUERY") | StringUtils.equalsIgnoreCase(span.getOperationName(), "UPDATE")
                    | StringUtils.equalsIgnoreCase(span.getOperationName(), "DELETE") | StringUtils.equalsIgnoreCase(span.getOperationName(), "SELECT")) {
                // if database call

                // look if there is a component auto instrumented, if not use generic component
                String component = span.getComponent();
                if (StringUtils.isNotEmpty(span.getComponent()) && !component.startsWith(UNKNOWN_ASSEMBLY_COMPONENT_TYPE)) {
                    String name = StringUtils.replace(component, "-", " ");
                    String capitalizedName = StringUtils.capitaliseAllWords(name).replace(" ", "");
                    assemblyComponentTypeName = GENERIC_ASSEMBLY_COMPONENT_TYPE + capitalizedName;
                } else {
                    assemblyComponentTypeName = GENERIC_ASSEMBLY_COMPONENT_TYPE + DATABASE_DRIVER;
                }
                allocationComponentName = executionContainerName + "::" + assemblyComponentTypeName;
                span.setOperationName(DATABASE_CALL);
                span.setOperationParameters(emptyArray);
                operationFactoryName = assemblyComponentTypeName + "." + span.getOperationName();

            } else {
                // invalid
                Operation op = new Operation(-1, null, new Signature(span.getOperationName(), new String[0], null, new String[0]));
                invalidExecutions.add(new Execution(op, new AllocationComponent(-1, null, null), span.getTraceID(), span.getSpanID(), Execution.NO_SESSION_ID, span.getChildOf(), -1, -1, span.getStartTime(), tout, false));
                return null;
            }
        }
        numberOfValidExecutions.getAndIncrement();

        AllocationComponent allocInst = systemModelRepository.getAllocationFactory()
                .lookupAllocationComponentInstanceByNamedIdentifier(allocationComponentName);

        // Allocation component instance doesn't exist
        if (allocInst == null) {
            AssemblyComponent assemblyComponent = systemModelRepository.getAssemblyFactory()
                    .lookupAssemblyComponentInstanceByNamedIdentifier(assemblyComponentTypeName);

            // assembly instance doesn't exist
            if (assemblyComponent == null) {
                ComponentType componentType = systemModelRepository.getTypeRepositoryFactory().lookupComponentTypeByNamedIdentifier(assemblyComponentTypeName);

                if (componentType == null) {
                    // Component type doesn't exist
                    componentType = systemModelRepository.getTypeRepositoryFactory().createAndRegisterComponentType(assemblyComponentTypeName,
                            assemblyComponentTypeName);
                }
                assemblyComponent = systemModelRepository.getAssemblyFactory()
                        .createAndRegisterAssemblyComponentInstance(assemblyComponentTypeName, componentType);
            }

            ExecutionContainer execContainer = systemModelRepository.getExecutionEnvironmentFactory()
                    .lookupExecutionContainerByNamedIdentifier(executionContainerName);

            // doesn't exist, yet
            if (execContainer == null) {
                execContainer = systemModelRepository.getExecutionEnvironmentFactory()
                        .createAndRegisterExecutionContainer(executionContainerName);
            }

            allocInst = systemModelRepository.getAllocationFactory()
                    .createAndRegisterAllocationComponentInstance(allocationComponentName, assemblyComponent, execContainer);
        }

        Signature operationSignature = new Signature(span.getOperationName(), emptyArray, span.getReturnType(), span.getParameters());
        Operation op = systemModelRepository.getOperationFactory().lookupOperationByNamedIdentifier(operationFactoryName);

        // Operation doesn't exist
        if (op == null) {
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
