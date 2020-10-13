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
        configuration.setInputFileName("C:\\Users\\ptreyer\\Desktop\\evaluation_resources_loop_30_ps001\\json_loop30_ps001.json");
        configuration.setOutputDirectory("/test");

        PMXController pmxController = new PMXController(configuration);
        pmxController.readTracingData();

        TraceReconstruction filter = new TraceReconstruction();

        ProcessingObjectWrapper result = filter.filter(configuration, pmxController.getTraceRecord());

        result.getSystemModelRepository().saveSystemToHTMLFile("target/json_loop30_ps001.html");

        System.out.println("finish");
    }
}
