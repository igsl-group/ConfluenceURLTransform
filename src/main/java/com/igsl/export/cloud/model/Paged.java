package com.igsl.export.cloud.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * REST API result that is paged
 * This is used by Jira REST APIs
 */
public interface Paged {
	@JsonIgnore
	public abstract int getPageTotal();
	@JsonIgnore
	public abstract int getPageSize();
	@JsonIgnore
	public abstract int getPageStartAt();
}
