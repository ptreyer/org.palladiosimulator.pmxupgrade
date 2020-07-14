package de.kit.research.model.inputreader.opentracing;

public class Reference {

	/**
	 * to verify, enum
	 */
	private String refType;
	private String traceID;
	private String spanID;
	public String getRefType() {
		return refType;
	}
	public void setRefType(String refType) {
		this.refType = refType;
	}
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
	
	
	
}
