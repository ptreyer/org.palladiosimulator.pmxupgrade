package de.kit.research.logic.inputreader.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.kit.research.logic.inputreader.InputReaderInterface;
import de.kit.research.model.common.Configuration;
import de.kit.research.model.inputreader.InputObjectWrapper;
import de.kit.research.model.inputreader.opentracing.jaeger.TraceRecord;

import java.io.*;

public class InputReaderOpenTracingImpl implements InputReaderInterface {

    @Override
    public InputObjectWrapper readTracingData(Configuration configuration) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File initialFile = new File(configuration.getInputfileName());
        InputStream targetStream = new FileInputStream(initialFile);
        return mapper.readValue(targetStream, TraceRecord.class);
    }

}
