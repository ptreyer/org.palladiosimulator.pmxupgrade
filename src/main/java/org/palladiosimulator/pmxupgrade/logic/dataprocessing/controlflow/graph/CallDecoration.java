package org.palladiosimulator.pmxupgrade.logic.dataprocessing.controlflow.graph;

import org.palladiosimulator.pmxupgrade.model.graph.AbstractVertexDecoration;

public class CallDecoration extends AbstractVertexDecoration {

    int executionCount;

    public CallDecoration() {
        executionCount = 0;
    }

    @Override
    public String createFormattedOutput() {
        return "" + executionCount;
    }

    public void registerExecution() {
        this.executionCount++;

    }

}
