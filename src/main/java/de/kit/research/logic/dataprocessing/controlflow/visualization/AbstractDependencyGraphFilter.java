package de.kit.research.logic.dataprocessing.controlflow.visualization;

import de.kit.research.model.systemmodel.ISystemModelElement;
import de.kit.research.model.systemmodel.trace.AbstractMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Refactored copy from LogAnalysis-legacy tool.
 * 
 * @param <T>
 * 
 * @author Andre van Hoorn, Lena Stoever, Matthias Rohr,
 * 
 * @since 1.1
 */
public abstract class AbstractDependencyGraphFilter<T extends ISystemModelElement> extends AbstractGraphProducingFilter<AbstractDependencyGraph<T>> {

	private final List<AbstractNodeDecorator> decorators = new ArrayList<>();

	/**
	 * Creates a new abstract dependency graph filter using the given data.
	 * 
	 * @param graph
	 *            The graph to produce / extend
	 */
	public AbstractDependencyGraphFilter(final AbstractDependencyGraph<T> graph) {
		super(graph);
	}

	/**
	 * Adds a node decorator to this graph.
	 * 
	 * @param decorator
	 *            The decorator to add
	 */
	public void addDecorator(final AbstractNodeDecorator decorator) {
		this.decorators.add(decorator);
	}

	/**
	 * This is a helper method to invoke all decorators and send them a message.
	 * 
	 * @param message
	 *            The message to send the decorators.
	 * @param sourceNode
	 *            The source node.
	 * @param targetNode
	 *            The target node.
	 */
	protected void invokeDecorators(final AbstractMessage message, final DependencyGraphNode<?> sourceNode, final DependencyGraphNode<?> targetNode) {
		for (final AbstractNodeDecorator currentDecorator : this.decorators) {
			currentDecorator.processMessage(message, sourceNode, targetNode, TimeUnit.NANOSECONDS);
		}
	}

	/**
	 * Determines whether the given edge is assumed or not.
	 * 
	 * @param source
	 *            The source of the edge.
	 * @param target
	 *            The target of the edge.
	 * 
	 * @return true iff the edge is assumed (which means in fact that either the source or the target or both are assumed).
	 */
	protected boolean isDependencyAssumed(final DependencyGraphNode<?> source, final DependencyGraphNode<?> target) {
		return source.isAssumed() || target.isAssumed();
	}
}
