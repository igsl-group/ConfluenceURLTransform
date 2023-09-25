package com.igsl.handler;

import java.net.URI;

public class HandlerResult {
	private URI uri;
	private String tag;
	private boolean replaceTag;
	public HandlerResult(URI uri) {
		this.uri = uri;
		replaceTag = false;
	}
	public HandlerResult(String tag) {
		this.tag = tag;
		replaceTag = true;
	}
	public URI getUri() {
		return uri;
	}
	public String getTag() {
		return tag;
	}
	public boolean isReplaceTag() {
		return replaceTag;
	}
}
