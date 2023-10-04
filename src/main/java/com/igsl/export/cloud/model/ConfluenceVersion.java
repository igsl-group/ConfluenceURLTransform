package com.igsl.export.cloud.model;

public class ConfluenceVersion {
	private String createdAt;
	private String message;
	private int number;
	private boolean minorEdit;
	private String authorId;
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public boolean isMinorEdit() {
		return minorEdit;
	}
	public void setMinorEdit(boolean minorEdit) {
		this.minorEdit = minorEdit;
	}
	public String getAuthorId() {
		return authorId;
	}
	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}
}
