package org.palladiosimulator.pmxupgrade.model.systemmodel.repository;

import org.palladiosimulator.pmxupgrade.model.systemmodel.component.ComponentType;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

/**
 * This is a repository in which the different component types ({@link ComponentType}) can be stored.
 * 
 * @author Andre van Hoorn
 * 
 * @since 1.1
 */
public class TypeRepository extends AbstractSystemSubRepository {

	/** This constant represents the root component. */
	public static final ComponentType ROOT_COMPONENT = new ComponentType(ROOT_ELEMENT_ID, SystemModelRepository.ROOT_NODE_LABEL);

	private final Map<String, ComponentType> componentTypesByName = new Hashtable<String, ComponentType>(); // NOPMD (UseConcurrentHashMap)
	private final Map<Integer, ComponentType> componentTypesById = new Hashtable<Integer, ComponentType>(); // NOPMD (UseConcurrentHashMap)

	/**
	 * Creates a new instance of this class using the given parameters.
	 * 
	 * @param systemFactory
	 *            The system factory.
	 */
	public TypeRepository(final SystemModelRepository systemFactory) {
		super(systemFactory);
	}

	/**
	 * Returns the instance for the passed namedIdentifier; null if no instance
	 * with this namedIdentifier.
	 * 
	 * @param namedIdentifier
	 *            The identifier to search for.
	 * 
	 * @return The corresponding component type if available; null otherwise.
	 */
	public final ComponentType lookupComponentTypeByNamedIdentifier(final String namedIdentifier) {
		synchronized (this) {
			return this.componentTypesByName.get(namedIdentifier);
		}
	}

	/**
	 * Creates and registers a component type that has not been registered yet.
	 * 
	 * @param namedIdentifier
	 *            The identifier of the new component type.
	 * @param fullqualifiedName
	 *            The fully qualfieid name of the new component type.
	 * 
	 * @return the created component type
	 */
	public final ComponentType createAndRegisterComponentType(final String namedIdentifier, final String fullqualifiedName) {
		final ComponentType newInst;
		synchronized (this) {
			if (this.componentTypesByName.containsKey(namedIdentifier)) {
				throw new IllegalArgumentException("Element with name " + namedIdentifier + "exists already");
			}
			final int id = this.getAndIncrementNextId();
			newInst = new ComponentType(id, fullqualifiedName);
			this.componentTypesById.put(id, newInst);
			this.componentTypesByName.put(namedIdentifier, newInst);
		}
		return newInst;
	}

	/**
	 * Returns a collection of all registered component types.
	 * 
	 * @return a collection of all registered component types.
	 */
	public final Collection<ComponentType> getComponentTypes() {
		synchronized (this) {
			return this.componentTypesById.values();
		}
	}
}
