package com.igsl.handler.postmigrate.jira;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igsl.config.Config;
import com.igsl.export.cloud.CloudJiraDashboards;
import com.igsl.handler.URLPattern;
import com.igsl.handler.postmigrate.BasePostMigrate;
import com.igsl.handler.postmigrate.MappingSetting;
import com.igsl.handler.postmigrate.URLSetting;

public class Dashboard extends BasePostMigrate {

	private static final Logger LOGGER = LogManager.getLogger(Dashboard.class);
	private static final String PAGEID = "pageId";
	private static final String SELECT_PAGEID = "selectPageId";
	
	@Override
	protected URLPattern[] getPatterns() {
		return new URLPattern[] {
			new URLPattern()
				.setPath(
						config.getUrlTransform().getJiraToBasePath() + 
						"/secure/Dashboard.jspa")
				.setQuery(PAGEID, SELECT_PAGEID),
		};
	}
	
	public Dashboard(Config config) {
		super(	config, 
				config.getUrlTransform().getJiraToHost(), 
				Arrays.asList(
					new MappingSetting(
						new CloudJiraDashboards(), 
						CloudJiraDashboards.COL_DCID, 
						CloudJiraDashboards.COL_CLOUDID)
				),
				Arrays.asList(
					new URLSetting(
							Dashboard.class, 
							Pattern.compile(
									Pattern.quote(config.getUrlTransform().getJiraToBasePath() + 
									"/secure/Dashboard.jspa")),
							CloudJiraDashboards.class,
							PAGEID, null, 
							CloudJiraDashboards.class) {
							private String dashboardId;
							@Override
							public void process(
									String path, 
									List<NameValuePair> query, 
									Map<String, Map<String, String>> mappings) throws Exception {
								// Store dashboard Id
								for (NameValuePair item : query) {
									if (PAGEID.equals(item.getName())) {
										this.dashboardId = item.getValue();
										break;
									} else if (SELECT_PAGEID.equals(item.getName())) {
										this.dashboardId = item.getValue();
										break;
									}
								}
								Map<String, String> mapping = mappings.get(this.getPathBaseExport().getCanonicalName());
								if (mapping.containsKey(this.dashboardId)) {
									this.dashboardId = mapping.get(this.dashboardId);
								} else {
									throw new Exception(getPostMigrate().getCanonicalName() + 
											" Mapping not found for dashboardId: " + this.dashboardId);
								}
							}
							@Override
							public String getPath() {
								return config.getUrlTransform().getJiraToBasePath() + "/dashboards/" + dashboardId;
							}
							@Override
							public Map<String, String> getParameters() {
								return null;
							}
					}
				));
	}
}

// TODO Cloud dashboard link puts ID in path, not param