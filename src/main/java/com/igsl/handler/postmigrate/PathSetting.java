package com.igsl.handler.postmigrate;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class PathSetting {
	private Pattern pathPattern;
	private Class<?> baseExport;
	public PathSetting(
			Pattern pathPattern,
			Class<?> baseExport) {
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
}