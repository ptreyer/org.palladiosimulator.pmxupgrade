package de.kit.research.model.systemmodel.repository;

import de.kit.research.model.systemmodel.ComponentType;
import de.kit.research.model.systemmodel.Operation;
import de.kit.research.model.systemmodel.util.Signature;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

/**
 * This is a repository in which the available operations ({@link Operation}) can be stored.
 * 
 * @author Andre van Hoorn
 * 
 * @since 1.1
 */
public class OperationRepository extends AbstractSystemSubRepository {
	public static final Signature ROOT_SIGNATURE = new Signature(SystemModelRepository.ROOT_NODE_LABEL, new String[] {}, "<>", new String[] {});
	public static final Operation ROOT_OPERATION = new Operation(AbstractSystemSubRepository.ROOT_ELEMENT_ID, TypeRepository.ROOT_COMPONENT,
			ROOT_SIGNATURE);

	private final Map<String, Operation> operationsByName = new Hashtable<String, Operation>(); // NOPMD (UseConcurrentHashMap)
	private final Map<Integer, Operation> operationsById = new Hashtable<Integer, Operation>(); // NOPMD (UseConcurrentHashMap)

	/**
	 * Creates a new instance of this class using the given parameters.
	 * 
	 * @param systemFactory
	 *            The system factory.
	 */
	public OperationRepository(final SystemModelRepository systemFactory) {
		super(systemFactory);
	}

	/**
	 * @param namedIdentifier
	 *            The identifier to search for.
	 * 
	 * @return The instance for the passed namedIdentifier; null if no instance with this namedIdentifier.
	 */
	public final Operation lookupOperationByNamedIdentifier(final String namedIdentifier) {
		return this.operationsByName.get(namedIdentifier);
	}

	public final Operation createAndRegisterOperation(final String namedIdentifier, final ComponentType componentType, final Signature signature) {
		if (this.operationsByName.containsKey(namedIdentifier)) {
			throw new IllegalArgumentException("Element with name " + namedIdentifier + "exists already");
		}
		final int id = this.getAndIncrementNextId();
		final Operation newInst = new Operation(id, componentType, signature);
		this.operationsById.put(id, newInst);
		this.operationsByName.put(namedIdentifier, newInst);
		return newInst;
	}

	/**
	 * Delivers a collection containing all available operations.
	 * 
	 * @return The already stored operations.
	 */
	public final Collection<Operation> getOperations() {
		return this.operationsById.values();
	}
}
