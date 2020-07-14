package de.kit.research.model.opentracing;

import java.util.List;

public class Process {
	
	private String serviceName;
	private List<Tag> tags;
	
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public List<Tag> getTags() {
		return tags;
	}
	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}
	
}
