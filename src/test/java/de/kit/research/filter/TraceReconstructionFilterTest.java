package de.kit.research.filter;

import de.kit.research.logic.PMXController;
import de.kit.research.logic.filter.opentracing.TraceReconstructionFilter;
import de.kit.research.model.common.Configuration;
import de.kit.research.model.exception.PMXException;
import de.kit.research.model.inputreader.ProcessingObjectWrapper;
import de.kit.research.model.inputreader.opentracing.jaeger.Span;
import de.kit.research.model.inputreader.opentracing.jaeger.Trace;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

public class TraceReconstructionFilterTest {

    @Test
    void filter() throws PMXException, FileNotFoundException, UnsupportedEncodingException {
        Configuration configuration = new Configuration();
        configuration.setInputFileName("C:\\Users\\ptreyer\\workspace\\pmxupgrade\\src\\test\\resources\\json\\combination3.json");

        PMXController pmxController = new PMXController(configuration);
        pmxController.readTracingData();

        TraceReconstructionFilter filter = new TraceReconstructionFilter();

        ProcessingObjectWrapper result = filter.filter(configuration, pmxController.getTraceRecord());

        result.getSystemModelRepository().saveSystemToHTMLFile("test.html");

        System.out.println("finish");
    }
}
