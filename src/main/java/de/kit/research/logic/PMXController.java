package de.kit.research.logic;

import de.kit.research.logic.dataprocessing.DataProcessingService;
import de.kit.research.logic.dataprocessing.controlflow.graph.DependencyGraphNode;
import de.kit.research.logic.dataprocessing.controlflow.graph.WeightedBidirectionalDependencyGraphEdge;
import de.kit.research.logic.filter.opentracing.TimestampFilter;
import de.kit.research.logic.filter.opentracing.TraceIdFilter;
import de.kit.research.logic.filter.opentracing.TraceReconstructionFilter;
import de.kit.research.logic.inputreader.InputReaderInterface;
import de.kit.research.logic.inputreader.impl.InputReaderOpenTracingImpl;
import de.kit.research.logic.modelcreation.PerformanceModelCreationService;
import de.kit.research.logic.modelcreation.builder.IModelBuilder;
import de.kit.research.model.common.Configuration;
import de.kit.research.model.constants.PMXConstants;
import de.kit.research.model.exception.PMXException;
import de.kit.research.model.graph.AbstractGraph;
import de.kit.research.model.inputreader.InputObjectWrapper;
import de.kit.research.model.inputreader.ProcessingObjectWrapper;
import de.kit.research.model.inputreader.opentracing.jaeger.TraceRecord;
import de.kit.research.model.systemmodel.trace.TraceInformation;
import de.kit.research.model.systemmodel.util.AllocationComponentOperationPair;
import org.codehaus.plexus.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

// TODO change method signature
public class PMXController {

    private final InputReaderInterface inputReaderInterface;
    private final DataProcessingService dataProcessingService;
    private IModelBuilder modelBuilder;

    private Configuration configuration;
    private InputObjectWrapper inputObjectWrapper;
    private TraceRecord traceRecord;
    private ProcessingObjectWrapper processingObjectWrapper;
    private HashMap<String, List<Double>> workload;
    private AbstractGraph<DependencyGraphNode<AllocationComponentOperationPair>, WeightedBidirectionalDependencyGraphEdge<AllocationComponentOperationPair>, TraceInformation> operationGraph;
    private HashMap<String, Double> resourceDemands;

    public PMXController(Configuration configuration) throws PMXException {
        validateConfiguration(configuration);

        this.configuration = configuration;
        inputReaderInterface = new InputReaderOpenTracingImpl();
        dataProcessingService = new DataProcessingService();
    }

    public PMXController(Configuration configuration, IModelBuilder modelBuilder) throws PMXException {
        validateConfiguration(configuration);

        this.configuration = configuration;
        this.modelBuilder = modelBuilder;
        inputReaderInterface = new InputReaderOpenTracingImpl();
        dataProcessingService = new DataProcessingService();
    }

    public void buildPerformanceModel() throws PMXException {
        readTracingData();
        initAndExecuteFilters();
        processTracingData();
        createModel();
    }

    public void readTracingData() throws PMXException {
        try {
            inputObjectWrapper = inputReaderInterface.readTracingData(configuration);
            traceRecord = (TraceRecord) inputObjectWrapper;
        } catch (IOException e) {
            throw new PMXException(PMXConstants.ERROR_DATA_INPUT + e.getMessage());
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
        // TODO call FailureEstimation
    }

    public void createModel() throws PMXException {
        PerformanceModelCreationService performanceModelCreationService = new PerformanceModelCreationService(configuration, getModelBuilder(), processingObjectWrapper.getSystemModelRepository());
        performanceModelCreationService.inputWorkload(workload);
        performanceModelCreationService.inputGraph(operationGraph);
        performanceModelCreationService.inputResourceDemands(resourceDemands);

        performanceModelCreationService.createPerformanceModel();
    }

    private void validateConfiguration(Configuration configuration) throws PMXException {
        if (configuration == null) {
            throw new PMXException(PMXConstants.ERROR_CONFIG);
        }
        if (StringUtils.isEmpty(configuration.getOutputDirectory())) {
            throw new PMXException(PMXConstants.ERROR_CONFIG_OUTPUT_DIR);
        }
        if (StringUtils.isEmpty(configuration.getInputFileName())) {
            throw new PMXException(PMXConstants.ERROR_CONFIG_INPUT_DIR);
        }
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

    public IModelBuilder getModelBuilder() throws PMXException {
        if (modelBuilder == null) {
            throw new PMXException(PMXConstants.ERROR_BUILDER);
        }
        return modelBuilder;
    }

    public void setModelBuilder(IModelBuilder builder) {
        this.modelBuilder = builder;
    }
}
