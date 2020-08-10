package de.kit.research.logic;

import de.kit.research.logic.dataprocessing.DataProcessingService;
import de.kit.research.logic.dataprocessing.controlflow.graph.AbstractDependencyGraph;
import de.kit.research.logic.filter.opentracing.TimestampFilter;
import de.kit.research.logic.filter.opentracing.TraceIdFilter;
import de.kit.research.logic.filter.opentracing.TraceReconstructionFilter;
import de.kit.research.logic.inputreader.InputReaderInterface;
import de.kit.research.logic.inputreader.impl.InputReaderOpenTracingImpl;
import de.kit.research.logic.modelcreation.PerformanceModelCreationService;
import de.kit.research.model.common.Configuration;
import de.kit.research.model.constants.PMXConstants;
import de.kit.research.model.exception.PMXException;
import de.kit.research.model.inputreader.InputObjectWrapper;
import de.kit.research.model.inputreader.ProcessingObjectWrapper;
import de.kit.research.model.inputreader.opentracing.jaeger.TraceRecord;
import de.kit.research.model.systemmodel.util.AllocationComponentOperationPair;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class PMXController {

    private InputReaderInterface inputReaderInterface;
    private DataProcessingService dataProcessingService;
    private PerformanceModelCreationService performanceModelCreationService;

    private Configuration configuration;
    private InputObjectWrapper inputObjectWrapper;
    private TraceRecord traceRecord;
    private ProcessingObjectWrapper processingObjectWrapper;

    private HashMap<String, List<Double>> timeSeriesMap;
    private AbstractDependencyGraph<AllocationComponentOperationPair> graph;

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

    public void initAndExecuteFilters(){
        TimestampFilter timestampFilter = new TimestampFilter();
        traceRecord = timestampFilter.filter(configuration, traceRecord);

        TraceIdFilter traceIdFilter = new TraceIdFilter();
        traceRecord = traceIdFilter.filter(configuration, traceRecord);

        TraceReconstructionFilter traceReconstructionFilter = new TraceReconstructionFilter();
        processingObjectWrapper = traceReconstructionFilter.filter(configuration, traceRecord);
    }

    public void processTracingData(){
        timeSeriesMap = dataProcessingService.analyzeWorkload(processingObjectWrapper.getExecutionTraces());
        graph = dataProcessingService.resolveControlFlow(processingObjectWrapper.getExecutionTraces(), processingObjectWrapper.getSystemModelRepository());



        // TODO call DataProcessingService
    }

    public void createPerformanceModel(){

        //performanceModelCreationService = new PerformanceModelCreationService();

        // TODO call PerformanceModelCreationService
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
}
