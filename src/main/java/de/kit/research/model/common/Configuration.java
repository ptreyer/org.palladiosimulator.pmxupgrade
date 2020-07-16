package de.kit.research.model.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public final class Configuration extends Properties {

    private static final long serialVersionUID = 3364877592243422259L;

    private String inputfileName;
    private String outputDirectory;
    private String tracer;
    private List<String> traceIDsToFilter;
    private Long ignoreBeforeTimestamp;
    private Long ignoreAfterTimestamp;
    private HashMap<String, Integer> numCores;

    /**
     * Creates a new (empty) configuration.
     */
    public Configuration() {
        this(null);
    }

    /**
     * Creates a new instance of this class using the given parameters.
     *
     * @param defaults The property object which delivers the default values for the new configuration.
     */
    public Configuration(final Properties defaults) {
        super(defaults);
    }

    public String getInputfileName() {
        return inputfileName;
    }

    public void setInputfileName(String inputfileName) {
        this.inputfileName = inputfileName;
    }

    public List<String> getTraceIDsToFilter() {
        if (traceIDsToFilter == null)
            return new ArrayList<>();
        return traceIDsToFilter;
    }

    public void setTraceIDsToFilter(List<String> traceIDsToFilter) {
        this.traceIDsToFilter = traceIDsToFilter;
    }

    public String getTracer() {
        return tracer;
    }

    public void setTracer(String tracer) {
        this.tracer = tracer;
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public Long getIgnoreBeforeTimestamp() {
        return ignoreBeforeTimestamp;
    }

    public void setIgnoreBeforeTimestamp(Long ignoreBeforeTimestamp) {
        this.ignoreBeforeTimestamp = ignoreBeforeTimestamp;
    }

    public Long getIgnoreAfterTimestamp() {
        return ignoreAfterTimestamp;
    }

    public void setIgnoreAfterTimestamp(Long ignoreAfterTimestamp) {
        this.ignoreAfterTimestamp = ignoreAfterTimestamp;
    }

    public HashMap<String, Integer> getNumCores() {
        return numCores;
    }

    public void setNumCores(HashMap<String, Integer> numCores) {
        this.numCores = numCores;
    }
}
