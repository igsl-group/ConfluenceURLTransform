package com.igsl.handler.postmigrate.confluence;

import java.net.URI;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;

import com.igsl.config.Config;
import com.igsl.handler.HandlerResult;
import com.igsl.handler.URLPattern;
import com.igsl.handler.confluence.Confluence;

public class PostMigrate extends Confluence {

	@Override
	public boolean needPostMigrate() {
		return true;
	}

	private static final URLPattern[] PATTERNS = new URLPattern[] {
		new URLPattern().setPath("/pages/templates2/viewpagetemplate.action").setQuery("entityId"),
		new URLPattern().setPath("/createpage.action").setQuery("fromPageId"),
		new URLPattern().setPath("/calendar/calendarPage.action").setQuery("calendarId"),
		new URLPattern("/display/~[^?]+"),
		new URLPattern("/display/[^?]+").setQuery("preview"),
		new URLPattern().setPath("/users/viewuserprofile.action").setQuery("userName"),
		new URLPattern().setPath("/secure/ViewProfile.jspa").setQuery("name"),
		new URLPattern().setPath("/admin/users/edituser.action").setQuery("username"),
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
				return true;
			}
		}
		return false;
	}

	@Override
	public HandlerResult handle(URI uri, String text) throws Exception {
		URIBuilder parser = new URIBuilder(uri);
		List<NameValuePair> params = parser.getQueryParams();
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
		builder.setParameters(params);
		builder.setFragment(uri.getFragment());
		return new HandlerResult(builder.build());
	}
	
}
