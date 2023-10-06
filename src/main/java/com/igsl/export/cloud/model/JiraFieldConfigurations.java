package com.igsl.export.cloud.model;

import java.util.List;

public class JiraFieldConfigurations implements Paged {
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
	private int maxResults;
	private int startAt;
	private int total;
	private List<JiraFieldConfiguration> values;
	public int getMaxResults() {
		return maxResults;
	}
	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}
	public int getStartAt() {
		return startAt;
	}
	public void setStartAt(int startAt) {
		this.startAt = startAt;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public List<JiraFieldConfiguration> getValues() {
		return values;
	}
	public void setValues(List<JiraFieldConfiguration> values) {
		this.values = values;
	}
}
