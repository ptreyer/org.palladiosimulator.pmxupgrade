package de.kit.research.dataprocessing;

import de.kit.research.logic.PMXController;
import de.kit.research.logic.dataprocessing.DataProcessingService;
import de.kit.research.logic.dataprocessing.controlflow.ControlFlowService;
import de.kit.research.logic.filter.opentracing.TraceReconstructionFilter;
import de.kit.research.model.common.Configuration;
import de.kit.research.model.exception.PMXException;
import org.junit.jupiter.api.Test;

public class WorkloadTest {

    @Test
    void filter() throws PMXException {
        Configuration configuration = new Configuration();
        configuration.setInputFileName("C:\\Users\\ptreyer\\workspace\\pmxupgrade\\src\\test\\resources\\json\\combination3.json");

        PMXController pmxController = new PMXController(configuration);
        pmxController.readTracingData();

        TraceReconstructionFilter filter = new TraceReconstructionFilter();

        filter.filter(configuration, pmxController.getTraceRecord());

        DataProcessingService service = new DataProcessingService();
        service.analyzeWorkload(filter.executionTraces);

        System.out.println("finish");

    }
}
