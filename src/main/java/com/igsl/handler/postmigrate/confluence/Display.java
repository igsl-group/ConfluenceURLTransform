package com.igsl.handler.postmigrate.confluence;

import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igsl.config.Config;
import com.igsl.export.cloud.CloudConfluencePages;
import com.igsl.export.cloud.CloudConfluenceSpaces;
import com.igsl.handler.URLPattern;
import com.igsl.handler.postmigrate.BasePostMigrate;
import com.igsl.handler.postmigrate.MappingSetting;
import com.igsl.handler.postmigrate.ParamSetting;

public class Display extends BasePostMigrate {

	private static final Logger LOGGER = LogManager.getLogger(Display.class);
	private static final String PREVIEW = "preview";
	private static final String PAGEID = "pageId";
	
	private static final URLPattern[] PATTERNS = new URLPattern[] {
			new URLPattern("/display/[^?]+").setQuery("pageId", "preview"),
	};
	
	public Display(Config config) {
		super(	config, 
				config.getUrlTransform().getConfluenceFromHost(), 
				Arrays.asList(
					new MappingSetting(
						new CloudConfluenceSpaces(),
						CloudConfluenceSpaces.COL_DCID,
						CloudConfluenceSpaces.COL_CLOUDID),
					new MappingSetting(
						new CloudConfluencePages(), 
						CloudConfluencePages.COL_DCID, 
						CloudConfluencePages.COL_CLOUDID)
				),
				null,
				Arrays.asList(
					new ParamSetting(PAGEID, CloudConfluencePages.class),
					new ParamSetting(
							PREVIEW, 
							CloudConfluencePages.class) {
						public String getReplacement(NameValuePair param, Map<String, Map<String, String>> mappings) {
							Pattern p = Pattern.compile("/([0-9]+)/([0-9]+)/(.+)");
							Matcher m = p.matcher(param.getValue());
							Map<String, String> spaceIdMap = mappings.get(CloudConfluenceSpaces.class.getCanonicalName());
							Map<String, String> pageIdMap = mappings.get(CloudConfluencePages.class.getCanonicalName());
							if (m.matches()) {
								String spaceId = m.group(1);
								String pageId = m.group(2);
								String attachmentName = m.group(3);
								if (spaceIdMap.containsKey(spaceId) && pageIdMap.containsKey(pageId)) {
									StringBuilder sb = new StringBuilder();
									m.appendReplacement(sb, 
											"/" + spaceIdMap.get(spaceId) + 
											"/" + pageIdMap.get(pageId) + 
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
//		loadMappings();
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
//		// Replace path segments
//		Matcher m = PREVIEW_PATTER.matcher(originalPath);
//		if (m.matches()) {
//			String spaceId = m.group(1);
//			String pageId = m.group(2);
//			String attachmentName = m.group(3);
//			StringBuilder sb = new StringBuilder();
//			if (mappings.get(SPACE).containsKey(spaceId)) {
//				spaceId = mappings.get(SPACE).get(spaceId);
//			} else {
//				Log.warn(LOGGER, "Mapping not found for spaceId: " + spaceId);
//			}
//			if (mappings.get(PAGE).containsKey(pageId)) {
//				pageId = mappings.get(PAGE).get(pageId);
//			} else {
//				Log.warn(LOGGER, "Mapping not found for pageId: " + pageId);
//			}
//			m.appendReplacement(sb, "/" + spaceId + "/" + pageId + "/" + attachmentName);
//			m.appendTail(sb);
//			builder.setPathSegments(addPathSegments(
//					config.getUrlTransform().getConfluenceToBasePath(),
//					sb.toString()));
//			// Restore parameters
//			builder.setParameters(params);
//			// Restore fragments
//			builder.setFragment(uri.getFragment());
//			return new HandlerResult(builder.build());
//		} else {
//			Log.error(LOGGER, "Preview parameter does not match pattern: " + uri.toASCIIString());
//			return new HandlerResult(uri);
//		}
//	}
}
