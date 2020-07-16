package de.kit.research.filter;

import de.kit.research.logic.PMXController;
import de.kit.research.model.common.Configuration;
import de.kit.research.model.exception.PMXException;
import de.kit.research.model.inputreader.opentracing.jaeger.Span;
import de.kit.research.model.inputreader.opentracing.jaeger.Trace;
import de.kit.research.model.inputreader.opentracing.jaeger.TraceRecord;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FilterTest {


    @Test
    void dateTest() {
        long time = Long.parseLong("1594631346638");
        Date date = new Date(time);
        Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String dateString = format.format(date);

        System.out.println("Date: " + dateString);
    }

    @Test
    void importJSON() throws PMXException {
        Configuration configuration = new Configuration();
        configuration.setInputfileName("C:\\Users\\ptreyer\\workspace\\pmxupgrade\\src\\test\\resources\\json\\combination.json");

        Long time = Long.parseLong("1594631346638");
        configuration.setIgnoreAfterTimestamp(time);

        List<String> traceIdsToFilter = new ArrayList<>();
        traceIdsToFilter.add("180cb19cb3a6d11d");
        configuration.setTraceIDsToFilter(traceIdsToFilter);

        PMXController pmxController = new PMXController(configuration);
        pmxController.readTracingData();
        pmxController.initAndExecuteFilter();

        for (Trace trace : pmxController.getTraceRecord().getData()) {
            System.out.println("-----");
            System.out.println("Trace: ");
            System.out.println(trace.getStartTime());
            System.out.println(trace.getTraceID());
            for (Span span : trace.getSpans()) {
                System.out.println("Span: ");
                System.out.println(span.getStartTime());
            }

        }
    }
}
