package org.palladiosimulator.pmxupgrade.model.inputreader.opentracing.jaeger;

import java.util.List;

public class Log {

    private long timestamp;
    private List<Tag> fields;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<Tag> getFields() {
        return fields;
    }

    public void setFields(List<Tag> fields) {
        this.fields = fields;
    }

}
