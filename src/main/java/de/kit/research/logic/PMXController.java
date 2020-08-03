package de.kit.research.logic;

import de.kit.research.logic.filter.opentracing.TimestampFilter;
import de.kit.research.logic.filter.opentracing.TraceIdFilter;
import de.kit.research.logic.filter.opentracing.TraceReconstructionFilter;
import de.kit.research.logic.inputreader.InputReaderInterface;
import de.kit.research.logic.inputreader.impl.InputReaderOpenTracingImpl;
import de.kit.research.model.common.Configuration;
import de.kit.research.model.constants.PMXConstants;
import de.kit.research.model.exception.PMXException;
import de.kit.research.model.inputreader.InputObjectWrapper;
import de.kit.research.model.inputreader.opentracing.jaeger.TraceRecord;

import java.io.IOException;
import java.util.Optional;

public class PMXController {

    private InputReaderInterface inputReaderInterface;

    private Configuration configuration;
    private InputObjectWrapper inputObjectWrapper;
    private TraceRecord traceRecord;

    public PMXController(Configuration configuration) {
        // TODO validate config
        this.configuration = configuration;
        inputReaderInterface = new InputReaderOpenTracingImpl();
    }

    public void readTracingData() throws PMXException {
        try {
            inputObjectWrapper = inputReaderInterface.readTracingData(configuration);
            traceRecord = (TraceRecord) inputObjectWrapper;
        } catch (IOException e) {
            throw new PMXException(PMXConstants.FEHLER_DATENEINLESE);
        }
    }

    public void initAndExecuteFilter(){
        TimestampFilter timestampFilter = new TimestampFilter();
        traceRecord = timestampFilter.filter(configuration, traceRecord);

        TraceIdFilter traceIdFilter = new TraceIdFilter();
        traceRecord = traceIdFilter.filter(configuration, traceRecord);

        TraceReconstructionFilter traceReconstructionFilter = new TraceReconstructionFilter();
        traceRecord = traceReconstructionFilter.filter(configuration, traceRecord);
    }

    public void processTracingData(){
        // TODO call DataProcessingService
    }

    public void createPerformanceModel(){
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
