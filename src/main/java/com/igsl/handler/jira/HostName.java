package com.igsl.handler.jira;

import java.net.URI;

import org.apache.http.client.utils.URIBuilder;

import com.igsl.Config;
import com.igsl.handler.HandlerResult;

public class HostName extends Jira {

	public HostName(Config config) {
		super(config);
	}

	@Override
	protected boolean _accept(URI uri) {
		return super._accept(uri);
	}

	@Override
	public HandlerResult handle(URI uri, String text) throws Exception {
		URIBuilder builder = new URIBuilder();
		builder.setScheme(config.getToScheme());
		builder.setHost(config.getJiraToHost());
		String originalPath = uri.getPath();
		builder.setPathSegments(addPathSegments(
				config.getJiraToBasePath(),
				(originalPath.startsWith(config.getJiraFromBasePath())? 
						originalPath.substring(config.getJiraFromBasePath().length()) : 
						originalPath)
				));
		builder.setCustomQuery(uri.getQuery());
		builder.setFragment(uri.getFragment());
		return new HandlerResult(builder.build());
	}
	
}
