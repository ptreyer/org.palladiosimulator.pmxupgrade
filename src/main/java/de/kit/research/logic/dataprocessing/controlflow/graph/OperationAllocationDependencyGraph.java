
package de.kit.research.logic.dataprocessing.controlflow.graph;

import de.kit.research.model.systemmodel.util.AllocationComponentOperationPair;

/**
 * This class represents operation dependency graphs on the allocation level.
 * 
 * @author Holger Knoche
 * 
 * @since 1.6
 */
public class OperationAllocationDependencyGraph extends AbstractDependencyGraph<AllocationComponentOperationPair> {

	/**
	 * Creates a new graph with the given root entity.
	 * 
	 * @param rootEntity
	 *            The root entity to use for this graph
	 */
	public OperationAllocationDependencyGraph(final AllocationComponentOperationPair rootEntity) {
		super(rootEntity);
	}

}
