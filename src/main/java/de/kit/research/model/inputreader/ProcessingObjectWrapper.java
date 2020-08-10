package de.kit.research.model.inputreader;

import de.kit.research.model.systemmodel.repository.SystemModelRepository;
import de.kit.research.model.systemmodel.trace.Execution;
import de.kit.research.model.systemmodel.trace.ExecutionTrace;

import java.util.List;

public class ProcessingObjectWrapper {

    private SystemModelRepository systemModelRepository;
    private List<ExecutionTrace> executionTraces;
    private List<Execution> invalidExecutions;

    public ProcessingObjectWrapper(SystemModelRepository systemModelRepository, List<ExecutionTrace> executionTraces, List<Execution> invalidExecutions) {
        this.systemModelRepository = systemModelRepository;
        this.executionTraces = executionTraces;
        this.invalidExecutions = invalidExecutions;
    }

    public SystemModelRepository getSystemModelRepository() {
        return systemModelRepository;
    }

    public void setSystemModelRepository(SystemModelRepository systemModelRepository) {
        this.systemModelRepository = systemModelRepository;
    }

    public List<ExecutionTrace> getExecutionTraces() {
        return executionTraces;
    }

    public void setExecutionTraces(List<ExecutionTrace> executionTraces) {
        this.executionTraces = executionTraces;
    }

    public List<Execution> getInvalidExecutions() {
        return invalidExecutions;
    }

    public void setInvalidExecutions(List<Execution> invalidExecutions) {
        this.invalidExecutions = invalidExecutions;
    }
}
