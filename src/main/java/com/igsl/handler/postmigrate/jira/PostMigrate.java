package com.igsl.handler.postmigrate.jira;

import java.net.URI;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igsl.Log;
import com.igsl.config.Config;
import com.igsl.handler.HandlerResult;
import com.igsl.handler.URLPattern;
import com.igsl.handler.jira.Jira;

public class PostMigrate extends Jira {

	private static final Logger LOGGER = LogManager.getLogger(PostMigrate.class);
	
	@Override
	public boolean needPostMigrate() {
		return true;
	}

	private static final URLPattern[] PATTERNS = new URLPattern[] {
		new URLPattern().setPath("/secure/DeleteLink.jspa").setQuery("id", "linkType"),
	};
	
	public PostMigrate(Config config) {
		super(config);
	}

	@Override
	protected boolean _accept(URI uri) {
		if (!super._accept(uri)) {
			return false;
		}
		for (URLPattern path : PATTERNS) {
			if (path.match(uri)) {
				Log.debug(LOGGER, "Jira PostMigrate accepts: [" + uri + "]");
				return true;
			}
		}
		Log.debug(LOGGER, "Jira PostMigrate rejects: [" + uri + "]");
		return false;
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
