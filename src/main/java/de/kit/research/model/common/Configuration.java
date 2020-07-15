package de.kit.research.model.common;

import java.util.Properties;

public final class Configuration extends Properties {

	private static final long serialVersionUID = 3364877592243422259L;

	private String fileName;

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

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
