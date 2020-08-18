package de.kit.research.logic;

import de.kit.research.logic.dataprocessing.DataProcessingService;
import de.kit.research.logic.dataprocessing.controlflow.graph.AbstractDependencyGraph;
import de.kit.research.logic.dataprocessing.controlflow.graph.CallDecoration;
import de.kit.research.logic.dataprocessing.controlflow.graph.DependencyGraphNode;
import de.kit.research.logic.dataprocessing.controlflow.graph.WeightedBidirectionalDependencyGraphEdge;
import de.kit.research.logic.filter.opentracing.TimestampFilter;
import de.kit.research.logic.filter.opentracing.TraceIdFilter;
import de.kit.research.logic.filter.opentracing.TraceReconstructionFilter;
import de.kit.research.logic.inputreader.InputReaderInterface;
import de.kit.research.logic.inputreader.impl.InputReaderOpenTracingImpl;
import de.kit.research.logic.modelcreation.PerformanceModelCreationService;
import de.kit.research.logic.modelcreation.builder.CSVBuilder;
import de.kit.research.logic.modelcreation.builder.IModelBuilder;
import de.kit.research.logic.modelcreation.builder.ModelBuilder;
import de.kit.research.logic.modelcreation.creator.PerformanceModelCreator;
import de.kit.research.model.common.Configuration;
import de.kit.research.model.constants.PMXConstants;
import de.kit.research.model.exception.PMXException;
import de.kit.research.model.graph.AbstractGraph;
import de.kit.research.model.inputreader.InputObjectWrapper;
import de.kit.research.model.inputreader.ProcessingObjectWrapper;
import de.kit.research.model.inputreader.opentracing.jaeger.TraceRecord;
import de.kit.research.model.systemmodel.component.AssemblyComponent;
import de.kit.research.model.systemmodel.repository.SystemModelRepository;
import de.kit.research.model.systemmodel.trace.ExternalCall;
import de.kit.research.model.systemmodel.trace.Operation;
import de.kit.research.model.systemmodel.trace.TraceInformation;
import de.kit.research.model.systemmodel.util.AllocationComponentOperationPair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class PMXController {

    private static final Logger log = LogManager.getLogger(PMXController.class);

    private InputReaderInterface inputReaderInterface;
    private DataProcessingService dataProcessingService;
    private PerformanceModelCreationService performanceModelCreationService;

    private IModelBuilder builder;

    private Configuration configuration;
    private InputObjectWrapper inputObjectWrapper;
    private TraceRecord traceRecord;
    private ProcessingObjectWrapper processingObjectWrapper;

    private HashMap<String, List<Double>> workload;
    private AbstractGraph<DependencyGraphNode<AllocationComponentOperationPair>, WeightedBidirectionalDependencyGraphEdge<AllocationComponentOperationPair>, TraceInformation> operationGraph;
    private HashMap<String, Double> resourceDemands;

    public PMXController(Configuration configuration) {
        // TODO validate config
        this.configuration = configuration;
        inputReaderInterface = new InputReaderOpenTracingImpl();
        dataProcessingService = new DataProcessingService();

    }

    public void readTracingData() throws PMXException {
        try {
            inputObjectWrapper = inputReaderInterface.readTracingData(configuration);
            traceRecord = (TraceRecord) inputObjectWrapper;
        } catch (IOException e) {
            throw new PMXException(PMXConstants.FEHLER_DATENEINLESE);
        }
    }

    public void initAndExecuteFilters() {
        TimestampFilter timestampFilter = new TimestampFilter();
        traceRecord = timestampFilter.filter(configuration, traceRecord);

        TraceIdFilter traceIdFilter = new TraceIdFilter();
        traceRecord = traceIdFilter.filter(configuration, traceRecord);

        TraceReconstructionFilter traceReconstructionFilter = new TraceReconstructionFilter();
        processingObjectWrapper = traceReconstructionFilter.filter(configuration, traceRecord);
    }

    public void processTracingData() {
        workload = dataProcessingService.analyzeWorkload(processingObjectWrapper.getExecutionTraces());
        operationGraph = dataProcessingService.resolveControlFlow(processingObjectWrapper.getExecutionTraces(), processingObjectWrapper.getSystemModelRepository());
        resourceDemands = dataProcessingService.calculateResourceDemands(processingObjectWrapper.getExecutionTraces());

        // TODO call DataProcessingService
    }

    public void createPerformanceModel() throws PMXException {
        performanceModelCreationService = new PerformanceModelCreationService(configuration, getBuilder(), processingObjectWrapper.getSystemModelRepository());
        performanceModelCreationService.inputWorkload(workload);
        performanceModelCreationService.inputGraph(operationGraph);
        performanceModelCreationService.inputResourceDemands(resourceDemands);
        // performanceModelCreationService.addCPUCoreNumbers();

        performanceModelCreationService.createPerformanceModel();
    }

    public InputObjectWrapper getInputObjectWrapper() {
        return inputObjectWrapper;
    }

    public void setInputObjectWrapper(InputObjectWrapper inputObjectWrapper) {
        this.inputObjectWrapper = inputObjectWrapper;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public TraceRecord getTraceRecord() {
        return traceRecord;
    }

    public void setTraceRecord(TraceRecord traceRecord) {
        this.traceRecord = traceRecord;
    }

    public IModelBuilder getBuilder() {
        if (builder == null) {
            log.error("builder not initialized");
        }
        return builder;
    }

    public void setBuilder(IModelBuilder builder) {
        this.builder = builder;
    }
}
