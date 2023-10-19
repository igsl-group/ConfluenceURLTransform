package com.igsl.export.cloud.model;

import java.util.List;

public class JiraProjectVersions implements Paged {
	private int startAt;
	private int maxResults;
	private int total;
	private List<JiraProjectVersion> values;
	private JiraProject project;
	@Override
	public int getPageTotal() {
		return total;
	}
	@Override
	public int getPageSize() {
		return maxResults;
	}
	@Override
	public int getPageStartAt() {
		return startAt;
	}
	public int getStartAt() {
		return startAt;
	}
	public void setStartAt(int startAt) {
		this.startAt = startAt;
	}
	public int getMaxResults() {
		return maxResults;
	}
	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public List<JiraProjectVersion> getValues() {
		return values;
	}
	public void setValues(List<JiraProjectVersion> values) {
		this.values = values;
	}
	public JiraProject getProject() {
		return project;
	}
	public void setProject(JiraProject project) {
		this.project = project;
	}
}
