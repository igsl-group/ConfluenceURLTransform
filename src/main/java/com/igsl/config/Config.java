package com.igsl.config;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Config {
	private static final String NEWLINE = System.getProperty("line.separator");	
	private Handler handler;
	private Cloud cloud;
	private Connections connections;
	private URLTransform urlTransform;	
	private ObjectExport dcExport;
	private PostMigrate postMigrate;
	private Blueprint blueprint;
	@JsonIgnore
	private Path outputDirectory;
	@JsonIgnore
	private Path dcExportDirectory;	
	@JsonIgnore
	private Path cloudExportDirectory;
	@JsonIgnore
	private Path urlExportDirectory;
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
		if (dcExport != null) {
			messages.addAll(dcExport.validate());
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
	public Path getOutputDirectory() {
		return outputDirectory;
	}
	public void setOutputDirectory(Path outputDirectory) {
		this.outputDirectory = outputDirectory;
	}
	public Cloud getCloud() {
		return cloud;
	}
	public void setCloud(Cloud cloud) {
		this.cloud = cloud;
	}
	public ObjectExport getDcExport() {
		return dcExport;
	}
	public void setDcExport(ObjectExport dcExport) {
		this.dcExport = dcExport;
	}
	public Path getDcExportDirectory() {
		return dcExportDirectory;
	}
	public void setDcExportDirectory(Path dcExportDirectory) {
		this.dcExportDirectory = dcExportDirectory;
	}
	public Path getCloudExportDirectory() {
		return cloudExportDirectory;
	}
	public void setCloudExportDirectory(Path cloudExportDirectory) {
		this.cloudExportDirectory = cloudExportDirectory;
	}
	public Path getUrlExportDirectory() {
		return urlExportDirectory;
	}
	public void setUrlExportDirectory(Path urlExportDirectory) {
		this.urlExportDirectory = urlExportDirectory;
	}
	public Handler getHandler() {
		return handler;
	}
	public void setHandler(Handler handler) {
		this.handler = handler;
	}
	public PostMigrate getPostMigrate() {
		return postMigrate;
	}
	public void setPostMigrate(PostMigrate postMigrate) {
		this.postMigrate = postMigrate;
	}
	public Blueprint getBlueprint() {
		return blueprint;
	}
	public void setBlueprint(Blueprint blueprint) {
		this.blueprint = blueprint;
	}
}
