package com.igsl.handler;

import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igsl.CSV;
import com.igsl.config.Config;
import com.igsl.export.cloud.BaseExport;
import com.igsl.export.cloud.CloudConfluencePageTemplates;

public abstract class Handler {

	private static final Logger LOGGER = LogManager.getLogger(Handler.class);

	protected static final String QUERY_PAGE_ID = 
			"SELECT c.TITLE, s.SPACEKEY FROM CONTENT c JOIN SPACES s ON s.SPACEID = c.SPACEID WHERE c.CONTENTID = ?";
	protected static final String QUERY_ATTACHMENT_ID = 
			"SELECT a.TITLE AS AttachmentTitle, p.TITLE AS PageTitle, s.SPACEKEY " + 
			"FROM CONTENT a " + 
			"JOIN CONTENT p ON p.CONTENTID = a.PAGEID " + 
			"JOIN SPACES s ON s.SPACEID = a.SPACEID " + 
			"WHERE a.CONTENTTYPE = 'ATTACHMENT' AND a.CONTENTID = ?";
	
	protected static final String PATH_DELIM = "/";
	public static final String ENCODING = "ASCII";

	protected Config config;
	private Pattern schemeRegex;
	
	/**
	 * Constructor
	 * @param config Config
	 */
	public Handler(Config config) {
		this.config = config;
		if (config.getUrlTransform().getFromSchemeRegex() != null) {
			this.schemeRegex = Pattern.compile(config.getUrlTransform().getFromSchemeRegex());
		}
	}

	/**
	 * URL decode string
	 * @param s String
	 * @return String
	 * @throws UnsupportedEncodingException
	 */
	public static String decode(String s) throws UnsupportedEncodingException {
		return URLDecoder.decode(s, ENCODING);
	}
	
	/**
	 * URL encode string
	 * @param s String
	 * @return String
	 * @throws UnsupportedEncodingException
	 */
	public static String encode(String s) throws UnsupportedEncodingException {
		return URLEncoder.encode(s, ENCODING);
	}
	
	/**
	 * Create list for URIBuilder.setPathSegments()
	 * If PATH_DELIM is found, it will be removed from start and end of each segment
	 * 
	 * @param segments
	 * @return
	 */
	protected static List<String> addPathSegments(String... segments) {
		List<String> result = new ArrayList<>();
		for (String segment : segments) {
			if (segment != null && !segment.isBlank()) {
				if (segment.startsWith(PATH_DELIM)) {
					segment = segment.substring(PATH_DELIM.length());
				}
				if (segment.endsWith(PATH_DELIM)) {
					segment = segment.substring(0, segment.length() - 1);
				}
				for (String s : segment.split(Pattern.quote(PATH_DELIM))) {
					result.add(s);
				}
			}
		}
		return result;
	}

	/**
	 * Check if this handler will handle the provided URI.
	 * @param uri
	 * @return
	 */
	protected abstract boolean _accept(URI uri);

	/**
	 * Check if this handler will handle the provided URI.
	 * Invokes _accept() of implementations. 
	 * 
	 * @param uri URI
	 * @return boolean
	 */
	public final boolean accept(URI uri) {
		if (uri == null) {
			return false;
		}
		if (schemeRegex != null) {
			String scheme = uri.getScheme();
			if (scheme == null) {
				scheme = config.getUrlTransform().getDefaultScheme();
			}
			if (!schemeRegex.matcher(scheme).matches()) {
				return false;
			}
		}
		if (!_accept(uri)) {
			return false;
		}
		return true;
	}
	
	/**
	 * If this handler is for URLs that require post-migration data patching.
	 * If true, then space key and title of the page will be logged.
	 * @return
	 */
	public boolean needPostMigrate() {
		return false;
	}

	/**
	 * Converts provided uri into new format.
	 * 
	 * HandlerResult takes either a String or URI. 
	 * If URI is used, then the hyperlink target is replaced. 
	 * If String is used, then the whole hyperlink is replaced.
	 * 
	 * @param uri URI, link target
	 * @param text String, display text
	 * @return HandlerResult 
	 * @throws Exception
	 */
	public abstract HandlerResult handle(URI uri, String text) throws Exception;
}
