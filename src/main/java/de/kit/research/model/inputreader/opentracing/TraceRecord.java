package de.kit.research.model.inputreader.opentracing;

import de.kit.research.model.inputreader.InputObjectWrapper;

import java.util.List;

/** 
 * 
 * @author ptreyer
 *
 */
public class TraceRecord extends InputObjectWrapper {
	
	private List<Trace> data;

	public List<Trace> getData() {
		return data;
	}

	public void setData(List<Trace> data) {
		this.data = data;
	}
}
