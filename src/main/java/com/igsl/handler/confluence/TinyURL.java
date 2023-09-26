package com.igsl.handler.confluence;

import java.net.URI;
import java.net.URLDecoder;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igsl.Config;
import com.igsl.TinyURLGenerator;
import com.igsl.handler.HandlerResult;

public class TinyURL extends Confluence {
	private static final Logger LOGGER = LogManager.getLogger(TinyURL.class);
	// /x/<Code>
	private static final Pattern PATH_REGEX = Pattern.compile("^/x/(.+)$");
	private static final String NEW_PATH = "/display/";
	
	public TinyURL(Config config) {
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
		Matcher m = PATH_REGEX.matcher(uri.getPath());
		if (m.matches()) {
			String tinyURL = m.group(1);
			tinyURL = URLDecoder.decode(tinyURL, ENCODING).trim();
			int pageId = TinyURLGenerator.unpack(tinyURL);
			// Resolve page ID into space key and page title
			try (PreparedStatement ps = config.getConfluenceConnection().prepareStatement(QUERY_PAGE_ID)) {
				ps.setInt(1, pageId);
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						String title = rs.getString(1);
						String spaceKey = rs.getString(2);
						URIBuilder builder = new URIBuilder();
						builder.setScheme(config.getToScheme());
						builder.setHost(config.getConfluenceToHost());
						builder.setPathSegments(addPathSegments(
								config.getConfluenceToBasePath(),
								NEW_PATH,
								encode(spaceKey),
								encode(title)));
						builder.setFragment(uri.getFragment());
						return new HandlerResult(builder.build());
					}
				}
			} catch (Exception ex) {
				return new HandlerResult(uri, ex.getMessage());
			}
		}
		return new HandlerResult(uri);
	}

}
