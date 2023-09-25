package com.igsl.handler.jira;

import java.net.URI;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igsl.Config;
import com.igsl.handler.Handler;

public abstract class Jira extends Handler {
	private static final Logger LOGGER = LogManager.getLogger(Jira.class);
	private Pattern hostRegex;
	
	public Jira(Config config) {
		super(config);
		if (config.getJiraFromHost() != null) {
			hostRegex = Pattern.compile(Pattern.quote(config.getJiraFromHost()));
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
