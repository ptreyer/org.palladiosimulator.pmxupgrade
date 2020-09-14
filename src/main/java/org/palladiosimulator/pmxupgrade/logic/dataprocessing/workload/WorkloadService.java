package org.palladiosimulator.pmxupgrade.logic.dataprocessing.workload;

import org.palladiosimulator.pmxupgrade.logic.modelcreation.builder.ModelBuilder;
import org.palladiosimulator.pmxupgrade.model.systemmodel.trace.AbstractMessage;
import org.palladiosimulator.pmxupgrade.model.systemmodel.trace.Execution;
import org.palladiosimulator.pmxupgrade.model.systemmodel.trace.ExecutionTrace;
import org.palladiosimulator.pmxupgrade.model.systemmodel.trace.MessageTrace;

import java.util.*;

public class WorkloadService {

    private static HashMap<String, List<Double>> workloadTimeSeriesMap;

    public HashMap<String, List<Double>> analyzeWorkload(final List<ExecutionTrace> executionTraces) {
        workloadTimeSeriesMap = new HashMap<>();
        executionTraces.stream().map(ExecutionTrace::getMessageTrace).forEach(this::analyzeMessageTrace);
        return workloadTimeSeriesMap;
    }

    private void analyzeMessageTrace(final MessageTrace mt) {
        AbstractMessage startMessage = getStartMessage(mt);// mt.getSequenceAsVector().get(0);
        Execution x = startMessage.getReceivingExecution();
        String host = x.getAllocationComponent().getExecutionContainer().getName();
        String function = x.getOperation().getSignature().getName();
        String component = x.getAllocationComponent().getAssemblyComponent().getType().getTypeName();
        addTimeStamp(host, component, function, x.getTin());
    }

    private AbstractMessage getStartMessage(final MessageTrace mt) {
        for (AbstractMessage message : mt.getSequenceAsVector()) {
            if (message.getTimestamp() == mt.getStartTimestamp()) {
                return message;
            }
        }
        return mt.getSequenceAsVector().get(0);
    }

    private void sortMessageTrace(final MessageTrace mt) {
        List<AbstractMessage> messages = new ArrayList<>(mt.getSequenceAsVector());
        Comparator<AbstractMessage> timeComparator = Comparator.comparingLong(AbstractMessage::getTimestamp);
        messages.sort(timeComparator);
    }

    private synchronized void addTimeStamp(String host, String component, String function, double timestamp) {
        List<Double> timeSeries;
        String key = function + ModelBuilder.seperatorChar + component + ModelBuilder.seperatorChar + host;
        if (workloadTimeSeriesMap.containsKey(key)) {
            timeSeries = workloadTimeSeriesMap.get(key);
            timeSeries.add(timestamp);
        } else {
            timeSeries = new ArrayList<>();
            timeSeries.add(timestamp);
            workloadTimeSeriesMap.put(key, timeSeries);
        }
    }

}
