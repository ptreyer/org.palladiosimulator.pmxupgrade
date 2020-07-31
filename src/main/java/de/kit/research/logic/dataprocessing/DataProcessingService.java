package de.kit.research.logic.dataprocessing;

import de.kit.research.logic.dataprocessing.controlflow.ControlFlowService;
import de.kit.research.logic.dataprocessing.workload.WorkloadService;
import de.kit.research.model.systemmodel.trace.ExecutionTrace;

import java.util.HashMap;
import java.util.List;

public class DataProcessingService {

    private ControlFlowService controlFlowService = new ControlFlowService();
    private WorkloadService workloadService = new WorkloadService();


    public void analyzeWorkload(List<ExecutionTrace> executionTraceList) {
        HashMap<String, List<Double>> workloadTimeSeriesMap = workloadService.analyzeWorkload(executionTraceList);
    }



}
