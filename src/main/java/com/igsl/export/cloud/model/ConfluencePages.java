package com.igsl.export.cloud.model;

import java.util.List;

public class ConfluencePages extends Linked {
	private List<ConfluencePage> results;
	public List<ConfluencePage> getResults() {
		return results;
	}
	public void setResults(List<ConfluencePage> results) {
		this.results = results;
	}
}
