package org.palladiosimulator.pmxupgrade.logic.inputreader;

import org.palladiosimulator.pmxupgrade.model.common.Configuration;
import org.palladiosimulator.pmxupgrade.model.inputreader.InputObjectWrapper;

import java.io.IOException;

public interface InputReaderInterface {

    InputObjectWrapper readTracingData(Configuration configuration) throws IOException;

}
