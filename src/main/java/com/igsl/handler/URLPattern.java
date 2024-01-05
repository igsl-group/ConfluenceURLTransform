package com.igsl.handler;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igsl.Log;

public class URLPattern {
	private static final Logger LOGGER = LogManager.getLogger(URLPattern.class);
	private Pattern pathPattern;
	/**
	 * URI must contain at least one of these to be accepted
	 */
	private Map<String, Pattern> queryPatterns = new HashMap<>();

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
			this.queryPatterns.put(s, Pattern.compile(Pattern.quote(s) + "=([^&#]+)"));
		}
		return this;
	}

	public boolean match(URI uri) {
		return match(uri.getPath(), uri.getQuery());
	}
	
	public Matcher getPathMatcher(URI uri) {
		return this.pathPattern.matcher(uri.getPath());
	}
	
	public Map<String, Matcher> getQueryMatchers(URI uri) {
		if (this.queryPatterns.size() != 0) {
			Map<String, Matcher> result = new HashMap<>();
			String query = null;
			try {
				query = Handler.decode(uri.getQuery());
			} catch (UnsupportedEncodingException e) {
				Log.debug(LOGGER, "Failed to decode query: " + e.getMessage());
			}
			if (query != null) {
				for (Map.Entry<String, Pattern> p : this.queryPatterns.entrySet()) {
					result.put(p.getKey(), p.getValue().matcher(query));
				}
				return result;
			}
		}
		return Collections.emptyMap();
	}
	
	public boolean match(String path, String query) {
		if (query != null) {
			try {
				query = Handler.decode(query);
			} catch (UnsupportedEncodingException e) {
				Log.debug(LOGGER, "Failed to decode query: " + e.getMessage());
				return false;
			}
		}
		if (!pathPattern.matcher(path).matches()) {
			Log.debug(LOGGER, "Path does not match [" + path + "] vs [" + pathPattern.toString() + "]");
			return false;
		}
		boolean queryMatched = (queryPatterns.size() == 0);
		if (queryPatterns.size() != 0 && query != null) {
			for (Pattern p : queryPatterns.values()) {
				Log.debug(LOGGER, "Query [" + query + "] vs [" + p.toString() + "]");
				if (p.matcher(query).find()) {
					queryMatched = true;
					break;
				}
			}
		}
		Log.debug(LOGGER, "Query match: " + queryMatched);
		return queryMatched;
	}

	public Pattern getPathPattern() {
		return pathPattern;
	}

	public Map<String, Pattern> getQueryPatterns() {
		return queryPatterns;
	}
}