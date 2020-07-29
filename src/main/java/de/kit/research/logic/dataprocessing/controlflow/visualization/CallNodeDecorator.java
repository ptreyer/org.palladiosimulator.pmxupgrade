package de.kit.research.logic.dataprocessing.controlflow.visualization;

import de.kit.research.model.systemmodel.trace.AbstractMessage;

import java.util.concurrent.TimeUnit;


public class CallNodeDecorator extends AbstractNodeDecorator {

    @Override
    public void processMessage(AbstractMessage message,
                               DependencyGraphNode<?> sourceNode, DependencyGraphNode<?> targetNode,
                               TimeUnit timeUnit) {
        if (sourceNode.equals(targetNode)) {
            return;
        }

        CallDecoration callDecoration = targetNode.getDecoration(CallDecoration.class);

        if (callDecoration == null) {
            callDecoration = new CallDecoration();
            targetNode.addDecoration(callDecoration);
        }

        callDecoration.registerExecution();    //message.getReceivingExecution()


    }

}
