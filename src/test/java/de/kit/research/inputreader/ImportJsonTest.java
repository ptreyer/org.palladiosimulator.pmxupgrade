package de.kit.research.inputreader;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.kit.research.logic.PMXController;
import de.kit.research.model.common.Configuration;
import de.kit.research.model.exception.PMXException;
import de.kit.research.model.inputreader.opentracing.jaeger.Span;
import de.kit.research.model.inputreader.opentracing.jaeger.Trace;
import de.kit.research.model.inputreader.opentracing.jaeger.TraceRecord;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class ImportJsonTest {

    @Test
    void readData() {
        ObjectMapper mapper = new ObjectMapper();
        File initialFile = new File("C:\\Users\\ptreyer\\workspace\\pmxupgrade\\src\\test\\resources\\json\\combination3.json");
        try {
            InputStream targetStream = new FileInputStream(initialFile);
            TraceRecord traceRecord = mapper.readValue(targetStream, TraceRecord.class);
            System.out.println(traceRecord);

            for (Trace trace : traceRecord.getData()) {
                System.out.println("-----");
                System.out.println("Trace: ");
                System.out.println(trace.getTraceID());
                for (Span span : trace.getSpans()) {
                    System.out.println("Span: ");
                    System.out.println(span.getSpanID());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void importJSON() throws PMXException {
        Configuration configuration = new Configuration();
        configuration.setInputFileName("C:\\Users\\ptreyer\\workspace\\pmxupgrade\\src\\test\\resources\\json\\combination.json");
        configuration.setOutputDirectory("/test");

        PMXController pmxController = new PMXController(configuration);
        pmxController.readTracingData();

        if(pmxController.getInputObjectWrapper() instanceof TraceRecord) {
            TraceRecord traceRecord = (TraceRecord) pmxController.getInputObjectWrapper();

            for (Trace trace : traceRecord.getData()) {
                System.out.println("-----");
                System.out.println("Trace: ");
                System.out.println(trace.getTraceID());
                for (Span span : trace.getSpans()) {
                    System.out.println("Span: ");
                    System.out.println(span.getSpanID());
                }
            }

        }
    }
}
