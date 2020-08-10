package de.kit.research.logic.parametricdependencies;

import de.kit.research.model.systemmodel.repository.SystemModelRepository;

public interface ParametricDependenciesResolverInterface {

    SystemModelRepository identifyParametricDependencies(SystemModelRepository systemModelRepository);

    SystemModelRepository characterizeParametricDependencies(SystemModelRepository systemModelRepository);

}
