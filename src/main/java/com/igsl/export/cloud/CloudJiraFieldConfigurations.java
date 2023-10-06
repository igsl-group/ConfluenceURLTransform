package com.igsl.export.cloud;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MultivaluedMap;

import com.igsl.config.Config;
import com.igsl.export.cloud.model.JiraFieldConfiguration;
import com.igsl.export.cloud.model.JiraFieldConfigurations;

public class CloudJiraFieldConfigurations extends BaseExport<JiraFieldConfigurations> {

	public CloudJiraFieldConfigurations() {
		super(JiraFieldConfigurations.class);
	}

	@Override
	public String getLimitParameter() {
		return "maxResults";
	}

	@Override
	public String getStartAtParameter() {
		return "startAt";
	}

	@Override
	protected List<String> getCSVHeaders() {
		return Arrays.asList("ID", "NAME", "DESCRIPTION", "IS_DEFAULT");
	}

	@Override
	protected List<List<Object>> getRows(JiraFieldConfigurations obj) {
		List<List<Object>> result = new ArrayList<>();
		for (JiraFieldConfiguration conf : obj.getValues()) {
			result.add(Arrays.asList(
					conf.getId(),
					conf.getName(),
					conf.getDescription(),
					conf.isIsDefault()
					));
		}
		return result;
	}

	@Override
	public List<JiraFieldConfigurations> getObjects(Config config) throws Exception {
		MultivaluedMap<String, Object> header = getAuthenticationHeader(config);
		Map<String, Object> query = new HashMap<>();
		List<JiraFieldConfigurations> result = invokeRest(config, 
				"/rest/api/3/fieldconfiguration", HttpMethod.GET, header, query, null);
		return result;
	}

}
