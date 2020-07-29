package de.kit.research.logic.dataprocessing.controlflow.visualization;


import de.kit.research.logic.dataprocessing.controlflow.visualization.graph.AbstractGraph;
import de.kit.research.logic.dataprocessing.controlflow.visualization.graph.IOriginRetentionPolicy;
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
