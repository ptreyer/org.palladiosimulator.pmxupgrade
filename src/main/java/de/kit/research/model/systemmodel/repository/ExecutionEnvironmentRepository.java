package de.kit.research.model.systemmodel.repository;

import de.kit.research.model.systemmodel.component.ExecutionContainer;
import de.kit.research.model.systemmodel.component.RootExecutionContainer;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;


/**
 * 
 * @author Andre van Hoorn
 * 
 * @since 1.1
 */
public class ExecutionEnvironmentRepository extends AbstractSystemSubRepository {

	/** The root execution container. */
	public static final ExecutionContainer ROOT_EXECUTION_CONTAINER = new RootExecutionContainer();

	private final Map<String, ExecutionContainer> executionContainersByName = new Hashtable<String, ExecutionContainer>(); // NOPMD (UseConcurrentHashMap)
	private final Map<Integer, ExecutionContainer> executionContainersById = new Hashtable<Integer, ExecutionContainer>(); // NOPMD (UseConcurrentHashMap)

	/**
	 * Creates a new instance of this class using the given parameters.
	 * 
	 * @param systemFactory
	 *            The system factory.
	 */
	public ExecutionEnvironmentRepository(final SystemModelRepository systemFactory) {
		super(systemFactory);
	}

	/**
	 * @param namedIdentifier
	 *            The identifier to search for.
	 * 
	 * @return The instance for the passed namedIdentifier; null if no instance with this namedIdentifier.
	 */
	public final ExecutionContainer lookupExecutionContainerByNamedIdentifier(final String namedIdentifier) {
		return this.executionContainersByName.get(namedIdentifier);
	}

	/**
	 * Returns the instance for the passed container ID; null if no instance
	 * with this ID.
	 * 
	 * @param containerId
	 *            The ID to search for.
	 * 
	 * @return The container for the given ID if it exists; null otherwise.
	 */
	public final ExecutionContainer lookupExecutionContainerByContainerId(final int containerId) {
		return this.executionContainersById.get(containerId);
	}

	/**
	 * This method creates a new execution container and registers it as well.
	 * 
	 * @param namedIdentifier
	 *            The identifier of the new container.
	 * @param name
	 *            The name of the new container.
	 * 
	 * @return The newly created execution container.
	 */
	public final ExecutionContainer createAndRegisterExecutionContainer(final String namedIdentifier, final String name) {
		if (this.executionContainersByName.containsKey(namedIdentifier)) {
			throw new IllegalArgumentException("Element with name " + namedIdentifier + "exists already");
		}
		final int id = this.getAndIncrementNextId();
		final ExecutionContainer newInst = new ExecutionContainer(id, null, name);
		this.executionContainersById.put(id, newInst);
		this.executionContainersByName.put(namedIdentifier, newInst);
		return newInst;
	}

	/**
	 * Delivers all available execution containers.
	 * 
	 * @return A collection containing the available containers.
	 */
	public final Collection<ExecutionContainer> getExecutionContainers() {
		return this.executionContainersById.values();
	}
}
