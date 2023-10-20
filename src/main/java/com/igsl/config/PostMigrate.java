package com.igsl.config;

import java.util.Collections;
import java.util.List;

public class PostMigrate implements ConfigInterface {
	private boolean performUpdate;
	public boolean isPerformUpdate() {
		return performUpdate;
	}
	public void setPerformUpdate(boolean performUpdate) {
		this.performUpdate = performUpdate;
	}
	@Override
	public List<String> validate() {
		return Collections.emptyList();
	}
}
