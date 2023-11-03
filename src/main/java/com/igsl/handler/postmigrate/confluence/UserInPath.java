package com.igsl.handler.postmigrate.confluence;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
			new URLPattern()
				.setPathRegex(
						Pattern.quote(config.getUrlTransform().getConfluenceToBasePath()) + 
						"/display/~([^?]+)")
		};
	}
	
	public UserInPath(Config config) {
		super(	config, 
				config.getUrlTransform().getConfluenceToHost(), 
				Arrays.asList(
					new MappingSetting(
						new CloudConfluenceUsers(), 
						CloudConfluenceUsers.COL_DCID, 
						CloudConfluenceUsers.COL_CLOUDID)
				),
				Arrays.asList(
					new PathSetting(
							UserInPath.class,
							Pattern.compile(
									Pattern.quote(config.getUrlTransform().getConfluenceToBasePath()) + 
									"/display/~([^?]+)"),
							CloudConfluenceUsers.class
							) {
							@Override
							public String getReplacement(Matcher m, Map<String, Map<String, String>> mappings) 
									throws Exception {
								Map<String, String> mapping = mappings.get(this.getBaseExport().getCanonicalName());
								String userName = m.group(1);
								if (mapping.containsKey(userName)) {
									return config.getUrlTransform().getConfluenceToBasePath() + "/display/" + 
											mapping.get(userName);
								} else {
									throw new Exception(getPostMigrate().getCanonicalName() + 
											" Mapping not found for userName: " + userName);
								}
							}
					}
				),
				null);
	}
}
