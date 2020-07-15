package de.kit.research.model.inputreader.opentracing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import de.kit.research.model.inputreader.InputObjectWrapper;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/** 
 * 
 * @author ptreyer
 *
 */
@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TraceRecord extends InputObjectWrapper {
	
	private List<Trace> data;

	public List<Trace> getData() {
		return data;
	}

	public void setData(List<Trace> data) {
		this.data = data;
	}
}
