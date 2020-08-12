package de.kit.research.logic.dataprocessing.resourcedemands.librede;

import tools.descartes.librede.algorithm.IKalmanFilterAlgorithm;
import tools.descartes.librede.approach.ResponseTimeApproximationApproach;
import tools.descartes.librede.approach.ServiceDemandLawApproach;
import tools.descartes.librede.approach.WangKalmanFilterApproach;
import tools.descartes.librede.configuration.ConfigurationFactory;
import tools.descartes.librede.configuration.EstimationAlgorithmConfiguration;
import tools.descartes.librede.configuration.EstimationApproachConfiguration;
import tools.descartes.librede.configuration.EstimationSpecification;
import tools.descartes.librede.configuration.LibredeConfiguration;
import tools.descartes.librede.configuration.Resource;
import tools.descartes.librede.configuration.TraceConfiguration;
import tools.descartes.librede.configuration.WorkloadDescription;
import tools.descartes.librede.datasource.memory.InMemoryDataSource;
import tools.descartes.librede.exceptions.NonOverlappingRangeException;
import tools.descartes.librede.repository.TimeSeries;
import tools.descartes.librede.units.Time;
import tools.descartes.librede.units.UnitsFactory;
import tools.descartes.librede.util.RepositoryUtil;
import tools.descartes.librede.util.RepositoryUtil.Range;

public class EstimationSpecificationFactory {

	public static EstimationSpecification createEstimationSpecification(
			InMemoryDataSource dataSource, WorkloadDescription workloadDescription, String host,
			LibredeConfiguration configuration, int numCores) {
		
		EstimationSpecification estimationSpecification = ConfigurationFactory.eINSTANCE
				.createEstimationSpecification();
		estimationSpecification.setRecursive(false);
		configuration.setEstimation(estimationSpecification);

		Range range;
		try {
			range = RepositoryUtil.deduceMaximumOverlappingInterval(dataSource,
					configuration);
		} catch (NonOverlappingRangeException e) {
			range = RepositoryUtil.deduceMaximumInterval(dataSource, configuration);
			for (TraceConfiguration trace : configuration.getInput().getObservations()) {
				TimeSeries timeSeries = dataSource.getData(trace.getLocation());
				timeSeries.setStartTime(range.getStart());
				timeSeries.setEndTime(range.getEnd());
			}
		}
		
		estimationSpecification
				.setStepSize(UnitsFactory.eINSTANCE.createQuantity(range.getValue() / 10, Time.SECONDS));
		estimationSpecification.setWindow(60);
		
		configuration.getEstimation().setStartTimestamp(UnitsFactory.eINSTANCE.createQuantity(range.getStart(), Time.SECONDS));
		configuration.getEstimation().setEndTimestamp(UnitsFactory.eINSTANCE.createQuantity(range.getEnd(), Time.SECONDS));

		addEstimationSpecification(workloadDescription, configuration, host, estimationSpecification, numCores);
		return estimationSpecification;
	}
	
	private static void addEstimationSpecification(
			WorkloadDescription workloadDescription,
			LibredeConfiguration libredeConfiguration, String host,
			EstimationSpecification estimationSpecification, int numCores) {
		if (workloadDescription.getResources().size() == 0) {
			//log.info("\tassumtion: one CPU with two cores");
			Resource cpu = ConfigurationFactory.eINSTANCE.createResource();
			cpu.setName("CPU@" + host);
			cpu.setNumberOfServers(numCores);
			workloadDescription.getResources().add(cpu);
			LibReDEAdapter.mapServicesToResources(workloadDescription);
		} else {
			EstimationSpecificationFactory.addServiceDemandLawToEstimationSpecification(estimationSpecification);
		}
		EstimationSpecificationFactory
		.addResponseTimeApproximationApproachToEstimationSpecification(estimationSpecification);
	}

	private static void addResponseTimeApproximationApproachToEstimationSpecification(
			EstimationSpecification estimationSpecification) {
		EstimationApproachConfiguration estimationApproachConfiguration = ConfigurationFactory.eINSTANCE
				.createEstimationApproachConfiguration();
		estimationApproachConfiguration
				.setType(ResponseTimeApproximationApproach.class
						.getCanonicalName());
		estimationSpecification.getApproaches().add(
				estimationApproachConfiguration);
	}

	private static void addZhangKalmannApproachToEstimationSpecification(
			EstimationSpecification estimationSpecification) {
		EstimationApproachConfiguration estimationApproachConfiguration = ConfigurationFactory.eINSTANCE
				.createEstimationApproachConfiguration();
		estimationApproachConfiguration.setType(WangKalmanFilterApproach.class
				.getCanonicalName());
		estimationSpecification.getApproaches().add(
				estimationApproachConfiguration);
		EstimationAlgorithmConfiguration estimationAlgorithmConfiguration = ConfigurationFactory.eINSTANCE.createEstimationAlgorithmConfiguration();
		estimationSpecification.getAlgorithms().add(estimationAlgorithmConfiguration);
		String iKalmanFilter = IKalmanFilterAlgorithm.class.getCanonicalName();
		estimationAlgorithmConfiguration.setType(iKalmanFilter );
		
	}

	private static void addServiceDemandLawToEstimationSpecification(
			EstimationSpecification estimationSpecification) {
		EstimationApproachConfiguration estimationApproachConfiguration = ConfigurationFactory.eINSTANCE
				.createEstimationApproachConfiguration();
		estimationApproachConfiguration.setType(ServiceDemandLawApproach.class
				.getCanonicalName());
		estimationSpecification.getApproaches().add(
				estimationApproachConfiguration);
	}

}
