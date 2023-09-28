package com.igsl.handler.jira;

import java.net.URI;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igsl.config.Config;
import com.igsl.handler.HandlerResult;
import com.igsl.handler.URLPattern;

public class PostMigrate extends Jira {

	private static final Logger LOGGER = LogManager.getLogger(PostMigrate.class);
	
	@Override
	public boolean needPostMigrate() {
		return true;
	}

	private static final URLPattern[] PATTERNS = new URLPattern[] {
		new URLPattern().setPath("/secure/RapidBoard.jspa").setQuery("rapidView"),
		new URLPattern().setPathRegex("/secure/attachment/.+"),
		new URLPattern().setPath("/secure/Dashboard.jspa").setQuery("pageId", "selectPageId"),
		new URLPattern().setPath("/servicedesk/customer/portal/[0-9]+"),
		
		new URLPattern().setPath("/issues").setQuery("filter", "jql"),
		new URLPattern().setPath("/browse").setQuery("filter", "jql"),
		new URLPattern().setPath("/secure/IssueNavigator.jspa"),
		new URLPattern().setPathRegex("/projects/[^#?]*").setQuery("filter", "jql"),
		new URLPattern().setPath("/secure/ReleaseNote.jspa").setQuery("projectId", "version"),
		new URLPattern().setPath("/secure/viewavatar").setQuery("avatarId"),
		new URLPattern().setPath("/secure/BrowseProjects.jspa"),
		new URLPattern().setPath("/secure/CreateIssue.jspa").setQuery("pid", "issuetype"),
		new URLPattern().setPath("/secure/CreateIssue!default.jspa").setQuery("pid", "issuetype"),
		new URLPattern().setPath("/secure/DeleteLink.jspa"),
		new URLPattern().setPath("/secure/EditFilter.jspa"),
		new URLPattern().setPath("/secure/EditFilter!default.jspa"),
		new URLPattern().setPath("/secure/admin/ConfigureFieldLayout!default.jspa"),
		new URLPattern().setPath("/secure/project/EditProject!default.jspa"),
		new URLPattern().setPath("/servicedesk/admin/[^/]+/sla/custom/[0-9]+"),
		new URLPattern().setPath("/secure/CreateIssueDetails!init.jspa").setQuery("pid", "issuetype"),
		new URLPattern().setPath("/secure/DataplaneReport!default.jspa").setQuery("report"),
		new URLPattern().setPathRegex("/servicedesk/customer/portal/[0-9]+/.*"),
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
				LOGGER.debug("Jira PostMigrate accepts: [" + uri + "]");
				return true;
			}
		}
		LOGGER.debug("Jira PostMigrate rejects: [" + uri + "]");
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
