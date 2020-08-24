package de.kit.research.logic.modelcreation.creator;

import de.kit.research.logic.modelcreation.builder.IModelBuilder;
import de.kit.research.model.systemmodel.component.AllocationComponent;
import de.kit.research.model.systemmodel.component.AssemblyComponent;
import de.kit.research.model.systemmodel.component.ComponentType;
import de.kit.research.model.systemmodel.component.ExecutionContainer;
import de.kit.research.model.systemmodel.repository.SystemModelRepository;
import de.kit.research.model.systemmodel.trace.Operation;
import de.kit.research.model.systemmodel.util.Signature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.shared.utils.StringUtils;

import java.util.Collection;
import java.util.HashMap;

public class PerformanceModelCreator {

	private static final Logger log = LogManager.getLogger(PerformanceModelCreator.class);

	public static void addComponentsToAssemblies(SystemModelRepository systemModel, IModelBuilder builder) {
		final Collection<AssemblyComponent> assemblyComponents = systemModel.getAssemblyFactory()
				.getAssemblyComponentInstances();
		for (AssemblyComponent assembly : assemblyComponents) {
			String assemblyName = assembly.getType().getTypeName();
			if (!StringUtils.equalsIgnoreCase(assemblyName, "'Entry'")) {
				builder.addComponentToAssembly(assemblyName, assemblyName);
			}
		}
	}

	public static void createAllocations(SystemModelRepository systemModel, IModelBuilder builder) {
		Collection<AllocationComponent> allocationComponents = systemModel.getAllocationFactory()
				.getAllocationComponentInstances();
		for (AllocationComponent allocationComponent : allocationComponents) {
			builder.addAllocationContext(allocationComponent.getAssemblyComponent().getType().getTypeName(),
					allocationComponent.getExecutionContainer().getName());
		}
	}

	public static void createAssemblies(SystemModelRepository systemModel, IModelBuilder builder) {
		final Collection<AssemblyComponent> assemblyComponents = systemModel.getAssemblyFactory()
				.getAssemblyComponentInstances();
		for (AssemblyComponent assembly : assemblyComponents) {
			String assemblyName = assembly.getType().getTypeName();
			builder.addAssembly(assemblyName);
		}
	}

	public static void createComponentsAndInterfaces(SystemModelRepository systemModel, IModelBuilder builder) {
		final Collection<ComponentType> componentTypes = systemModel.getTypeRepositoryFactory().getComponentTypes();
		for (final ComponentType type : componentTypes) {
			builder.addComponent(type.getTypeName());
			builder.addInterface("I" + type.getTypeName());
			for (Operation operation : type.getOperations()) {
				Signature signature = operation.getSignature();
				builder.addMethod(type, signature);
			}
		}
	}

	public static void createExecutionContainers(SystemModelRepository systemModel, IModelBuilder builder,
			HashMap<String, Integer> numCores) {
		final Collection<ExecutionContainer> executionContainers = systemModel.getExecutionEnvironmentFactory()
				.getExecutionContainers();
		for (ExecutionContainer container : executionContainers) {
			int numberOfCores;
			try {
				numberOfCores = numCores.get(container.getName());
			} catch (Exception e) {
				log.info("could not find number of cores for " + container.getName());
				log.info("assumed numberOfCores = 2");
				numberOfCores = 2;
			}
			builder.addHost(container.getName(), numberOfCores);
		}
	}

}
