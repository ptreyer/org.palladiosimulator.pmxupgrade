package org.palladiosimulator.pmxupgrade.model.systemmodel.repository;

import org.palladiosimulator.pmxupgrade.model.systemmodel.component.AssemblyComponent;
import org.palladiosimulator.pmxupgrade.model.systemmodel.trace.Operation;
import org.palladiosimulator.pmxupgrade.model.systemmodel.util.AssemblyComponentOperationPair;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

/**
 * Assembly Component OperationPairFactory of the system model.
 *
 * @author Patrick Treyer
 */
public class AssemblyComponentOperationPairFactory extends AbstractSystemSubRepository {
    public static final AssemblyComponentOperationPair ROOT_PAIR =
            new AssemblyComponentOperationPair(AbstractSystemSubRepository.ROOT_ELEMENT_ID, OperationRepository.ROOT_OPERATION,
                    AssemblyRepository.ROOT_ASSEMBLY_COMPONENT);

    private final Map<String, AssemblyComponentOperationPair> pairsByName = new Hashtable<>();
    private final Map<Integer, AssemblyComponentOperationPair> pairsById = new Hashtable<>();

    /**
     * Creates a new instance of this class using the given parameters.
     *
     * @param systemFactory The system factory.
     */
    public AssemblyComponentOperationPairFactory(final SystemModelRepository systemFactory) {
        super(systemFactory);
    }

    /**
     * Returns a corresponding pair instance (existing or newly created).
     *
     * @param assemblyComponent The assemble component for the pair.
     * @param operation         The operation for the pair.
     * @return The corresponding pair instance if it exists, otherwise a new one.
     */
    public final AssemblyComponentOperationPair getPairInstanceByPair(final AssemblyComponent assemblyComponent, final Operation operation) {
        final AssemblyComponentOperationPair inst = this.getPairByNamedIdentifier(assemblyComponent.getId() + "-" + operation.getId());
        if (inst == null) {
            return this.createAndRegisterPair(operation, assemblyComponent);
        }
        return inst;
    }

    private AssemblyComponentOperationPair createAndRegisterPair(final Operation operation, final AssemblyComponent assemblyComponent) {
        return this.createAndRegisterPair(assemblyComponent.getId() + "-" + operation.getId(), operation, assemblyComponent);
    }

    /**
     * Returns the instance for the passed factory name; null if no instance
     * with this factory name.
     *
     * @param namedIdentifier The identifier to search for.
     */
    private AssemblyComponentOperationPair getPairByNamedIdentifier(final String namedIdentifier) {
        return this.pairsByName.get(namedIdentifier);
    }

    /**
     * @param id The ID of the instance in question.
     * @return The instance for the passed ID; null if no instance with this ID is available.
     */
    public final AssemblyComponentOperationPair getPairById(final int id) {
        return this.pairsById.get(id);
    }

    private AssemblyComponentOperationPair createAndRegisterPair(final String namedIdentifier, final Operation operation,
                                                                 final AssemblyComponent assemblyComponent) {
        if (this.pairsByName.containsKey(namedIdentifier)) {
            throw new IllegalArgumentException("Element with name " + namedIdentifier + "exists already");
        }
        final int id = this.getAndIncrementNextId();
        final AssemblyComponentOperationPair newInst = new AssemblyComponentOperationPair(id, operation, assemblyComponent);
        this.pairsById.put(id, newInst);
        this.pairsByName.put(namedIdentifier, newInst);
        return newInst;
    }

    public final Collection<AssemblyComponentOperationPair> getPairs() {
        return this.pairsById.values();
    }

    public AssemblyComponentOperationPair getRootPair() {
        return ROOT_PAIR;
    }
}
