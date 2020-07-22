package de.kit.research.dataprocessing;

import de.kit.research.logic.PMXController;
import de.kit.research.logic.dataprocessing.componentdetection.ComponentDetectionService;
import de.kit.research.model.common.Configuration;
import de.kit.research.model.exception.PMXException;
import de.kit.research.model.inputreader.opentracing.jaeger.TraceRecord;
import de.kit.research.model.systemmodel.component.ExecutionContainer;
import de.kit.research.model.systemmodel.repository.SystemModelRepository;
import org.junit.jupiter.api.Test;

public class DataProcessingTest {

    @Test
    void detectComponents() throws PMXException {
        Configuration configuration = new Configuration();
        configuration.setInputFileName("C:\\Users\\ptreyer\\workspace\\pmxupgrade\\src\\test\\resources\\json\\combination2.json");

        PMXController pmxController = new PMXController(configuration);
        pmxController.readTracingData();

        SystemModelRepository systemModelRepository = new SystemModelRepository();

        if (!(pmxController.getInputObjectWrapper() instanceof TraceRecord))
            return;

        TraceRecord traceRecord = (TraceRecord) pmxController.getInputObjectWrapper();

        ComponentDetectionService service = new ComponentDetectionService();
        systemModelRepository = service.detectComponents(systemModelRepository, traceRecord);

        for (ExecutionContainer container: systemModelRepository.getExecutionEnvironmentFactory().getExecutionContainers()){
            System.out.println("Container: " + container.getIdentifier());
        }
    }

}
