package de.kit.research.logic.dataprocessing.controlflow.visualization;

import de.kit.research.logic.dataprocessing.controlflow.visualization.AbstractDependencyGraph;
import de.kit.research.logic.dataprocessing.controlflow.visualization.AbstractDependencyGraphFilter;
import de.kit.research.logic.dataprocessing.controlflow.visualization.DependencyGraphNode;
import de.kit.research.model.systemmodel.component.AllocationComponent;
import de.kit.research.model.systemmodel.repository.AllocationComponentOperationPairFactory;
import de.kit.research.model.systemmodel.repository.OperationRepository;
import de.kit.research.model.systemmodel.repository.SystemModelRepository;
import de.kit.research.model.systemmodel.trace.AbstractMessage;
import de.kit.research.model.systemmodel.trace.MessageTrace;
import de.kit.research.model.systemmodel.trace.Operation;
import de.kit.research.model.systemmodel.trace.SynchronousReplyMessage;
import de.kit.research.model.systemmodel.util.AllocationComponentOperationPair;

public class FlowGraphCreationAdapter extends AbstractDependencyGraphFilter<AllocationComponentOperationPair> {

    /**
     * Creates a new abstract dependency graph filter using the given data.
     *
     * @param graph The graph to produce / extend
     */
    public FlowGraphCreationAdapter(AbstractDependencyGraph<AllocationComponentOperationPair> graph) {
        super(graph);
    }

    public void inputMessageTraces(final MessageTrace t, SystemModelRepository systemModelRepository) {
        for (final AbstractMessage m : t.getSequenceAsVector()) {
            if (m instanceof SynchronousReplyMessage) {
                continue;
            }
            final AllocationComponent senderComponent = m.getSendingExecution().getAllocationComponent();
            final AllocationComponent receiverComponent = m.getReceivingExecution().getAllocationComponent();
            final int rootOperationId = OperationRepository.ROOT_OPERATION.getId();
            final Operation senderOperation = m.getSendingExecution().getOperation();
            final Operation receiverOperation = m.getReceivingExecution().getOperation();

            // The following two get-calls to the factory return s.th. in either case
            final AllocationComponentOperationPairFactory pairFactory = systemModelRepository.getAllocationPairFactory();

            AllocationComponentOperationPair senderPair;
            if (senderOperation.getId() == rootOperationId) {
                senderPair = this.getGraph().getRootNode().getEntity();
            } else {
                senderPair = pairFactory.getPairInstanceByPair(senderComponent, senderOperation);
            }

            AllocationComponentOperationPair receiverPair;
            if (receiverOperation.getId() == rootOperationId) {
                receiverPair = this.getGraph().getRootNode().getEntity();
            } else {
                receiverPair = pairFactory.getPairInstanceByPair(receiverComponent, receiverOperation);
            }

            DependencyGraphNode<AllocationComponentOperationPair> senderNode = this.getGraph().getNode(senderPair
                    .getId());
            DependencyGraphNode<AllocationComponentOperationPair> receiverNode = this.getGraph().getNode(receiverPair.getId());
            if (senderNode == null) {
                senderNode = new DependencyGraphNode<>(senderPair.getId(), senderPair, t.getTraceInformation(),
                        this.getOriginRetentionPolicy());

                if (m.getSendingExecution().isAssumed()) {
                    senderNode.setAssumed();
                }

                this.getGraph().addNode(senderNode.getId(), senderNode);
            } else {
                this.handleOrigin(senderNode, t.getTraceInformation());
            }

            if (receiverNode == null) {
                receiverNode = new DependencyGraphNode<>(receiverPair.getId(), receiverPair, t.getTraceInformation(),
                        this.getOriginRetentionPolicy());

                if (m.getReceivingExecution().isAssumed()) {
                    receiverNode.setAssumed();
                }

                this.getGraph().addNode(receiverNode.getId(), receiverNode);
            } else {
                this.handleOrigin(receiverNode, t.getTraceInformation());
            }

            final boolean assumed = this.isDependencyAssumed(senderNode, receiverNode);

            senderNode.addOutgoingDependency(receiverNode, assumed, t.getTraceInformation(), this.getOriginRetentionPolicy());
            receiverNode.addIncomingDependency(senderNode, assumed, t.getTraceInformation(), this.getOriginRetentionPolicy());

            this.invokeDecorators(m, senderNode, receiverNode);
        }
    }


}