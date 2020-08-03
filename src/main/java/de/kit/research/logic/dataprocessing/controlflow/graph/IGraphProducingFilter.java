package de.kit.research.logic.dataprocessing.controlflow.graph;


import de.kit.research.model.graph.AbstractGraph;
import de.kit.research.model.graph.IOriginRetentionPolicy;
import de.kit.research.model.exception.AnalysisConfigurationException;

/**
 * Interface for graph-producing filters.
 * 
 * @author Holger Knoche
 * 
 * @param <G>
 *            The type of the produced graph
 * 
 * @since 1.6
 */
public interface IGraphProducingFilter<G extends AbstractGraph<?, ?, ?>> {

	void requestOriginRetentionPolicy(IOriginRetentionPolicy policy) throws AnalysisConfigurationException;

}
