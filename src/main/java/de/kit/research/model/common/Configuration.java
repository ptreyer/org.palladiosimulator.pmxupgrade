package de.kit.research.model.common;

import java.util.HashMap;
import java.util.Properties;

public final class Configuration extends Properties {

	private static final long serialVersionUID = 3364877592243422259L;

	private String inputfileName;
	private String outputDirectory;
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
	 * @param defaults
	 *            The property object which delivers the default values for the new configuration.
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

	public String getOutputDirectory() {
		return outputDirectory;
	}

	public void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	public HashMap<String, Integer> getNumCores() {
		return numCores;
	}

	public void setNumCores(HashMap<String, Integer> numCores) {
		this.numCores = numCores;
	}
}
