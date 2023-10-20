package com.igsl.handler.postmigrate.jira;

import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igsl.Log;
import com.igsl.config.Config;
import com.igsl.export.cloud.CloudJiraAttachments;
import com.igsl.handler.URLPattern;
import com.igsl.handler.postmigrate.BasePostMigrate;
import com.igsl.handler.postmigrate.MappingSetting;
import com.igsl.handler.postmigrate.PathSetting;

public class Attachment extends BasePostMigrate {

	private static final Logger LOGGER = LogManager.getLogger(Attachment.class);
	private static final String PARAM = "";
	
	private static final URLPattern PATTERN = new URLPattern().setPathRegex("/secure/attachment/(.+)/(.+)");
	
	public Attachment(Config config) {
		super(	config, 
				config.getUrlTransform().getConfluenceFromHost(), 
				Arrays.asList(
					new MappingSetting(
						new CloudJiraAttachments(), 
						CloudJiraAttachments.COL_DCID, 
						CloudJiraAttachments.COL_CLOUDID)
				),
				Arrays.asList(
					new PathSetting(
						Attachment.class,
						Pattern.compile("/secure/attachment/(.+)/(.+)"),
						CloudJiraAttachments.class
						) {
						@Override
						public String getReplacement(Matcher m, Map<String, Map<String, String>> mappings) {
							Map<String, String> mapping = mappings.get(getBaseExport().getCanonicalName());
							String attachmentId = m.group(1);
							if (mapping.containsKey(attachmentId)) {
								return "/secure/attachment/" + mapping.get(attachmentId) + "/$2";
							} else {
								Log.warn(LOGGER, 
										getPostMigrate().getCanonicalName() + 
										" Mapping not found for attachmentId: " + attachmentId);
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
//		URIBuilder parser = new URIBuilder(uri);
//		List<NameValuePair> params = parser.getQueryParams();
//		URIBuilder builder = new URIBuilder();
//		builder.setScheme(config.getUrlTransform().getToScheme());
//		builder.setHost(config.getUrlTransform().getJiraToHost());
//		String originalPath = uri.getPath();
//		if (originalPath.startsWith(config.getUrlTransform().getJiraFromBasePath())) {
//			originalPath = originalPath.substring(config.getUrlTransform().getJiraFromBasePath().length());
//		}
//		Matcher m = PATTERN.getPathPattern().matcher(originalPath);
//		if (m.matches()) {
//			String attachmentId = m.group(1);
//			String attachmentName = m.group(2);
//			StringBuilder sb = new StringBuilder();
//			if (mappings.get(PARAM).containsKey(attachmentId)) {
//				m.appendReplacement(sb, 
//						"/secure/attachment/" + mappings.get(PARAM).get(attachmentId) + "/" + attachmentName);
//				m.appendTail(sb);
//				builder.setPathSegments(addPathSegments(
//						config.getUrlTransform().getJiraToBasePath(),
//						sb.toString()));
//			} else {
//				Log.warn(LOGGER, "Mapping not found for attachmentId: " + attachmentId);
//				builder.setPathSegments(addPathSegments(
//						config.getUrlTransform().getJiraToBasePath(),
//						(originalPath.startsWith(config.getUrlTransform().getJiraFromBasePath())? 
//								originalPath.substring(config.getUrlTransform().getJiraFromBasePath().length()) : 
//								originalPath)
//						));
//			}			
//		} else {
//			Log.error(LOGGER, "Pattern does not match: " + originalPath);
//			builder.setPathSegments(addPathSegments(
//					config.getUrlTransform().getJiraToBasePath(),
//					(originalPath.startsWith(config.getUrlTransform().getJiraFromBasePath())? 
//							originalPath.substring(config.getUrlTransform().getJiraFromBasePath().length()) : 
//							originalPath)
//					));
//		}
//		builder.setParameters(params);
//		builder.setFragment(uri.getFragment());
//		return new HandlerResult(builder.build());
//	}
}
