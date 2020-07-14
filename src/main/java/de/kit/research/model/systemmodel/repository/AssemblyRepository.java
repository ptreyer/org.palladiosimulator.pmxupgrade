package de.kit.research.model.systemmodel.repository;

import de.kit.research.model.systemmodel.AssemblyComponent;
import de.kit.research.model.systemmodel.ComponentType;
import de.kit.research.model.systemmodel.RootAssemblyComponent;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

public class AssemblyRepository extends AbstractSystemSubRepository {

	/** This constant represents the root assembly component. */
	public static final AssemblyComponent ROOT_ASSEMBLY_COMPONENT = new RootAssemblyComponent();

	private final Map<String, AssemblyComponent> assemblyComponentInstancesByName = new Hashtable<String, AssemblyComponent>(); // NOPMD (UseConcurrentHashMap)
	private final Map<Integer, AssemblyComponent> assemblyComponentInstancesById = new Hashtable<Integer, AssemblyComponent>(); // NOPMD (UseConcurrentHashMap)

	/**
	 * Creates a new instance of this class using the given parameters.
	 * 
	 * @param systemFactory
	 *            The system factory.
	 */
	public AssemblyRepository(final SystemModelRepository systemFactory) {
		super(systemFactory);
	}

	/**
	 * Returns the instance for the passed ID; null if no instance with this ID exists.
	 * 
	 * @param containerId
	 *            The ID to search for.
	 * 
	 * @return The component for the given ID if it exists; null otherwise.
	 */
	public final AssemblyComponent lookupAssemblyComponentById(final int containerId) {
		return this.assemblyComponentInstancesById.get(containerId);
	}

	/**
	 * Returns the instance for the passed factoryIdentifier; null if no instance
	 * with this factoryIdentifier.
	 * 
	 * @param namedIdentifier
	 *            The identifier to search for.
	 * 
	 * @return The component for the given identifier if it exists; null otherwise.
	 */
	public final AssemblyComponent lookupAssemblyComponentInstanceByNamedIdentifier(final String namedIdentifier) {
		return this.assemblyComponentInstancesByName.get(namedIdentifier);
	}

	/**
	 * Creates a new assembly component instance and registers it as well.
	 * 
	 * @param namedIdentifier
	 *            The identifier of the new component.
	 * @param componentType
	 *            The new component type.
	 * 
	 * @return The newly created assembly component.
	 */
	public final AssemblyComponent createAndRegisterAssemblyComponentInstance(final String namedIdentifier, final ComponentType componentType) {
		if (this.assemblyComponentInstancesByName.containsKey(namedIdentifier)) {
			throw new IllegalArgumentException("Element with name " + namedIdentifier + "exists already");
		}
		final int id = this.getAndIncrementNextId();
		final AssemblyComponent newInst = new AssemblyComponent(id, "@" + id, componentType);
		this.assemblyComponentInstancesById.put(id, newInst);
		this.assemblyComponentInstancesByName.put(namedIdentifier, newInst);
		return newInst;
	}

	/**
	 * Delivers all available assembly component instances.
	 * 
	 * @return A collection containing all components.
	 */
	public final Collection<AssemblyComponent> getAssemblyComponentInstances() {
		return this.assemblyComponentInstancesById.values();
	}
}
