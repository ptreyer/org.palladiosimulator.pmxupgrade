package de.kit.research.logic;

import de.kit.research.logic.inputreader.InputReaderInterface;
import de.kit.research.logic.inputreader.impl.InputReaderOpenTracingImpl;
import de.kit.research.model.common.Configuration;
import de.kit.research.model.inputreader.InputObjectWrapper;

import java.io.IOException;

public class PMXController {

    private InputReaderInterface inputReaderInterface;

    private Configuration configuration;
    private InputObjectWrapper inputObjectWrapper;

    public PMXController(Configuration configuration, InputObjectWrapper inputObjectWrapper) {
        this.configuration = configuration;
        this.inputObjectWrapper = inputObjectWrapper;
        inputReaderInterface = new InputReaderOpenTracingImpl();
    }

    private void readTracingData(Configuration configuration){
        try {
            inputReaderInterface.readTracingData(configuration);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initAndConnectFilter(InputObjectWrapper inputObjectWrapper){

    }

    private void processTracingData(Configuration configuration){

    }

    private void createPerformanceModel(InputObjectWrapper inputObjectWrapper){

    }



}
