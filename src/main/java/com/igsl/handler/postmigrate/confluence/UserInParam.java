package com.igsl.handler.postmigrate.confluence;

import java.net.URI;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igsl.config.Config;
import com.igsl.export.cloud.CloudConfluenceUsers;
import com.igsl.handler.URLPattern;
import com.igsl.handler.postmigrate.BasePostMigrate;
import com.igsl.handler.postmigrate.MappingSetting;
import com.igsl.handler.postmigrate.ParamSetting;

public class UserInParam extends BasePostMigrate {

	private static final Logger LOGGER = LogManager.getLogger(UserInParam.class);
	private static final String PARAM_NAME = "name";
	private static final String PARAM_USER_NAME = "userName";
	private static final String PARAM_USERNAME = "username";
	
	@Override
	public boolean needPostMigrate() {
		return true;
	}

	private static final URLPattern[] PATTERNS = new URLPattern[] {
		new URLPattern().setPath("/users/viewuserprofile.action").setQuery("userName"),
		new URLPattern().setPath("/secure/ViewProfile.jspa").setQuery("name"),
		new URLPattern().setPath("/admin/users/edituser.action").setQuery("username"),
	};
	
	public UserInParam(Config config) {
		super(	config, 
				config.getUrlTransform().getConfluenceFromHost(), 
				Arrays.asList(
					new MappingSetting(
						new CloudConfluenceUsers(), 
						CloudConfluenceUsers.COL_DCID, 
						CloudConfluenceUsers.COL_CLOUDID)
				),
				null,
				Arrays.asList(
					new ParamSetting(PARAM_NAME, CloudConfluenceUsers.class),
					new ParamSetting(PARAM_USERNAME, CloudConfluenceUsers.class),
					new ParamSetting(PARAM_USER_NAME, CloudConfluenceUsers.class)
				));
	}

	@Override
	protected boolean _accept(URI uri) {
		if (!super._accept(uri)) {
			return false;
		}
		for (URLPattern path : PATTERNS) {
			if (path.match(uri)) {
				return true;
			}
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
//		builder.setPathSegments(addPathSegments(
//				config.getUrlTransform().getConfluenceToBasePath(),
//				originalPath));
//		// Restore parameters
//		for (NameValuePair param : params) {
//			if (PARAM_NAMES.contains(param.getName())) {
//				if (mappings.get(PARAM_DUMMY).containsKey(param.getValue())) {
//					builder.addParameter(param.getName(), 
//							mappings.get(PARAM_DUMMY).get(param.getValue()));
//				} else {
//					Log.warn(LOGGER, "Mapping not found for userName: " + param.getValue());
//					builder.addParameter(param.getName(), param.getValue());
//				}
//			} else {
//				builder.addParameter(param.getName(), param.getValue());
//			}
//		}
//		// Restore fragments
//		builder.setFragment(uri.getFragment());
//		return new HandlerResult(builder.build());
//	}
}
