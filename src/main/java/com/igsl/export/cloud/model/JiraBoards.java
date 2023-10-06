package com.igsl.export.cloud.model;

import java.util.List;

public class JiraBoards implements Paged {
	@Override
	public int getPageTotal() {
		return 0;
	}
	@Override
	public int getPageSize() {
		return 0;
	}
	@Override
	public int getPageStartAt() {
		return 0;
	}
	private int startAt;
	private int maxResults;
	private int total;
	private List<JiraBoard> values;
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
	public List<JiraBoard> getValues() {
		return values;
	}
	public void setValues(List<JiraBoard> values) {
		this.values = values;
	}
}
