package com.igsl.export.cloud.model;

import java.util.List;

public class JiraProjects implements Paged {
	@Override
	public int getPageTotal() {
		return total;
	}
	@Override
	public int getPageSize() {
		return values.size();
	}
	@Override
	public int getPageStartAt() {
		return startAt;
	}
	private int startAt;
	private int maxResults;
	private int total;
	private List<JiraProject> values;
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
	public List<JiraProject> getValues() {
		return values;
	}
	public void setValues(List<JiraProject> values) {
		this.values = values;
	}
}
