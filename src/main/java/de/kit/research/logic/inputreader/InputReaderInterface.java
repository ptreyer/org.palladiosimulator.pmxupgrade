package de.kit.research.logic.inputreader;

import de.kit.research.model.inputreader.InputObjectWrapper;

import java.io.IOException;

public interface InputReaderInterface {

    InputObjectWrapper readTracingData(String filename) throws IOException;

}
