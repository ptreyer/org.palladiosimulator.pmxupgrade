import com.fasterxml.jackson.databind.ObjectMapper;
import de.kit.research.model.opentracing.Span;
import de.kit.research.model.opentracing.Trace;
import de.kit.research.model.opentracing.TraceRecord;
import org.junit.jupiter.api.Test;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class ImportJsonTest {

    @Test
    void test() {
        ObjectMapper mapper = new ObjectMapper();
        File initialFile = new File("C:\\Users\\ptreyer\\workspace\\pmxupgrade\\src\\test\\resources\\json\\trace1.json");
        try {
            InputStream targetStream = new FileInputStream(initialFile);
            TraceRecord traceRecord = mapper.readValue(targetStream, TraceRecord.class);
            System.out.println(traceRecord);

            for (Trace trace : traceRecord.getData()) {
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
}
