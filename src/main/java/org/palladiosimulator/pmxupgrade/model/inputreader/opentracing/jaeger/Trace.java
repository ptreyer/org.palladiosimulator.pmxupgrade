package org.palladiosimulator.pmxupgrade.model.inputreader.opentracing.jaeger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.List;

/**
 * Trace representation of the Opentracing Jaeger data model.
 *
 * @author Patrick Treyer
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Trace {

    private String traceID;

    /**
     * todo null check
     */
    private List<Span> spans;

    /**
     * todo map
     */
    private HashMap<String, Process> processes;

    @JsonIgnore
    private Long startTime;

    public String getTraceID() {
        return traceID;
    }

    public void setTraceID(String traceID) {
        this.traceID = traceID;
    }

    public List<Span> getSpans() {
        return spans;
    }

    public void setSpans(List<Span> spans) {
        this.spans = spans;
    }

    public HashMap<String, Process> getProcesses() {
        return processes;
    }

    public void setProcesses(HashMap<String, Process> processes) {
        this.processes = processes;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

}
