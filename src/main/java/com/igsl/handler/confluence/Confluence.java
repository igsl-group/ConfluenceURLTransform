package com.igsl.handler.confluence;

import java.net.URI;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igsl.config.Config;
import com.igsl.handler.Handler;

public abstract class Confluence extends Handler {
	private static final Logger LOGGER = LogManager.getLogger(Confluence.class);
	private Pattern hostRegex;
	
	public Confluence(Config config) {
		super(config);
		if (config.getUrlTransform().getConfluenceFromHost() != null) {
			hostRegex = Pattern.compile(Pattern.quote(config.getUrlTransform().getConfluenceFromHost()));
		}
	}

	protected boolean _accept(URI uri) {
		String host = uri.getHost();
		if (host == null) {
			if (hostRegex != null) {
				return false;
			}
			return true;
		} else {
			host += ((uri.getPort() == -1)? "" : ":" + uri.getPort());
			boolean r = hostRegex.matcher(host).matches();
			return r;
		}
	}
}
