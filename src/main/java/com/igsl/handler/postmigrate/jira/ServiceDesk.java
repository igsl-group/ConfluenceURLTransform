package com.igsl.handler.postmigrate.jira;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igsl.Log;
import com.igsl.config.Config;
import com.igsl.export.cloud.CloudJiraServiceDesks;
import com.igsl.handler.URLPattern;
import com.igsl.handler.postmigrate.BasePostMigrate;
import com.igsl.handler.postmigrate.MappingSetting;
import com.igsl.handler.postmigrate.PathSetting;

public class ServiceDesk extends BasePostMigrate {

	private static final Logger LOGGER = LogManager.getLogger(ServiceDesk.class);
	private static final String BOARD_ID = "rapidView";
	
	@Override
	protected URLPattern[] getPatterns() {
		return new URLPattern[] {
			new URLPattern()
				.setPathRegex(
						Pattern.quote(config.getUrlTransform().getJiraToBasePath()) + 
						"/servicedesk/customer/portal/[0-9]+.*"),
		};
	}
	
	public ServiceDesk(Config config) {
		super(	config, 
				config.getUrlTransform().getJiraToHost(), 
				Arrays.asList(
					new MappingSetting(
						new CloudJiraServiceDesks(), 
						CloudJiraServiceDesks.COL_DCID, 
						CloudJiraServiceDesks.COL_CLOUDID)
				),
				Arrays.asList(
					new PathSetting(
							ServiceDesk.class,
							Pattern.compile(
									Pattern.quote(config.getUrlTransform().getJiraToBasePath()) + 
									"/servicedesk/customer/portal/([0-9]+)(.*)"),
							CloudJiraServiceDesks.class) {						
						@Override
						public String getReplacement(Matcher m, Map<String, Map<String, String>> mappings) throws Exception {
							Map<String, String> mapping = mappings.get(getBaseExport().getCanonicalName());
							String portalId = m.group(1);
							if (mapping.containsKey(portalId)) {
								StringBuilder sb = new StringBuilder();
								// Note: service desk URL does not start with /jira
								return "/servicedesk/customer/portal/" + mapping.get(portalId) + "$2";
							} else {
								throw new Exception(getPostMigrate().getCanonicalName() + 
										" Mapping not found for portalId: " + portalId);
							}
						}
					}
				),
				null);
	}
}
