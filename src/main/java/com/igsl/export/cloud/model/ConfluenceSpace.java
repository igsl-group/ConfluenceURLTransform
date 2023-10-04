package com.igsl.export.cloud.model;

import java.util.Date;

public class ConfluenceSpace {
	private String name;
	private String key;
	private String id;
	private String type;
	private String description;
	private String icon;
	private String homepageId;
	private String status;
	private Date createdAt;
	private String authorId;
	private ConfluenceLink _links;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getHomepageId() {
		return homepageId;
	}
	public void setHomepageId(String homepageId) {
		this.homepageId = homepageId;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public String getAuthorId() {
		return authorId;
	}
	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public ConfluenceLink get_links() {
		return _links;
	}
	public void set_links(ConfluenceLink _links) {
		this._links = _links;
	}
}