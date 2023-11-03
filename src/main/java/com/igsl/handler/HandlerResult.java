package com.igsl.handler;

import java.net.URI;

public class HandlerResult {
	private HandlerResultType resultType;	
	private URI uri;
	private String tag;
	private String errorMessage;
	public HandlerResult(URI uri, HandlerResultType resultType, String errorMessage) {
		this.uri = uri;
		this.errorMessage = errorMessage;
		this.resultType = resultType;
	}
	public HandlerResult(URI uri) {
		this.uri = uri;
		this.resultType = HandlerResultType.URI;
	}
	public HandlerResult(String tag) {
		this.tag = tag;
		this.resultType = HandlerResultType.TAG;
	}
	public URI getUri() {
		return uri;
	}
	public String getTag() {
		return tag;
	}
	public HandlerResultType getResultType() {
		return resultType;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
}
