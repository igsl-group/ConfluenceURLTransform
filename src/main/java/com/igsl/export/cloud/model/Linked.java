package com.igsl.export.cloud.model;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.igsl.Log;

/**
 * REST API that uses a link to point to next batch of results
 * This is used by Confluence REST APIs
 */
public abstract class Linked {
	private static final Logger LOGGER = LogManager.getLogger(Linked.class);
	protected ConfluenceLink _links;	
	@JsonIgnore
	public Map<String, Object> getNext() {
		if (_links != null && _links.getNext() != null && !_links.getNext().isBlank()) {
			try {
				Map<String, Object> result = new HashMap<>();
				URIBuilder builder = new URIBuilder(_links.getNext());
				for (NameValuePair pair : builder.getQueryParams()) {
					result.put(pair.getName(), pair.getValue());
				}
				return result;
			} catch (URISyntaxException ex) {
				Log.error(LOGGER, "Unable to parse _links \"" + _links + "\"", ex);
			}
		}
		return null;
	}
	public ConfluenceLink get_links() {
		return _links;
	}
	public void set_links(ConfluenceLink _links) {
		this._links = _links;
	}
}
