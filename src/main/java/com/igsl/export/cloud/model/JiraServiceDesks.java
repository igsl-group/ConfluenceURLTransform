package com.igsl.export.cloud.model;

import java.util.List;

public class JiraServiceDesks implements Paged {
	@Override
	public int getPageTotal() {
		return -1;
	}
	@Override
	public int getPageSize() {
		return size;
	}
	@Override
	public int getPageStartAt() {
		return start;
	}
	private int limit;
	private int size;
	private int start;
	private List<JiraServiceDesk> values;
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public List<JiraServiceDesk> getValues() {
		return values;
	}
	public void setValues(List<JiraServiceDesk> values) {
		this.values = values;
	}
}
