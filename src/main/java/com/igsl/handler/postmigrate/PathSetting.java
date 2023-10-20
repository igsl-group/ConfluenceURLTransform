package com.igsl.handler.postmigrate;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class PathSetting {
	private Class<? extends BasePostMigrate> postMigrate;
	private Pattern pathPattern;
	private Class<?> baseExport;
	public PathSetting(
			Class<? extends BasePostMigrate> postMigrate,
			Pattern pathPattern,
			Class<?> baseExport) {
		this.postMigrate = postMigrate;
		this.pathPattern = pathPattern;
		this.baseExport = baseExport;
	}
	public abstract String getReplacement(Matcher m, Map<String, Map<String, String>> mappings);
	public Pattern getPathPattern() {
		return pathPattern;
	}
	public Class<?> getBaseExport() {
		return baseExport;
	}
	public Class<? extends BasePostMigrate> getPostMigrate() {
		return postMigrate;
	}
}