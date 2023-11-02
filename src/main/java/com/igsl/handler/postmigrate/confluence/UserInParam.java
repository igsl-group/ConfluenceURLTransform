package com.igsl.handler.postmigrate.confluence;

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

	@Override
	protected URLPattern[] getPatterns() {
		return new URLPattern[] {
			new URLPattern().setPath("/users/viewuserprofile.action").setQuery("userName"),
			new URLPattern().setPath("/secure/ViewProfile.jspa").setQuery("name"),
			new URLPattern().setPath("/admin/users/edituser.action").setQuery("username"),
		};
	}
	
	public UserInParam(Config config) {
		super(	config, 
				config.getUrlTransform().getConfluenceToHost(), 
				config.getUrlTransform().getConfluenceToBasePath(),
				Arrays.asList(
					new MappingSetting(
						new CloudConfluenceUsers(), 
						CloudConfluenceUsers.COL_DCID, 
						CloudConfluenceUsers.COL_CLOUDID)
				),
				null,
				Arrays.asList(
					new ParamSetting(UserInParam.class, PARAM_NAME, CloudConfluenceUsers.class),
					new ParamSetting(UserInParam.class, PARAM_USERNAME, CloudConfluenceUsers.class),
					new ParamSetting(UserInParam.class, PARAM_USER_NAME, CloudConfluenceUsers.class)
				));
	}
}
