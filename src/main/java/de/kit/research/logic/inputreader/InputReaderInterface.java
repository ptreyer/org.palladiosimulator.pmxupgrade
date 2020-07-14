package de.kit.research.logic.inputreader;

import de.kit.research.model.InputObjectWrapper;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface InputReaderInterface {

    InputObjectWrapper readTracingData(String filename) throws IOException;

}
