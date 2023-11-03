package com.igsl.handler.postmigrate.jira;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igsl.Log;
import com.igsl.config.Config;
import com.igsl.export.cloud.CloudJiraFieldConfigurations;
import com.igsl.export.cloud.CloudJiraSLAs;
import com.igsl.handler.URLPattern;
import com.igsl.handler.postmigrate.BasePostMigrate;
import com.igsl.handler.postmigrate.MappingSetting;
import com.igsl.handler.postmigrate.PathSetting;

public class SLA extends BasePostMigrate {

	private static final Logger LOGGER = LogManager.getLogger(SLA.class);
	
	@Override
	protected URLPattern[] getPatterns() {
		return new URLPattern[] {
			new URLPattern()
				.setPathRegex(
					Pattern.quote(config.getUrlTransform().getJiraToBasePath()) + 
					"/servicedesk/admin/[^/]+/sla/custom/[0-9]+"),
		};
	}
	
	public SLA(Config config) {
		super(	config, 
				config.getUrlTransform().getJiraToHost(), 
				Arrays.asList(
					new MappingSetting(
						new CloudJiraSLAs(), 
						CloudJiraSLAs.COL_DCID, 
						CloudJiraSLAs.COL_CLOUDID)
				),
				Arrays.asList(
					new PathSetting(
							SLA.class,
							Pattern.compile(
									Pattern.quote(config.getUrlTransform().getJiraToBasePath()) + 
									"/servicedesk/admin/([^/]+)/sla/custom/([0-9]+)"),
							CloudJiraSLAs.class) {
							@Override
							public String getReplacement(Matcher m, Map<String, Map<String, String>> mappings) 
									throws Exception {
								String slaId = m.group(2);
								Map<String, String> mapping = mappings.get(getBaseExport().getCanonicalName());
								if (mapping.containsKey(slaId)) {
									return config.getUrlTransform().getJiraToBasePath() + 
											"/servicedesk/projects/$1/settings/sla/custom/" + mapping.get(slaId);
								} else {
									throw new Exception(getPostMigrate().getCanonicalName() + 
											" Mapping not found for slaId: " + slaId);
								}
							}
					}
				),
				null);
	}
}
