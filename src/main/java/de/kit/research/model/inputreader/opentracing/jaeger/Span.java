package de.kit.research.model.inputreader.opentracing.jaeger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.codehaus.plexus.util.CollectionUtils;
import org.codehaus.plexus.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Span {

    private String traceID;
    private String spanID;
    private int flags;
    private String operationName;
    private List<Reference> references;
    private Long startTime;
    private Long duration;
    private List<Tag> tags;

    private List<Log> logs;
    private String processID;

    private String component;
    private String componentType;
    private String[] operationParameters;
    private String operationReturnType;
    private String childOf;

    public String getTraceID() {
        return traceID;
    }

    public void setTraceID(String traceID) {
        this.traceID = traceID;
    }

    public String getSpanID() {
        return spanID;
    }

    public void setSpanID(String spanID) {
        this.spanID = spanID;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public List<Reference> getReferences() {
        return references;
    }

    public void setReferences(List<Reference> references) {
        this.references = references;
    }

    public Long getStartTime() {
        return TimeUnit.NANOSECONDS.toMicros(startTime);
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Log> getLogs() {
        return logs;
    }

    public void setLogs(List<Log> logs) {
        this.logs = logs;
    }

    public String getProcessID() {
        return processID;
    }

    public void setProcessID(String processID) {
        this.processID = processID;
    }

    public String getComponent() {
        if (StringUtils.isEmpty(component)) {
            this.getTags().forEach(t -> {
                if (StringUtils.equalsIgnoreCase(t.getKey(), "component"))
                    component = t.getValue();
            });
        }
        return component;
    }

    public String getComponentType() {
        if (StringUtils.isEmpty(componentType)) {
            this.getTags().forEach(t -> {
                if (StringUtils.equalsIgnoreCase(t.getKey(), "componentType"))
                    componentType = t.getValue();
            });
        }
        return componentType;
    }


    public String[] getParameters() {
        if (operationParameters == null) {
            this.getTags().forEach(t -> {
                if (StringUtils.equalsIgnoreCase(t.getKey(), "paramType")) {
                    operationParameters = StringUtils.split(t.getValue(), ",");
                }
            });
        }
        return operationParameters;
    }

    public String getReturnType() {
        if (StringUtils.isEmpty(operationReturnType)) {
            this.getTags().forEach(t -> {
                if (StringUtils.equalsIgnoreCase(t.getKey(), "returnType"))
                    operationReturnType = t.getValue();
            });
        }
        return operationReturnType;
    }

    public String getChildOf() {
        if (StringUtils.isEmpty(childOf)) {
            if (this.getReferences() == null)
                return null;

            this.getReferences().forEach(r -> {
                if (StringUtils.equalsIgnoreCase(r.getRefType(), "CHILD_OF"))
                    childOf = r.getSpanID();
            });
        }
        return childOf;
    }

    public void setOperationParameters(String[] operationParameters) {
        this.operationParameters = operationParameters;
    }
}
