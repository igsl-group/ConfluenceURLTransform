package com.igsl.handler;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class URLPattern {
	private static final Logger LOGGER = LogManager.getLogger(URLPattern.class);
	private Pattern pathPattern;
	/**
	 * URI must contain at least one of these to be accepted
	 */
	private List<Pattern> queryPatterns = new ArrayList<>();

	public URLPattern() {
	}

	public URLPattern(String pathRegex) {
		this.pathPattern = Pattern.compile(pathRegex);
	}

	public URLPattern setPathRegex(String pathRegex) {
		this.pathPattern = Pattern.compile(pathRegex);
		return this;
	}
	
	public URLPattern setPath(String path) {
		this.pathPattern = Pattern.compile(Pattern.quote(path) + "/?");
		return this;
	}

	public URLPattern setQuery(String... parameterNames) {
		this.queryPatterns.clear();
		for (String s : parameterNames) {
			this.queryPatterns.add(Pattern.compile(Pattern.quote(s) + "=[^&#]+"));
		}
		return this;
	}

	public boolean match(URI uri) {
		String path = uri.getPath();
		String query = uri.getQuery();
		LOGGER.debug("URLPattern checking: [" + path + "] [" + query + "] against [" + pathPattern.pattern() + "]");
		if (!pathPattern.matcher(path).matches()) {
			LOGGER.debug("URLPattern = false");
			return false;
		}
		boolean queryMatched = (queryPatterns.size() == 0);
		if (queryPatterns.size() != 0 && query != null) {
			for (Pattern p : queryPatterns) {
				LOGGER.debug("URLPattern against query: [" + p.pattern() + "]");
				if (p.matcher(query).find()) {
					queryMatched = true;
					break;
				}
			}
		}
		LOGGER.debug("URLPattern = " + queryMatched);
		return queryMatched;
	}

	public Pattern getPathPattern() {
		return pathPattern;
	}

	public List<Pattern> getQueryPatterns() {
		return queryPatterns;
	}
}