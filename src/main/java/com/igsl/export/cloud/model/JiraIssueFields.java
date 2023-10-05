package com.igsl.export.cloud.model;

import java.util.List;

public class JiraIssueFields {
	private List<JiraAttachment> attachment;
	public List<JiraAttachment> getAttachment() {
		return attachment;
	}
	public void setAttachment(List<JiraAttachment> attachment) {
		this.attachment = attachment;
	}
}
