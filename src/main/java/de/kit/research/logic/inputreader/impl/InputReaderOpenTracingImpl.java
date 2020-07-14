package de.kit.research.logic.inputreader.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.kit.research.logic.inputreader.InputReaderInterface;
import de.kit.research.model.InputObjectWrapper;
import de.kit.research.model.opentracing.TraceRecord;

import java.io.*;

public class InputReaderOpenTracingImpl implements InputReaderInterface {

    @Override
    public InputObjectWrapper readTracingData(String filename) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File initialFile = new File(filename);
        InputStream targetStream = new FileInputStream(initialFile);
        TraceRecord traceRecord = mapper.readValue(targetStream, TraceRecord.class);
        return traceRecord;
    }

}
