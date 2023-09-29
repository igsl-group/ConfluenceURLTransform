package com.igsl.config;

import java.util.ArrayList;
import java.util.List;

public class ObjectExport implements ConfigInterface {
	private List<String> handlers;
	@Override
	public List<String> validate() {
		List<String> messages = new ArrayList<>();
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
}
