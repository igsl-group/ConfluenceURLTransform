package com.igsl.config;

import java.util.List;

public class PostMigrate {
	private boolean performUpdate;
	private List<String> postMigrateHandlers;
	public List<String> getPostMigrateHandlers() {
		return postMigrateHandlers;
	}
	public void setPostMigrateHandlers(List<String> postMigrateHandlers) {
		this.postMigrateHandlers = postMigrateHandlers;
	}
	public boolean isPerformUpdate() {
		return performUpdate;
	}
	public void setPerformUpdate(boolean performUpdate) {
		this.performUpdate = performUpdate;
	}
}
