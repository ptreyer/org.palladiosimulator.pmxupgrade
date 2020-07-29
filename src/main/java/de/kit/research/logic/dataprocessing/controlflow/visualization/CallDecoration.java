package de.kit.research.logic.dataprocessing.controlflow.visualization;

import de.kit.research.logic.dataprocessing.controlflow.visualization.graph.AbstractVertexDecoration;

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
