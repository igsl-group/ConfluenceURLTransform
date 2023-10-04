package com.igsl.export.cloud.model;

import java.util.List;

public class ConfluenceSpaces extends Linked {
	private List<ConfluenceSpace> results;
	public List<ConfluenceSpace> getResults() {
		return results;
	}
	public void setResults(List<ConfluenceSpace> results) {
		this.results = results;
	}
}
