package com.igsl.handler.jira;

import java.net.URI;
import java.util.regex.Pattern;

import org.apache.http.client.utils.URIBuilder;

import com.igsl.Config;
import com.igsl.handler.HandlerResult;
import com.igsl.handler.URLPattern;

public class PostMigrate extends Jira {

	@Override
	public boolean needPostMigrate() {
		return true;
	}

	private static final URLPattern[] PATTERNS = new URLPattern[] {
		new URLPattern().setPath("/secure/RapidBoard.jspa"),
		new URLPattern().setPath("/issues"),
		new URLPattern().setPath("/secure/Dashboard.jspa"),
		new URLPattern().setPath("/secure/attachment/"),
		new URLPattern().setPath("/secure/IssueNavigator.jspa"),
		new URLPattern().setPath("/projects/[^#?]*"),
		new URLPattern().setPath("/secure/PortfolioReportView.jspa"),
		new URLPattern().setPath("/servicedesk/customer/portal/[0-9]+"),
		new URLPattern().setPath("/secure/ReleaseNote.jspa"),
		new URLPattern().setPath("/secure/viewavatar"),
		new URLPattern().setPath("/secure/BrowseProjects.jspa"),
		new URLPattern().setPath("/secure/CreateIssue.jspa"),
		new URLPattern().setPath("/secure/DeleteLink.jspa"),
		new URLPattern().setPath("/secure/EditFilter!default.jspa"),
		new URLPattern().setPath("/secure/admin/ConfigureFieldLayout!default.jspa"),
		new URLPattern().setPath("/secure/project/EditProject!default.jspa"),
		new URLPattern().setPath("/servicedesk/admin/[^/]+/sla/custom/[0-9]+")
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
					if (uri.getQuery() != null && 
						!query.matcher(uri.getQuery()).find()) {
						queryMatched = false;
						break;
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
