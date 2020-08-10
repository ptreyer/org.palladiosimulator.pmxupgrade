package de.kit.research.logic.dataprocessing;

import de.kit.research.logic.dataprocessing.controlflow.ControlFlowService;
import de.kit.research.logic.dataprocessing.controlflow.graph.AbstractDependencyGraph;
import de.kit.research.logic.dataprocessing.failureestimation.FailureEstimationService;
import de.kit.research.logic.dataprocessing.resourcedemands.ResourceDemandEstimationService;
import de.kit.research.logic.dataprocessing.workload.WorkloadService;
import de.kit.research.model.systemmodel.repository.SystemModelRepository;
import de.kit.research.model.systemmodel.trace.ExecutionTrace;
import de.kit.research.model.systemmodel.util.AllocationComponentOperationPair;

import java.util.HashMap;
import java.util.List;

public class DataProcessingService {

    private ControlFlowService controlFlowService = new ControlFlowService();
    private WorkloadService workloadService = new WorkloadService();
    private ResourceDemandEstimationService resourceDemandEstimationService = new ResourceDemandEstimationService();
    private FailureEstimationService failureEstimationService = new FailureEstimationService();


    public HashMap<String, List<Double>> analyzeWorkload(List<ExecutionTrace> executionTraces) {
        return workloadService.analyzeWorkload(executionTraces);
    }

    public AbstractDependencyGraph<AllocationComponentOperationPair> resolveControlFlow(List<ExecutionTrace> executionTraces, SystemModelRepository systemModelRepository) {
        return controlFlowService.resolveControlFlow(executionTraces, systemModelRepository);
    }

    public void calculateResourceDemands(){
        // TODO
    }

    public void calculateFailureProbabilities(){
        // TODO
    }

    public void resolveParametricDependencies(){
        // TODO
    }

}
