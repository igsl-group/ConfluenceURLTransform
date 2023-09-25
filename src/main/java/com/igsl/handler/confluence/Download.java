package com.igsl.handler.confluence;

import java.net.URI;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igsl.Config;
import com.igsl.handler.HandlerResult;

public class Download extends Confluence {

	private static final Logger LOGGER = LogManager.getLogger(Download.class);
	// /download/attachments/<id> or /download/thumbnails/<id>
	private static final Pattern PATH_REGEX = Pattern.compile("/download/(?:attachments|thumbnails)/([0-9]+)/(.+)");
	// Optional version parameter
	private static final Pattern VERSION_REGEX = Pattern.compile("version=([0-9]+)");

	public Download(Config config) {
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
		LOGGER.debug("Path: " + uri.getPath());
		LOGGER.debug("Query: " + uri.getQuery());
		Matcher pathMatcher = PATH_REGEX.matcher(uri.getPath());
		Matcher versionMatcher = VERSION_REGEX.matcher(uri.getQuery());
		if (pathMatcher.matches() && versionMatcher.find()) {
			String pageId = pathMatcher.group(1);
			String fileName = pathMatcher.group(2);
			String version = versionMatcher.group(1);
			// Find page title
			try (PreparedStatement ps = config.getConfluenceConnection().prepareStatement(QUERY_PAGE_ID)) {
				ps.setString(1, pageId);
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						String pageTitle = rs.getString(1);
						/*
						 * Attachment tag should look like: 
						 * 	<ac:link>
						 * 		<ri:attachment ri:filename="Banner.png">
						 * 			<ri:page ri:content-title="Test Page"/>
						 *		</ri:attachment>
						 *		<ac:plain-text-link-body><![CDATA[File Link]]></ac:plain-text-link-body>
						 *	</ac:link>
						 */
						StringBuilder sb = new StringBuilder();
						sb.append("<ac:link>");
						sb.append("<ri:attachment ri:filename=\"").append(fileName).append("\">");
						sb.append("<ri:page ri:content-title=\"").append(pageTitle).append("\"/>");
						sb.append("</ri:attachment>");
						sb.append("<ac:plain-text-link-body><![CDATA[").append(text).append("]]></ac:plain-text-link-body>");
						sb.append("</ac:link>");
						return new HandlerResult(sb.toString());
					}
				}
			}
		}
		return new HandlerResult(uri);
	}

}
