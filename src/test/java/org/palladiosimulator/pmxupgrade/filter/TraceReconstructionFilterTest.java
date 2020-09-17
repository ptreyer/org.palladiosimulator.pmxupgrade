package org.palladiosimulator.pmxupgrade.filter;

import org.palladiosimulator.pmxupgrade.logic.PMXController;
import org.palladiosimulator.pmxupgrade.logic.filter.opentracing.TraceReconstruction;
import org.palladiosimulator.pmxupgrade.model.common.Configuration;
import org.palladiosimulator.pmxupgrade.model.exception.PMXException;
import org.palladiosimulator.pmxupgrade.model.inputreader.ProcessingObjectWrapper;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

public class TraceReconstructionFilterTest {

    @Test
    void filter() throws PMXException, FileNotFoundException, UnsupportedEncodingException {
        Configuration configuration = new Configuration();
        configuration.setInputFileName("src/test/resources/json/combination4.json");
        configuration.setOutputDirectory("/test");

        PMXController pmxController = new PMXController(configuration);
        pmxController.readTracingData();

        TraceReconstruction filter = new TraceReconstruction();

        ProcessingObjectWrapper result = filter.filter(configuration, pmxController.getTraceRecord());

        result.getSystemModelRepository().saveSystemToHTMLFile("target/test.html");

        System.out.println("finish");
    }
}
