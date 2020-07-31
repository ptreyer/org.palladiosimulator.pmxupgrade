package de.kit.research.dataprocessing;

import de.kit.research.logic.PMXController;
import de.kit.research.logic.dataprocessing.controlflow.ControlFlowService;
import de.kit.research.logic.dataprocessing.controlflow.visualization.AbstractDependencyGraph;
import de.kit.research.logic.dataprocessing.controlflow.visualization.DependencyGraphNode;
import de.kit.research.logic.dataprocessing.controlflow.visualization.WeightedBidirectionalDependencyGraphEdge;
import de.kit.research.logic.dataprocessing.controlflow.visualization.graph.AbstractGraph;
import de.kit.research.logic.filter.opentracing.TraceReconstructionFilter;
import de.kit.research.model.common.Configuration;
import de.kit.research.model.exception.PMXException;
import de.kit.research.model.systemmodel.component.AllocationComponent;
import de.kit.research.model.systemmodel.component.AssemblyComponent;
import de.kit.research.model.systemmodel.repository.SystemModelRepository;
import de.kit.research.model.systemmodel.trace.TraceInformation;
import de.kit.research.model.systemmodel.util.AllocationComponentOperationPair;
import org.junit.jupiter.api.Test;

import java.io.*;

public class ControlFlowTest {

    @Test
    void filter() throws PMXException {
        Configuration configuration = new Configuration();
        configuration.setInputFileName("C:\\Users\\ptreyer\\workspace\\pmxupgrade\\src\\test\\resources\\json\\combination3.json");

        PMXController pmxController = new PMXController(configuration);
        pmxController.readTracingData();

        TraceReconstructionFilter filter = new TraceReconstructionFilter();

        filter.filter(configuration, pmxController.getTraceRecord());

        ControlFlowService service = new ControlFlowService();

        AbstractDependencyGraph<AllocationComponentOperationPair> graph = service.resolveControlFlow(filter.executionTraces, filter.systemModelRepository);

        doWork(graph, filter.systemModelRepository);

        System.out.println("finish");

    }

    private void doWork(
            final AbstractGraph<DependencyGraphNode<AllocationComponentOperationPair>, WeightedBidirectionalDependencyGraphEdge<AllocationComponentOperationPair>, TraceInformation> graph,
            SystemModelRepository systemModelRepository) {
        Writer fw = null;

        String outputFn = "output.txt";


        try {
//			log.info("Logging graph to " + outputFn);
            fw = new FileWriter(outputFn);
            fw.append("AllocationComponentInstances ");
            for (AllocationComponent ac : systemModelRepository
                    .getAllocationFactory().getAllocationComponentInstances()) {
                fw.append("\n ").append(ac.getIdentifier()).append(" ").append("(assembly: ").append(ac.getAssemblyComponent().getIdentifier()).append(") ").append("(execContainer: ").append(ac.getExecutionContainer().getIdentifier()).append(")");

            }
            fw.append("\n").append("AssemblyComponentInstances ");
            for (AssemblyComponent ac : systemModelRepository
                    .getAssemblyFactory().getAssemblyComponentInstances()) {
                fw.append("\n ").append(ac.getIdentifier()); // + " "					+ ac.getName());

            }
            fw.append("\n");

            for (DependencyGraphNode<AllocationComponentOperationPair> node : graph
                    .getVertices()) {

                //AllocationComponent allocationComponent = node.getEntity();
                fw.append("Node ").append(String.valueOf(node.getId())).append(" :: ").append(String.valueOf(node.getEntity().getOperation())).append("\t").append("\n");
                for (WeightedBidirectionalDependencyGraphEdge<AllocationComponentOperationPair> outgoingEdge : node
                        .getOutgoingEdges()) {

                    String start = node.getEntity().getAllocationComponent().getAssemblyComponent().getType().getTypeName();
                    String end = outgoingEdge.getTarget().getEntity().getAllocationComponent().getAssemblyComponent().getType().getTypeName();

                    fw.append("\t").append(String.valueOf(outgoingEdge.getTarget().getId())).append(": ").append("(weight=").append(String.valueOf(outgoingEdge.getTargetWeight())).append(")");
                }
                fw.append("\n");
            }
        } catch (IOException e) {
            System.out.println("Could not write file " + outputFn);
        } finally {
            if (fw != null)
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
}
