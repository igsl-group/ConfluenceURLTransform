package com.igsl.handler.postmigrate.confluence;

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

	@Override
	protected URLPattern[] getPatterns() {
		return new URLPattern[] {
			new URLPattern("/display/~([^?]+)"),
		};
	}
	
	public UserInPath(Config config) {
		super(	config, 
				config.getUrlTransform().getConfluenceToHost(), 
				config.getUrlTransform().getConfluenceToBasePath(),
				Arrays.asList(
					new MappingSetting(
						new CloudConfluenceUsers(), 
						CloudConfluenceUsers.COL_DCID, 
						CloudConfluenceUsers.COL_CLOUDID)
				),
				Arrays.asList(
					new PathSetting(
							UserInPath.class,
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
									Log.warn(LOGGER, 
											getPostMigrate().getCanonicalName() + 
											" Mapping not found for username: " + userName);
									return m.group(0);
								}
							}
					}
				),
				null);
	}
}
