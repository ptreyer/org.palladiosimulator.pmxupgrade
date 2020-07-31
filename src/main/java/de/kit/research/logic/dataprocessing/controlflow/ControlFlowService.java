package de.kit.research.logic.dataprocessing.controlflow;

import de.kit.research.logic.dataprocessing.controlflow.visualization.AbstractDependencyGraph;
import de.kit.research.logic.dataprocessing.controlflow.visualization.FlowGraphCreationAdapter;
import de.kit.research.logic.dataprocessing.controlflow.visualization.OperationAllocationDependencyGraph;
import de.kit.research.model.systemmodel.repository.AbstractSystemSubRepository;
import de.kit.research.model.systemmodel.repository.AllocationRepository;
import de.kit.research.model.systemmodel.repository.OperationRepository;
import de.kit.research.model.systemmodel.repository.SystemModelRepository;
import de.kit.research.model.systemmodel.trace.ExecutionTrace;
import de.kit.research.model.systemmodel.util.AllocationComponentOperationPair;

import java.util.List;

public class ControlFlowService {

    public AbstractDependencyGraph<AllocationComponentOperationPair> resolveControlFlow(final List<ExecutionTrace> executionTraces, final SystemModelRepository systemModelRepository) {

        FlowGraphCreationAdapter adapter = new FlowGraphCreationAdapter(new OperationAllocationDependencyGraph(new AllocationComponentOperationPair(
                AbstractSystemSubRepository.ROOT_ELEMENT_ID, OperationRepository.ROOT_OPERATION, AllocationRepository.ROOT_ALLOCATION_COMPONENT)));

        for (ExecutionTrace t : executionTraces) {
            adapter.inputMessageTraces(t.getMessageTrace(), systemModelRepository);
        }

        return adapter.getGraph();
    }

}
