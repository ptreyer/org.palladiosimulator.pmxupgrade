package org.palladiosimulator.pmxupgrade.dataprocessing;

import org.palladiosimulator.pmxupgrade.logic.PMXController;
import org.palladiosimulator.pmxupgrade.logic.dataprocessing.DataProcessingService;
import org.palladiosimulator.pmxupgrade.logic.filter.opentracing.TraceReconstructionFilter;
import org.palladiosimulator.pmxupgrade.model.common.Configuration;
import org.palladiosimulator.pmxupgrade.model.exception.PMXException;
import org.palladiosimulator.pmxupgrade.model.inputreader.ProcessingObjectWrapper;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

public class ResourceDemandTest {

    @Test
    void filter() throws PMXException {
        Configuration configuration = new Configuration();
        configuration.setInputFileName("src/test/resources/json/combination4.json");
        configuration.setOutputDirectory("/test");

        PMXController pmxController = new PMXController(configuration);
        pmxController.readTracingData();

        TraceReconstructionFilter filter = new TraceReconstructionFilter();

        ProcessingObjectWrapper processingObjectWrapper = filter.filter(configuration, pmxController.getTraceRecord());

        DataProcessingService service = new DataProcessingService();
        HashMap<String, Double> estimationResults =  service.calculateResourceDemands(processingObjectWrapper.getExecutionTraces());

        System.out.println("HashMap<String, Double>: " + estimationResults.size());

    }
}
