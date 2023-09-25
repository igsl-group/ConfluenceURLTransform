package com.igsl.handler.confluence;

import java.net.URI;
import java.util.regex.Pattern;

import org.apache.http.client.utils.URIBuilder;

import com.igsl.Config;
import com.igsl.handler.HandlerResult;
import com.igsl.handler.URLPattern;

public class PostMigrate extends Confluence {

	@Override
	public boolean needPostMigrate() {
		return true;
	}

	private static final URLPattern[] PATTERNS = new URLPattern[] {
		new URLPattern("/display/~[^?]+"),
		new URLPattern("/display/[^?]+").setQuery("preview"),
		new URLPattern().setPath("/pages/viewpageattachments.action").setQuery("pageId").setOptionalQuery("preview"),
		new URLPattern().setPath("/createpage.action"),
		new URLPattern().setPath("/users/viewuserprofile.action").setQuery("userName"),
		new URLPattern().setPath("/pages/viewpage.action").setQuery("pageId").setOptionalQuery("preview"),
		new URLPattern().setPath("/secure/ViewProfile.jspa").setQuery("name"),
		new URLPattern().setPath("/pages/editcomment.action"),
		new URLPattern().setPath("/pages/removecomment.action"),
		new URLPattern().setPath("/pages/replycomment.action"),
		new URLPattern().setPath("/admin/users/domembersofgroupsearch.action"),
		new URLPattern().setPath("/admin/users/edituser.action").setQuery("username"),
		new URLPattern().setPath("/calendar/calendarPage.action").setQuery("spaceKey", "calendarId")
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
			if (path.getPathPattern().matcher(uri.getPath()).matches()) {
				boolean queryMatched = true;
				for (Pattern query : path.getQueryPatterns()) {
					if (uri.getQuery() == null) {
						 queryMatched = false;
						 break;
					} else {
						if (!query.matcher(uri.getQuery()).find()) {
							queryMatched = false;
							break;
						} 
					}
				}
				return queryMatched;
			}
		}
		return false;
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
