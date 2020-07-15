package de.kit.research.model.inputreader.opentracing.jaeger;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.List;

public class Trace {

	private String traceID;
	private List<Span> spans;
	private HashMap<String, Process> processes;

	@JsonIgnore
	private Long startTime;

	private int warnings;

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

	public int getWarnings() {
		return warnings;
	}

	public void setWarnings(int warnings) {
		this.warnings = warnings;
	}
}
