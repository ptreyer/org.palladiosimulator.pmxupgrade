package org.palladiosimulator.pmxupgrade.model.systemmodel.repository;

import org.palladiosimulator.pmxupgrade.model.systemmodel.component.AllocationComponent;
import org.palladiosimulator.pmxupgrade.model.systemmodel.component.AssemblyComponent;
import org.palladiosimulator.pmxupgrade.model.systemmodel.component.ExecutionContainer;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

/**
 * Represents the Allocation repository of the system model.
 *
 * @author Patrick Treyer
 */
public class AllocationRepository extends AbstractSystemSubRepository {
    public static final AllocationComponent ROOT_ALLOCATION_COMPONENT =
            new AllocationComponent(ROOT_ELEMENT_ID, AssemblyRepository.ROOT_ASSEMBLY_COMPONENT,
                    ExecutionEnvironmentRepository.ROOT_EXECUTION_CONTAINER);

    private final Map<String, AllocationComponent> allocationComponentInstancesByName = new Hashtable<>();
    private final Map<Integer, AllocationComponent> allocationComponentInstancesById = new Hashtable<>();

    /**
     * Creates a new instance of this class using the given parameters.
     *
     * @param systemFactory The system factory.
     */
    public AllocationRepository(final SystemModelRepository systemFactory) {
        super(systemFactory);
    }

    /**
     * Returns the instance for the passed factoryIdentifier; null if no instance with this factoryIdentifier.
     *
     * @param namedIdentifier The identifier to search for.
     * @return The corresponding instance if it exists.
     */
    public final AllocationComponent lookupAllocationComponentInstanceByNamedIdentifier(final String namedIdentifier) {
        return this.allocationComponentInstancesByName.get(namedIdentifier);
    }

    public final AllocationComponent createAndRegisterAllocationComponentInstance(final String namedIdentifier, final AssemblyComponent assemblyComponentInstance,
                                                                                  final ExecutionContainer executionContainer) {
        if (this.allocationComponentInstancesByName.containsKey(namedIdentifier)) {
            throw new IllegalArgumentException("Element with name " + namedIdentifier + "exists already");
        }
        final int id = this.getAndIncrementNextId();
        final AllocationComponent newInst = new AllocationComponent(id, assemblyComponentInstance, executionContainer);
        this.allocationComponentInstancesById.put(id, newInst);
        this.allocationComponentInstancesByName.put(namedIdentifier, newInst);
        return newInst;
    }

    public final Collection<AllocationComponent> getAllocationComponentInstances() {
        return this.allocationComponentInstancesById.values();
    }
}
