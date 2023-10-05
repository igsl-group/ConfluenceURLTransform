package com.igsl.export.cloud.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JiraIssues implements Paged {
	@Override
	public int getPageTotal() {
		return total;
	}
	@Override
	public int getPageSize() {
		return issues.size();
	}
	@Override
	public int getPageStartAt() {
		return startAt;
	}
	private String expand;
	private int startAt;
	private int maxResults;
	private int total;
	private List<JiraIssue> issues;
	public String getExpand() {
		return expand;
	}
	public void setExpand(String expand) {
		this.expand = expand;
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
	public List<JiraIssue> getIssues() {
		return issues;
	}
	public void setIssues(List<JiraIssue> issues) {
		this.issues = issues;
	}
}
