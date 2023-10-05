package com.igsl.export.cloud.model;

public class JiraAttachment {
	private String id;
	private String filename;
	private JiraUser author;
	private long size;
	private String mimeType;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public JiraUser getAuthor() {
		return author;
	}
	public void setAuthor(JiraUser author) {
		this.author = author;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
}
