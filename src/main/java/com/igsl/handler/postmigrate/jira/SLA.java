package com.igsl.handler.postmigrate.jira;

import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igsl.config.Config;
import com.igsl.export.cloud.CloudJiraFieldConfigurations;
import com.igsl.export.cloud.CloudJiraSLAs;
import com.igsl.handler.URLPattern;
import com.igsl.handler.postmigrate.BasePostMigrate;
import com.igsl.handler.postmigrate.MappingSetting;
import com.igsl.handler.postmigrate.PathSetting;

public class SLA extends BasePostMigrate {

	private static final Logger LOGGER = LogManager.getLogger(SLA.class);
	
	private static final URLPattern[] PATTERNS = new URLPattern[] {
			new URLPattern().setPath("/servicedesk/admin/[^/]+/sla/custom/[0-9]+"),
	};
	
	public SLA(Config config) {
		super(	config, 
				config.getUrlTransform().getConfluenceFromHost(), 
				Arrays.asList(
					new MappingSetting(
						new CloudJiraSLAs(), 
						CloudJiraSLAs.COL_DCID, 
						CloudJiraSLAs.COL_CLOUDID)
				),
				Arrays.asList(
					new PathSetting(
							Pattern.compile("/servicedesk/admin/([^/]+)/sla/custom/([0-9]+)"),
							CloudJiraFieldConfigurations.class) {
							@Override
							public String getReplacement(Matcher m, Map<String, Map<String, String>> mappings) {
								String slaId = m.group(2);
								Map<String, String> mapping = mappings.get(getBaseExport().getCanonicalName());
								if (mapping.containsKey(slaId)) {
									return "/servicedesk/admin/$1/sla/custom/" + mapping.get(slaId);
								} else {
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
		for (URLPattern path : PATTERNS) {
			if (path.match(uri)) {
				return true;
			}
		}
		return false;
	}
}
