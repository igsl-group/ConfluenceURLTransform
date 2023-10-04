package com.igsl.export.cloud.model;

public class ConfluencePage {
	private String id;
	private String status;
	private String title;
	private String spaceId;
	private String parentId;
	private String parentType;
	private int position;
	private String authorId;
	private String createdAt;
	private ConfluenceVersion version;
	private ConfluenceBody body;
	private ConfluenceLink _links;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSpaceId() {
		return spaceId;
	}
	public void setSpaceId(String spaceId) {
		this.spaceId = spaceId;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getParentType() {
		return parentType;
	}
	public void setParentType(String parentType) {
		this.parentType = parentType;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public String getAuthorId() {
		return authorId;
	}
	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	public ConfluenceVersion getVersion() {
		return version;
	}
	public void setVersion(ConfluenceVersion version) {
		this.version = version;
	}
	public ConfluenceBody getBody() {
		return body;
	}
	public void setBody(ConfluenceBody body) {
		this.body = body;
	}
	public ConfluenceLink get_links() {
		return _links;
	}
	public void set_links(ConfluenceLink _links) {
		this._links = _links;
	}
}
