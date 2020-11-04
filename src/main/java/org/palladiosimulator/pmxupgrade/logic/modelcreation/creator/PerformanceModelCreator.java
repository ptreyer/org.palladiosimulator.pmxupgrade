package org.palladiosimulator.pmxupgrade.logic.modelcreation.creator;

import org.palladiosimulator.pmxupgrade.logic.modelcreation.builder.IModelBuilder;
import org.palladiosimulator.pmxupgrade.model.systemmodel.component.AllocationComponent;
import org.palladiosimulator.pmxupgrade.model.systemmodel.component.AssemblyComponent;
import org.palladiosimulator.pmxupgrade.model.systemmodel.component.ComponentType;
import org.palladiosimulator.pmxupgrade.model.systemmodel.component.ExecutionContainer;
import org.palladiosimulator.pmxupgrade.model.systemmodel.repository.SystemModelRepository;
import org.palladiosimulator.pmxupgrade.model.systemmodel.trace.Operation;
import org.palladiosimulator.pmxupgrade.model.systemmodel.util.Signature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.shared.utils.StringUtils;

import java.util.Collection;
import java.util.HashMap;

/**
 * Builds the performance Models by invocating the specific implementations of the builder pattern architecture.
 *
 * @author Patrick Treyer
 */
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
