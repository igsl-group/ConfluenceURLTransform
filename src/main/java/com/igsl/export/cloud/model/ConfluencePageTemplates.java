package com.igsl.export.cloud.model;

import java.util.List;

public class ConfluencePageTemplates extends Linked {
	private List<ConfluencePageTemplate> results;
	public List<ConfluencePageTemplate> getResults() {
		return results;
	}
	public void setResults(List<ConfluencePageTemplate> results) {
		this.results = results;
	}
}
