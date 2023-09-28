package com.igsl.handler.jira;

import java.net.URI;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;

import com.igsl.config.Config;
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
		URIBuilder parser = new URIBuilder(uri);
		List<NameValuePair> params = parser.getQueryParams();
		URIBuilder builder = new URIBuilder();
		builder.setScheme(config.getUrlTransform().getToScheme());
		builder.setHost(config.getUrlTransform().getJiraToHost());
		String originalPath = uri.getPath();
		builder.setPathSegments(addPathSegments(
				config.getUrlTransform().getJiraToBasePath(),
				(originalPath.startsWith(config.getUrlTransform().getJiraFromBasePath())? 
						originalPath.substring(config.getUrlTransform().getJiraFromBasePath().length()) : 
						originalPath)
				));
		builder.setParameters(params);
		builder.setFragment(uri.getFragment());
		return new HandlerResult(builder.build());
	}
	
}
