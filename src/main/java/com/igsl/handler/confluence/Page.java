package com.igsl.handler.confluence;

import java.net.URI;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igsl.config.Config;
import com.igsl.handler.HandlerResult;

public class Page extends Confluence {
	
	private static final Logger LOGGER = LogManager.getLogger(Page.class);
	private static final Pattern PATH_REGEX = Pattern.compile("/pages/[^?#]*");
	private static final Pattern PAGEID_REGEX = Pattern.compile("pageId=([0-9]+)");
	private static final String PARAM_PAGE_VERSION = "pageVersion";
	
	public Page(Config config) {
		super(config);
	}

	@Override
	public boolean needPostMigrate() {
		// Link to page with version format has changed in Cloud, only pageId is supported.
		// So we will transform the URL to the new format, and use post migrate to handle pageId.
		return true;
	}
	
	@Override
	protected boolean _accept(URI uri) {
		if (!super._accept(uri)) {
			return false;
		}
		String query = uri.getQuery();
		if (query == null || query.isBlank()) {
			return false;
		}
		return PATH_REGEX.matcher(uri.getPath()).matches() && PAGEID_REGEX.matcher(query).find();
	}

	@Override
	public HandlerResult handle(URI uri, String text) throws Exception {
		// Get pageVersion from pageId
		String version = null;
		String query = uri.getQuery();
		String pageIdString = null;
		Matcher pageIdMatcher = PAGEID_REGEX.matcher(query);
		if (pageIdMatcher.find()) {
			pageIdString = pageIdMatcher.group(1);
		}
		if (pageIdString != null) {
			int pageId = Integer.parseInt(pageIdString);
			try (PreparedStatement ps = config.getConnections().getConfluenceConnection().prepareStatement(QUERY_PAGE_ID)) {
				ps.setInt(1, pageId);
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						version = rs.getString(3);
					}
				}
			}
		}
		URIBuilder parser = new URIBuilder(uri);
		List<NameValuePair> params = parser.getQueryParams();
		URIBuilder builder = new URIBuilder();
		builder.setScheme(config.getUrlTransform().getToScheme());
		builder.setHost(config.getUrlTransform().getConfluenceToHost());
		// Convert to spaceKey + title format
		String newPath = uri.getPath();
		// Remove original base path
		if (newPath.startsWith(config.getUrlTransform().getConfluenceFromBasePath())) {
			newPath = newPath.substring(config.getUrlTransform().getConfluenceFromBasePath().length());
		}
		builder.setPathSegments(addPathSegments(
				config.getUrlTransform().getConfluenceToBasePath(),
				newPath));
		builder.setFragment(uri.getFragment());
		for (NameValuePair q : params) {
			builder.addParameter(q.getName(), q.getValue());
		}
		if (version != null) {
			builder.addParameter(PARAM_PAGE_VERSION, version);
		}
		return new HandlerResult(builder.build());
	}

}
