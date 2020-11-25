package org.palladiosimulator.pmxupgrade.logic.modelcreation.builder;

import org.palladiosimulator.pmxupgrade.model.systemmodel.component.ComponentType;
import org.palladiosimulator.pmxupgrade.model.systemmodel.trace.ExternalCall;
import org.palladiosimulator.pmxupgrade.model.systemmodel.util.Signature;
import org.eclipse.emf.ecore.EObject;

import java.util.HashMap;
import java.util.List;

/**
 * Interface for the Model builder, which can be implemented for specific performance model representations.
 *
 * @author PMX, Universitaet Wuerzburg.
 */
public interface IModelBuilder {

    EObject createAssembly(String assemblyName);

    EObject createComponent(String componentName);

    void addComponentToAssembly(String assemblyName, String componentName);

    EObject createInterface(String InterfaceName);

    EObject createMethod(ComponentType type, Signature signature);

    EObject createHost(String hostName, int numCores);

    EObject createAllocation(String assemblyName, String hostName);

    EObject createProvidedRole(String componentName, String interfaceName);

    EObject createRequiredRole(String componentName, String interfaceName);

    EObject createSEFF(String componentName, String methodName, List<ExternalCall> externalCalls, String processingResource, double meanResourceDemand);

    void addConnectionToAssemblies(String requiringAssemblyName, String providingAssemblyName);

    EObject connectAssemblies(String providingAssemblyName, String requiringAssemblyName);

    void addResourceDemand(String service);

    EObject getRole(String role);

    EObject getAssembly(String assemblyName);

    EObject getMethod(String methodName);

    EObject getInterface(String interfaceName);

    EObject getSEFF(String componentName, String methodName);

    void addProvidedRole(String componentName, String interfaceName);

    void addRequiredRole(String componentName, String interfaceName);

    void addComponent(String componentName);

    void addHost(String name, int numCores);

    EObject addAllocationContext(String componentName, String hostName);

    EObject addAssembly(String name);

    void addInterface(String typeName);

    void addSEFF(String componentName, String methodName, List<ExternalCall> externalCalls, String processingResource, double meanResourceDemand);

    void addSEFF(String componentName, String methodName, EObject seff);

    boolean isSEFF(String componentName, String methodName);

    void saveToFile();

    void saveToFile(String path);

    void addMethod(ComponentType type, Signature signature);

    void createNetwork(double averageNetworkDelay, double throughtput);

    String getOutputDirectory();

    void addUsageScenario(HashMap<String, List<Double>> workload);

}
