package com.igsl.config;

import java.util.ArrayList;
import java.util.List;

public class Config {
	private static final String NEWLINE = System.getProperty("line.separator");	
	private Connections connections;
	private URLTransform urlTransform;	
	private ObjectExport objectExport;
	public void validate() throws Exception {
		List<String> messages = new ArrayList<>();
		if (connections != null) {
			messages.addAll(connections.validate());
		} else {
			messages.add("connections is not specified");
		}
		if (urlTransform != null) {
			messages.addAll(urlTransform.validate());
		} else {
			messages.add("urlTransform is not specified");
		}
		if (objectExport != null) {
			messages.addAll(objectExport.validate());
		} else {
			messages.add("objectExport is not specified");
		}
		if (!messages.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (String msg : messages) {
				sb.append(msg).append(NEWLINE);
			}
			throw new Exception(sb.toString());
		}
	}
	public Connections getConnections() {
		return connections;
	}
	public URLTransform getUrlTransform() {
		return urlTransform;
	}
	public void setConnections(Connections conncetions) {
		this.connections = conncetions;
	}
	public void setUrlTransform(URLTransform urlTransform) {
		this.urlTransform = urlTransform;
	}
	public ObjectExport getObjectExport() {
		return objectExport;
	}
	public void setObjectExport(ObjectExport objectExport) {
		this.objectExport = objectExport;
	}
}
