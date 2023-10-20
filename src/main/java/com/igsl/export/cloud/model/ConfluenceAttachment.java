package com.igsl.export.cloud.model;

public class ConfluenceAttachment extends Linked {
	private String id;
	private String title;
	private String pageId;
	private String fileId;
	private ConfluencePage page;
	private ConfluenceVersion version;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPageId() {
		return pageId;
	}
	public void setPageId(String pageId) {
		this.pageId = pageId;
	}
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public ConfluenceVersion getVersion() {
		return version;
	}
	public void setVersion(ConfluenceVersion version) {
		this.version = version;
	}
	public ConfluencePage getPage() {
		return page;
	}
	public void setPage(ConfluencePage page) {
		this.page = page;
	}
}
