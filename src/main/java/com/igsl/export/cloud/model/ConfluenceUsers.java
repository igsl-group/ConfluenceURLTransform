package com.igsl.export.cloud.model;

import java.util.List;

public class ConfluenceUsers implements Paged {
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
	private List<ConfluenceSearchResult> results;
	private int start;
	private int limit;
	private int size;
	private int totalSize;
	private String cqlQuery;
	private int searchDuration;
	private int archivedResultCount;
	private ConfluenceLink _links;
	public List<ConfluenceSearchResult> getResults() {
		return results;
	}
	public void setResults(List<ConfluenceSearchResult> results) {
		this.results = results;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public int getTotalSize() {
		return totalSize;
	}
	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public String getCqlQuery() {
		return cqlQuery;
	}
	public void setCqlQuery(String cqlQuery) {
		this.cqlQuery = cqlQuery;
	}
	public int getSearchDuration() {
		return searchDuration;
	}
	public void setSearchDuration(int searchDuration) {
		this.searchDuration = searchDuration;
	}
	public int getArchivedResultCount() {
		return archivedResultCount;
	}
	public void setArchivedResultCount(int archivedResultCount) {
		this.archivedResultCount = archivedResultCount;
	}
	public ConfluenceLink get_links() {
		return _links;
	}
	public void set_links(ConfluenceLink _links) {
		this._links = _links;
	}
}
