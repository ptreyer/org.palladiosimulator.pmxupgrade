package org.palladiosimulator.pmxupgrade.logic.parametricdependencies;

import org.palladiosimulator.pmxupgrade.model.systemmodel.repository.SystemModelRepository;

public interface ParametricDependenciesResolverInterface {

    SystemModelRepository identifyParametricDependencies(SystemModelRepository systemModelRepository);

    SystemModelRepository characterizeParametricDependencies(SystemModelRepository systemModelRepository);

}
