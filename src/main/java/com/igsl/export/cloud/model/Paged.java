package com.igsl.export.cloud.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * REST API result that is paged
 * This is used by Jira REST APIs
 */
public interface Paged {
	@JsonIgnore
	public int getTotal();
	@JsonIgnore
	public int getSize();
	@JsonIgnore
	public String getStartParameterName();
}
