package org.palladiosimulator.pmxupgrade.dataprocessing;

import org.junit.jupiter.api.Test;
import org.palladiosimulator.pmxupgrade.logic.PMXController;
import org.palladiosimulator.pmxupgrade.logic.dataprocessing.DataProcessingService;
import org.palladiosimulator.pmxupgrade.logic.filter.opentracing.TraceReconstruction;
import org.palladiosimulator.pmxupgrade.model.common.Configuration;
import org.palladiosimulator.pmxupgrade.model.exception.PMXException;
import org.palladiosimulator.pmxupgrade.model.inputreader.ProcessingObjectWrapper;

public class WorkloadTest {

    @Test
    void filter() throws PMXException {
        Configuration configuration = new Configuration();
        configuration.setInputFileName("src/test/resources/json/combination4.json");
        configuration.setOutputDirectory("/test");

        PMXController pmxController = new PMXController(configuration);
        pmxController.readTracingData();

        TraceReconstruction filter = new TraceReconstruction();

        ProcessingObjectWrapper processingObjectWrapper = filter.filter(configuration, pmxController.getTraceRecord());

        DataProcessingService service = new DataProcessingService();
        service.analyzeWorkload(processingObjectWrapper.getExecutionTraces());

        System.out.println("finish");

    }
}
