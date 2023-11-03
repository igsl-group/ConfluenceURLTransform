package com.igsl.handler.postmigrate.jira;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igsl.config.Config;
import com.igsl.export.cloud.CloudJiraAttachments;
import com.igsl.handler.URLPattern;
import com.igsl.handler.postmigrate.BasePostMigrate;
import com.igsl.handler.postmigrate.MappingSetting;
import com.igsl.handler.postmigrate.PathSetting;

public class Attachment extends BasePostMigrate {

	private static final Logger LOGGER = LogManager.getLogger(Attachment.class);
	private static final String PARAM = "";
	
	@Override
	protected URLPattern[] getPatterns() {
		return new URLPattern[] {
			new URLPattern().setPathRegex(
					Pattern.quote(config.getUrlTransform().getJiraToBasePath()) + 
					"/secure/attachment/(.+)/(.+)")
		};
	}
	
	public Attachment(Config config) {
		super(	config, 
				config.getUrlTransform().getJiraToHost(),
				Arrays.asList(
					new MappingSetting(
						new CloudJiraAttachments(), 
						CloudJiraAttachments.COL_DCID, 
						CloudJiraAttachments.COL_CLOUDID)
				),
				Arrays.asList(
					new PathSetting(
						Attachment.class,
						Pattern.compile(
								Pattern.quote(config.getUrlTransform().getJiraToBasePath()) + 
								"/secure/attachment/(.+)/(.+)"),
						CloudJiraAttachments.class
						) {
						@Override
						public String getReplacement(Matcher m, Map<String, Map<String, String>> mappings) 
								throws Exception {
							Map<String, String> mapping = mappings.get(getBaseExport().getCanonicalName());
							String attachmentId = m.group(1);
							if (mapping.containsKey(attachmentId)) {
								return "/rest/api/3/attachment/content/" + mapping.get(attachmentId);
							} else {
								throw new Exception(getPostMigrate().getCanonicalName() + 
										" Mapping not found for attachmentId: " + attachmentId);
							}
						}
					}
				),
				null);
	}
}
