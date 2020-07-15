package de.kit.research.logic;

import de.kit.research.logic.inputreader.InputReaderInterface;
import de.kit.research.logic.inputreader.impl.InputReaderOpenTracingImpl;
import de.kit.research.model.common.Configuration;
import de.kit.research.model.constants.PMXConstants;
import de.kit.research.model.exception.PMXException;
import de.kit.research.model.inputreader.InputObjectWrapper;

import java.io.IOException;

public class PMXController {

    private InputReaderInterface inputReaderInterface;

    private Configuration configuration;
    private InputObjectWrapper inputObjectWrapper;

    public PMXController(Configuration configuration) {
        this.configuration = configuration;
        inputReaderInterface = new InputReaderOpenTracingImpl();
    }

    public void readTracingData() throws PMXException {
        try {
            inputObjectWrapper = inputReaderInterface.readTracingData(configuration);
        } catch (IOException e) {
            throw new PMXException(PMXConstants.FEHLER_DATENEINLESE);
        }
    }

    public void initAndConnectFilter(){

    }

    public void processTracingData(){

    }

    public void createPerformanceModel(){

    }

    public InputObjectWrapper getInputObjectWrapper() {
        return inputObjectWrapper;
    }

    public void setInputObjectWrapper(InputObjectWrapper inputObjectWrapper) {
        this.inputObjectWrapper = inputObjectWrapper;
    }
}
