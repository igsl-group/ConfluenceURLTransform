package com.igsl.handler;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class URLPattern {
	private Pattern pathPattern;
	private List<Pattern> queryPatterns = new ArrayList<>();
	private List<Pattern> optionalQueryPatterns = new ArrayList<>();

	public URLPattern() {
	}

	public URLPattern(String pathRegex) {
		this.pathPattern = Pattern.compile(pathRegex);
	}

	public URLPattern setPath(String path) {
		this.pathPattern = Pattern.compile(Pattern.quote(path));
		return this;
	}

	public URLPattern setQuery(String... parameterNames) {
		this.queryPatterns.clear();
		for (String s : parameterNames) {
			this.queryPatterns.add(Pattern.compile(Pattern.quote(s) + "=[^&#]+"));
		}
		return this;
	}

	public URLPattern setOptionalQuery(String... parameterNames) {
		this.optionalQueryPatterns.clear();
		for (String s : parameterNames) {
			this.optionalQueryPatterns.add(Pattern.compile(Pattern.quote(s) + "=[^&#]+"));
		}
		return this;
	}

	public boolean match(URI uri) {
		String path = uri.getPath();
		String query = uri.getQuery();
		if (!pathPattern.matcher(path).matches()) {
			return false;
		}
		for (Pattern p : queryPatterns) {
			if (!p.matcher(query).matches()) {
				return false;
			}
		}
		return true;
	}

	public Pattern getPathPattern() {
		return pathPattern;
	}

	public List<Pattern> getQueryPatterns() {
		return queryPatterns;
	}

	public List<Pattern> getOptionalQueryPatterns() {
		return optionalQueryPatterns;
	}
}