package de.kit.research.logic.inputreader;

import de.kit.research.model.common.Configuration;
import de.kit.research.model.inputreader.InputObjectWrapper;

import java.io.IOException;

public interface InputReaderInterface {

    InputObjectWrapper readTracingData(Configuration configuration) throws IOException;

}
