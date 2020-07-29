package de.kit.research.logic.dataprocessing.controlflow.visualization.graph;

/**
 * This is an abstract base for vertex decorations.
 * 
 * @author Holger Knoche
 * 
 * @since 1.5
 */
public abstract class AbstractVertexDecoration {

	/**
	 * Creates formatted output for this decoration.
	 * 
	 * @return See above
	 */
	public abstract String createFormattedOutput();

}
