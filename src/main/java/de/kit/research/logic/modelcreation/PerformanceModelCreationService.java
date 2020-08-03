package de.kit.research.logic.modelcreation;

import de.kit.research.logic.dataprocessing.controlflow.graph.CallDecoration;
import de.kit.research.logic.dataprocessing.controlflow.graph.DependencyGraphNode;
import de.kit.research.logic.dataprocessing.controlflow.graph.WeightedBidirectionalDependencyGraphEdge;
import de.kit.research.logic.modelcreation.builder.CSVBuilder;
import de.kit.research.logic.modelcreation.builder.IModelBuilder;
import de.kit.research.logic.modelcreation.builder.ModelBuilder;
import de.kit.research.logic.modelcreation.creator.PerformanceModelCreator;
import de.kit.research.model.common.Configuration;
import de.kit.research.model.graph.AbstractGraph;
import de.kit.research.model.systemmodel.component.AssemblyComponent;
import de.kit.research.model.systemmodel.repository.SystemModelRepository;
import de.kit.research.model.systemmodel.trace.ExternalCall;
import de.kit.research.model.systemmodel.trace.Operation;
import de.kit.research.model.systemmodel.trace.TraceInformation;
import de.kit.research.model.systemmodel.util.AllocationComponentOperationPair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Transforms the contents of a SystemModelRepository + graph + resource demands to the model of the builder
 */
public class PerformanceModelCreationService {

    private static final Logger log = LogManager.getLogger(PerformanceModelCreationService.class);

    /**
     * By default, writes output files to this file in the working directory.
     */
    private final String outputDir;
    private final IModelBuilder builder;
    private AbstractGraph<DependencyGraphNode<AllocationComponentOperationPair>, WeightedBidirectionalDependencyGraphEdge<AllocationComponentOperationPair>, TraceInformation> operationGraph;
    private HashMap<String, Double> resourceDemands; // = new HashMap<String, Double>();
    private HashMap<String, List<Double>> workload; // = new HashMap<String, Vector<Double>>();
    private SystemModelRepository systemEntityFactory;
    private HashMap<String, Integer> numCores = new HashMap<>();


    public PerformanceModelCreationService(final Configuration configuration, IModelBuilder builder, SystemModelRepository systemModelRepository) {
        this.outputDir = configuration.getOutputDirectory();
        this.builder = builder;
        this.systemEntityFactory = systemModelRepository;
    }

    public void inputResourceDemands(
            final HashMap<String, Double> resourceDemands) {
        log.info("received resource demands");
        this.resourceDemands = resourceDemands;
    }

    public void inputGraph(
            final AbstractGraph<DependencyGraphNode<AllocationComponentOperationPair>, WeightedBidirectionalDependencyGraphEdge<AllocationComponentOperationPair>, TraceInformation> operationGraph) {
        log.info("received operation graph");
        this.operationGraph = operationGraph;
    }

    public void inputWorkload(final HashMap<String, List<Double>> workload) {
        log.info("received workload");
        this.workload = workload;
    }

    public void buildModel(final boolean errorBeforeTermination) {
        // TODO Exceptions instead of boolean

        log.info("starting model creation " + "|||||||||||||||||||||||||||||||||||||||||||||||||");
        /**
         * Used to keep track of whether an error occurred, regardless of
         * whether before or during termination.
         */
        boolean error = errorBeforeTermination;
        if (operationGraph == null) {
            log.error("No operation graph has been passed!");
            error = true;
        }
        if (resourceDemands == null) {
            log.error("\tNo resource demand set has been passed!");
            error = true;
        } else if (resourceDemands.keySet().isEmpty()) {
            log.warn("\tPassed resource demand set is empty!");
            //error = true;
        }
        if (workload == null) {
            log.error("\tNo workload set has been passed!");
            error = true;
        } else if (workload.keySet().isEmpty()) {
            log.error("\tPassed workload is empty!");
            error = true;
        }

        if (!error) {
            buildPerformanceModel(operationGraph, resourceDemands, workload,
                    this.systemEntityFactory, numCores, builder);
            //super.terminate(errorBeforeTermination);

            //log.info("writing results to files... |||||||||||||||||||||||||||||||||||||||||||||||||");
            builder.saveToFile(outputDir);
        } else {
            //super.terminate(errorBeforeTermination);
            //throw new InternalError();
            //System.exit(0);
        }
    }

    public void addCPUCoreNumbers(HashMap<String, Integer> numCores) {
        this.numCores = numCores;
    }

    private static void buildPerformanceModel(
            AbstractGraph<DependencyGraphNode<AllocationComponentOperationPair>, WeightedBidirectionalDependencyGraphEdge<AllocationComponentOperationPair>, TraceInformation> operationGraph,
            HashMap<String, Double> resourceDemands,
            HashMap<String, List<Double>> workload,
            SystemModelRepository systemModelRepository, HashMap<String, Integer> numCores, IModelBuilder builder) {

        PerformanceModelCreator.createExecutionContainers(
                systemModelRepository, builder, numCores);
        PerformanceModelCreator.createComponentsAndInterfaces(
                systemModelRepository, builder);

        for (DependencyGraphNode<AllocationComponentOperationPair> node : operationGraph
                .getVertices()) {
            AssemblyComponent component = node.getEntity()
                    .getAllocationComponent().getAssemblyComponent();
            String componentName = ModelBuilder.applyNameFixes(component.getType().getTypeName());
            String hostName = node.getEntity().getAllocationComponent()
                    .getExecutionContainer().getName();
            // Teerat Pitakrat: TODO add allocation information to assembly

            if (componentName.equals("'Entry'")) {
                continue; // System entry node
            }
            builder.addAssembly(componentName + ModelBuilder.seperatorChar + hostName);
            builder.addComponentToAssembly(componentName + ModelBuilder.seperatorChar + hostName,
                    componentName);

            Operation method = node.getEntity().getOperation();
            String methodName = node.getEntity().getOperation().getSignature()
                    .getName();
            CallDecoration decoration = node
                    .getDecoration(CallDecoration.class);
            int numIncomingCalls = Integer.parseInt(decoration
                    .createFormattedOutput());
            List<ExternalCall> externalCalls = new ArrayList<>();

            log.info("\t" + hostName + " " + componentName + " " + methodName
                    + " " + numIncomingCalls + "x called externally");

            for (WeightedBidirectionalDependencyGraphEdge<AllocationComponentOperationPair> outgoingEdge : node
                    .getOutgoingEdges()) {
                helperMethod(builder, componentName, hostName, numIncomingCalls,
                        externalCalls, outgoingEdge);
            }

            Double meanResourceDemand = resourceDemands.get(componentName + ModelBuilder.seperatorChar
                    + methodName + ModelBuilder.seperatorChar + hostName);
            if (meanResourceDemand == null) {
                log.warn("\t\tNo resource demand found for " + componentName
                        + ModelBuilder.seperatorChar + methodName + ModelBuilder.seperatorChar + hostName);
                log.info(resourceDemands);
                log.info("\t\tresource demand has been set to 0.0");
                meanResourceDemand = 0.0;
            }
            log.info("Try to create behavior description for" + componentName + method.getSignature().getName());
            builder.addSEFF(componentName, method.getSignature().getName(),
                    externalCalls, hostName, meanResourceDemand);
        }

        PerformanceModelCreator.createAllocations(systemModelRepository,
                builder);

        Double averageNetworkDelay = resourceDemands.get("Network");
        double throughput = 10000000;
        if (averageNetworkDelay != null) {
            builder.createNetwork(averageNetworkDelay, throughput);
            log.info("\tnetwork");
            log.info("\t\taverageNetworkDelay set to " + averageNetworkDelay
                    + ", throughput set to " + throughput + ".");
            log.warn("\t\tthroughput value is not based on any measurements");
        }

        HashSet<String> names = new HashSet<>();
        if (workload != null && !workload.keySet().isEmpty()) {
            for (String key : workload.keySet()) {
                String className = key.split(ModelBuilder.seperatorChar)[1];
                names.add(className);
            }
            CSVBuilder.setOutputDirectory(builder.getOutputDirectory() + File.separator + "workloads" + File.separator);
            CSVBuilder.workloadToCSV(workload);
            // CSVBuilder.createInterarrivalCSVs(workload);
            // CSVBuilder.createCombinedCSV(workload);
            // CSVBuilder.createPetClinicCSVs(workload);
            log.info(names);
            builder.addUsageScenario(workload);
        }
    }


    private static void helperMethod(
            IModelBuilder builder,
            String componentName,
            String hostName,
            int numIncomingCalls,
            List<ExternalCall> externalCalls,
            WeightedBidirectionalDependencyGraphEdge<AllocationComponentOperationPair> outgoingEdge) {
        AllocationComponentOperationPair targetNode = outgoingEdge.getTarget()
                .getEntity();
        String targetComponentName = ModelBuilder.applyNameFixes(targetNode.getAllocationComponent()
                .getAssemblyComponent().getType().getTypeName());
        String calledMethodName = targetNode.getOperation().getSignature()
                .getName();
        String targetHostName = targetNode.getAllocationComponent()
                .getExecutionContainer().getName();

        // if(targetComponentName != componentName){
        builder.addProvidedRole(targetComponentName, "I" + targetComponentName);
        builder.addRequiredRole(componentName, "I" + targetComponentName);
        // }
        int numOutgoinCalls = outgoingEdge.getTargetWeight().intValue(); // TODO
        // think
        // about
        // adding
        // more
        // TraceInformation
        // to
        // graph

        double averageCalls = numOutgoinCalls / (double) numIncomingCalls;
        ExternalCall call = new ExternalCall(targetComponentName,
                calledMethodName, averageCalls);
        externalCalls.add(call);

        log.info("\t\tcalls on average " + call.getNumCalls() + "x "
                + targetComponentName + " " + call.getMethodName());

        builder.addAssembly(targetComponentName + ModelBuilder.seperatorChar + targetHostName);
        builder.addComponentToAssembly(targetComponentName + ModelBuilder.seperatorChar
                + targetHostName, targetComponentName);

        // if(targetComponentName != componentName){
        builder.addConnectionToAssemblies(componentName + ModelBuilder.seperatorChar + hostName,
                targetComponentName + ModelBuilder.seperatorChar + targetHostName);
        // }
    }
}
