package com.igsl.handler.postmigrate.confluence;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igsl.config.Config;
import com.igsl.export.cloud.CloudConfluenceAttachments;
import com.igsl.export.cloud.CloudConfluencePages;
import com.igsl.handler.URLPattern;
import com.igsl.handler.postmigrate.BasePostMigrate;
import com.igsl.handler.postmigrate.MappingSetting;
import com.igsl.handler.postmigrate.ParamSetting;

public class Display extends BasePostMigrate {

	private static final Logger LOGGER = LogManager.getLogger(Display.class);
	private static final String PREVIEW = "preview";
	private static final String PAGEID = "pageId";
	
	@Override
	protected URLPattern[] getPatterns() {
		return new URLPattern[] {
			new URLPattern("/display/[^?]+").setQuery(PAGEID, PREVIEW),
		};
	}
	
	public Display(Config config) {
		super(	config, 
				config.getUrlTransform().getConfluenceToHost(), 
				config.getUrlTransform().getConfluenceToBasePath(),
				Arrays.asList(
					new MappingSetting(
						new CloudConfluencePages(), 
						CloudConfluencePages.COL_DCID, 
						CloudConfluencePages.COL_CLOUDID),
					new MappingSetting(
						new CloudConfluenceAttachments(),
						CloudConfluenceAttachments.COL_DCID,
						CloudConfluenceAttachments.COL_CLOUDID)
				),
				null,
				Arrays.asList(
					new ParamSetting(Display.class, PAGEID, CloudConfluencePages.class),
					new ParamSetting(
							Display.class, 
							PREVIEW, 
							CloudConfluencePages.class) {
						public String getReplacement(NameValuePair param, Map<String, Map<String, String>> mappings) {
							Pattern p = Pattern.compile("/([0-9]+)/([0-9]+)/(.+)");
							Matcher m = p.matcher(param.getValue());
							Map<String, String> attachmentMap = 
									mappings.get(CloudConfluenceAttachments.class.getCanonicalName());
							Map<String, String> pageIdMap = mappings.get(CloudConfluencePages.class.getCanonicalName());
							if (m.matches()) {
								String pageId = m.group(1);
								String attachmentId = m.group(2);
								String attachmentName = m.group(3);
								if (pageIdMap.containsKey(pageId) && attachmentMap.containsKey(attachmentId)) {
									StringBuilder sb = new StringBuilder();
									m.appendReplacement(sb, 
											"/" + pageIdMap.get(pageId) + 
											"/" + attachmentMap.get(attachmentId) + 
											"/" + attachmentName);
									m.appendTail(sb);
									return sb.toString();
								}
							} 
							return param.getValue();
						}
					}
				));
	}
}
