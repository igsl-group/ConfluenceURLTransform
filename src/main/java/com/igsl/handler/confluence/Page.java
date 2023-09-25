package com.igsl.handler.confluence;

import java.net.URI;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igsl.Config;
import com.igsl.handler.HandlerResult;

public class Page extends Confluence {
	
	private static final Logger LOGGER = LogManager.getLogger(Page.class);
	private static final String PARAM_PAGEID = "pageId";
	private static final String PARAM_PREVIEW = "preview";
	private static final String PARAM_TITLE = "title";
	private static final String PARAM_SPACEKEY = "spaceKey";
	private static final Pattern PATH_REGEX = Pattern.compile("/pages/[^?#]*");
	private static final Pattern PAGEID_REGEX = Pattern.compile("pageId=([0-9]+)");
	
	public Page(Config config) {
		super(config);
	}

	@Override
	protected boolean _accept(URI uri) {
		if (!super._accept(uri)) {
			return false;
		}
		return PATH_REGEX.matcher(uri.getPath()).matches();
	}

	@Override
	public HandlerResult handle(URI uri, String text) throws Exception {
		String title = null;
		String spaceKey = null;
		String query = StringEscapeUtils.unescapeHtml4(uri.getQuery());
		String pageIdString = null;
		Matcher pageIdMatcher = PAGEID_REGEX.matcher(query);
		if (pageIdMatcher.find()) {
			pageIdString = pageIdMatcher.group(1);
		}
		Map<String, String> queries = parseQuery(query);
		if (pageIdString != null) {
			int pageId = Integer.parseInt(pageIdString);
			try (PreparedStatement ps = config.getConfluenceConnection().prepareStatement(QUERY_PAGE_ID)) {
				ps.setInt(1, pageId);
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						title = rs.getString(1);
						spaceKey = rs.getString(2);
					}
				}
			}
		}
		URIBuilder builder = new URIBuilder();
		builder.setScheme(config.getToScheme());
		builder.setHost(config.getConfluenceToHost());
		String newPath = uri.getPath();
		// Remove original base path
		if (newPath.startsWith(config.getConfluenceFromBasePath())) {
			newPath = newPath.substring(config.getConfluenceFromBasePath().length());
		}
		builder.setPathSegments(addPathSegments(
				config.getConfluenceToBasePath(),
				newPath));
		builder.setFragment(uri.getFragment());
		// Add back query parameters, except pageId and preview
		if (title != null && spaceKey != null) {
			builder.addParameter(PARAM_SPACEKEY, title);
			builder.addParameter(PARAM_TITLE, spaceKey);
		}
		for (Map.Entry<String, String> q : queries.entrySet()) {
			// TODO Keep PARAM_PREVIEW if post migration patching is used
			if (!PARAM_PAGEID.equals(q.getKey()) && !PARAM_PREVIEW.equals(q.getKey())) {
				builder.addParameter(q.getKey(), q.getValue());
			}
		}
		return new HandlerResult(builder.build());
	}

}
