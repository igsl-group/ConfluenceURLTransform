package com.igsl.config;

import java.util.ArrayList;
import java.util.List;

public class ObjectExport implements ConfigInterface {
	private List<String> handlers;
	private String outputDirectory;
	@Override
	public List<String> validate() {
		List<String> messages = new ArrayList<>();
		if (outputDirectory == null) {
			messages.add("outputDirectory is not specified");
		}
		if (handlers == null || handlers.size() == 0) {
			messages.add("No objectExport handlers are defined");
		}
		return messages;
	}
	public List<String> getHandlers() {
		return handlers;
	}
	public void setHandlers(List<String> handlers) {
		this.handlers = handlers;
	}
	public String getOutputDirectory() {
		return outputDirectory;
	}
	public void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}
}
