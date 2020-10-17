package org.palladiosimulator.pmxupgrade.model.inputreader.opentracing.jaeger;

/**
 * Tag representation of the Opentracing Jaeger data model.
 *
 * @author Patrick Treyer
 */
public class Tag {

    private String key;
    private String type;
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


}
