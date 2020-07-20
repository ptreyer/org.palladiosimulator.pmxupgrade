package de.kit.research.model.systemmodel.repository;

public class SystemModelRepository {

	public static final String ROOT_NODE_LABEL = "'Entry'";

	private final TypeRepository typeRepositoryFactory;
	private final AssemblyRepository assemblyFactory;
	private final ExecutionEnvironmentRepository executionEnvironmentFactory;
	private final AllocationRepository allocationFactory;
	private final OperationRepository operationFactory;
	private final AllocationComponentOperationPairFactory allocationPairFactory;
	private final AssemblyComponentOperationPairFactory assemblyPairFactory;

	/**
	 * Creates a new instance of this class using the given parameters.
	 * 
	 */
	public SystemModelRepository() {
		this.typeRepositoryFactory = new TypeRepository(this);
		this.assemblyFactory = new AssemblyRepository(this);
		this.executionEnvironmentFactory = new ExecutionEnvironmentRepository(this);
		this.allocationFactory = new AllocationRepository(this);
		this.operationFactory = new OperationRepository(this);
		this.allocationPairFactory = new AllocationComponentOperationPairFactory(this);
		this.assemblyPairFactory = new AssemblyComponentOperationPairFactory(this);
	}

	public final AllocationRepository getAllocationFactory() {
		return this.allocationFactory;
	}

	public final AssemblyRepository getAssemblyFactory() {
		return this.assemblyFactory;
	}

	public final ExecutionEnvironmentRepository getExecutionEnvironmentFactory() {
		return this.executionEnvironmentFactory;
	}

	/**
	 * Delivering the factory managing the available operations.
	 * 
	 * @return The operation factory.
	 */
	public final OperationRepository getOperationFactory() {
		return this.operationFactory;
	}

	/**
	 * Delivering the factory managing the available component types.
	 * 
	 * @return The types factory.
	 */
	public final TypeRepository getTypeRepositoryFactory() {
		return this.typeRepositoryFactory;
	}

	public AllocationComponentOperationPairFactory getAllocationPairFactory() {
		return this.allocationPairFactory;
	}

	public AssemblyComponentOperationPairFactory getAssemblyPairFactory() {
		return this.assemblyPairFactory;
	}

}
