package com.igsl.handler.confluence;

import java.net.URI;
import java.util.regex.Pattern;

import org.apache.http.client.utils.URIBuilder;

import com.igsl.config.Config;
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
		new URLPattern().setPath("/pages/viewpageattachments.action").setQuery("pageId"),
		new URLPattern().setPath("/pages/templates2/viewpagetemplate.action").setQuery("entityId"),
		new URLPattern().setPath("/createpage.action").setQuery("fromPageId"),
		new URLPattern().setPath("/users/viewuserprofile.action").setQuery("userName"),
		new URLPattern().setPath("/pages/viewpage.action").setQuery("pageId"),
		new URLPattern().setPath("/secure/ViewProfile.jspa").setQuery("name"),
		new URLPattern().setPath("/pages/editcomment.action"),
		new URLPattern().setPath("/pages/removecomment.action"),
		new URLPattern().setPath("/pages/replycomment.action"),
		new URLPattern().setPath("/admin/users/domembersofgroupsearch.action"),
		new URLPattern().setPath("/admin/users/edituser.action").setQuery("username"),
		new URLPattern().setPath("/calendar/calendarPage.action").setQuery("calendarId")
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
