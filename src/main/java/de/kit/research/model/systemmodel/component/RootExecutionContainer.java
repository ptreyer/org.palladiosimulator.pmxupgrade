package de.kit.research.model.systemmodel.component;

import de.kit.research.model.systemmodel.repository.AbstractSystemSubRepository;
import de.kit.research.model.systemmodel.repository.SystemModelRepository;

/**
 * Specific subtype for the root execution container.
 * 
 * @author Holger Knoche
 * 
 * @since 1.6
 */
public class RootExecutionContainer extends ExecutionContainer {

	/**
	 * Creates a new root execution container.
	 */
	public RootExecutionContainer() {
		super(Integer.toString(AbstractSystemSubRepository.ROOT_ELEMENT_ID), null, SystemModelRepository.ROOT_NODE_LABEL);
	}

	@Override
	public boolean isRootContainer() {
		return true;
	}
}
