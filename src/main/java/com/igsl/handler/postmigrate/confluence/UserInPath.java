package com.igsl.handler.postmigrate.confluence;

import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igsl.Log;
import com.igsl.config.Config;
import com.igsl.export.cloud.CloudConfluenceUsers;
import com.igsl.handler.URLPattern;
import com.igsl.handler.postmigrate.BasePostMigrate;
import com.igsl.handler.postmigrate.MappingSetting;
import com.igsl.handler.postmigrate.PathSetting;

public class UserInPath extends BasePostMigrate {

	private static final Logger LOGGER = LogManager.getLogger(UserInPath.class);
	
	@Override
	public boolean needPostMigrate() {
		return true;
	}

	private static final URLPattern PATTERN = new URLPattern("/display/~([^?]+)");
	
	public UserInPath(Config config) {
		super(	config, 
				config.getUrlTransform().getConfluenceFromHost(), 
				Arrays.asList(
					new MappingSetting(
						new CloudConfluenceUsers(), 
						CloudConfluenceUsers.COL_DCID, 
						CloudConfluenceUsers.COL_CLOUDID)
				),
				Arrays.asList(
					new PathSetting(
							Pattern.compile("/display/~([^?]+)"),
							CloudConfluenceUsers.class
							) {
							@Override
							public String getReplacement(Matcher m, Map<String, Map<String, String>> mappings) {
								Map<String, String> mapping = mappings.get(this.getBaseExport().getCanonicalName());
								String userName = m.group(1);
								if (mapping.containsKey(userName)) {
									return "/display/" + mapping.get(userName);
								} else {
									Log.warn(LOGGER, "Mapping not found for username: " + userName);
									return m.group(0);
								}
							}
					}
				),
				null);
	}

	@Override
	protected boolean _accept(URI uri) {
		if (!super._accept(uri)) {
			return false;
		}
		if (PATTERN.match(uri)) {
			return true;
		}
		return false;
	}	
	
//	@Override
//	public HandlerResult handle(URI uri, String text) throws Exception {
//		// Read mapping
//		loadMappings();
//		// Reconstruct URL
//		URIBuilder parser = new URIBuilder(uri);
//		List<NameValuePair> params = parser.getQueryParams();
//		URIBuilder builder = new URIBuilder();
//		builder.setScheme(config.getUrlTransform().getToScheme());
//		builder.setHost(config.getUrlTransform().getConfluenceToHost());
//		String originalPath = uri.getPath();
//		// Strip base path out of originalPath
//		if (originalPath.startsWith(config.getUrlTransform().getConfluenceFromBasePath())) {
//			originalPath = originalPath.substring(config.getUrlTransform().getConfluenceFromBasePath().length());
//		} 
//		// Replace username in path with account ID
//		Matcher m = PATTERN.getPathPattern().matcher(originalPath);
//		String userName = m.group(1);
//		StringBuilder sb = new StringBuilder();
//		if (mappings.get(PARAM_DUMMY).containsKey(userName)) {
//			String accountId = mappings.get(PARAM_DUMMY).get(userName);
//			m.appendReplacement(sb, "/display/~" + accountId);			
//		} else {
//			Log.warn(LOGGER, "No mapping found for username: " + userName);
//			m.appendReplacement(sb, "/display/~" + userName);
//		}		
//		m.appendTail(sb);		
//		builder.setPathSegments(addPathSegments(
//				config.getUrlTransform().getConfluenceToBasePath(),
//				sb.toString()));
//		// Restore parameters
//		builder.setParameters(params);
//		// Restore fragments
//		builder.setFragment(uri.getFragment());
//		return new HandlerResult(builder.build());
//	}
}
