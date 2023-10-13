package com.igsl.export.cloud.model;

public class ConfluencePageTemplate {
	private String templateId;
	private String name;
	private String description;
	private ConfluenceBody body;
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public ConfluenceBody getBody() {
		return body;
	}
	public void setBody(ConfluenceBody body) {
		this.body = body;
	}
}
