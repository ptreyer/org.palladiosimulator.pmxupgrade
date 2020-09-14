package org.palladiosimulator.pmxupgrade.logic.inputreader.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.palladiosimulator.pmxupgrade.logic.inputreader.InputReaderInterface;
import org.palladiosimulator.pmxupgrade.model.common.Configuration;
import org.palladiosimulator.pmxupgrade.model.inputreader.InputObjectWrapper;
import org.palladiosimulator.pmxupgrade.model.inputreader.opentracing.jaeger.TraceRecord;

import java.io.*;

public class InputReaderOpenTracingImpl implements InputReaderInterface {

    @Override
    public InputObjectWrapper readTracingData(Configuration configuration) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File initialFile = new File(configuration.getInputFileName());
        InputStream targetStream = new FileInputStream(initialFile);
        return mapper.readValue(targetStream, TraceRecord.class);
    }

}
