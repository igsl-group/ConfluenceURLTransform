package com.igsl.handler.confluence;

import java.net.URI;

import org.apache.http.client.utils.URIBuilder;

import com.igsl.Config;
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
		builder.setScheme(config.getToScheme());
		builder.setHost(config.getConfluenceToHost());
		String originalPath = uri.getPath();
		builder.setPathSegments(addPathSegments(
				config.getConfluenceToBasePath(),
				(originalPath.startsWith(config.getConfluenceFromBasePath())? 
						originalPath.substring(config.getConfluenceFromBasePath().length()) : 
						originalPath)
				));
		builder.setCustomQuery(uri.getQuery());
		builder.setFragment(uri.getFragment());
		return new HandlerResult(builder.build());
	}
	
}
