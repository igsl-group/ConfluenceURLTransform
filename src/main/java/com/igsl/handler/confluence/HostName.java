package com.igsl.handler.confluence;

import java.net.URI;

import org.apache.http.client.utils.URIBuilder;

import com.igsl.config.Config;
import com.igsl.handler.HandlerResult;

public class HostName extends Confluence {

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
		builder.setScheme(config.getUrlTransform().getToScheme());
		builder.setHost(config.getUrlTransform().getConfluenceToHost());
		String originalPath = uri.getPath();
		builder.setPathSegments(addPathSegments(
				config.getUrlTransform().getConfluenceToBasePath(),
				(originalPath.startsWith(config.getUrlTransform().getConfluenceFromBasePath())? 
						originalPath.substring(config.getUrlTransform().getConfluenceFromBasePath().length()) : 
						originalPath)
				));
		builder.setCustomQuery(uri.getQuery());
		builder.setFragment(uri.getFragment());
		return new HandlerResult(builder.build());
	}
	
}
