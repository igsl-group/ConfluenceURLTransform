package com.igsl.export.cloud;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MultivaluedMap;

import com.igsl.config.Config;
import com.igsl.export.cloud.model.JiraServiceDesk;
import com.igsl.export.cloud.model.JiraServiceDesks;

public class CloudJiraServiceDesks extends BaseExport<JiraServiceDesks> {

	public CloudJiraServiceDesks() {
		super(JiraServiceDesks.class);
	}
	
	@Override
	public String getLimitParameter() {
		return "limit";
	}

	@Override
	public String getStartAtParameter() {
		return "start";
	}

	@Override
	protected List<String> getCSVHeaders() {
		return Arrays.asList("ID", "PROJECTID", "PROJECTNAME", "PROJECTKEY");
	}

	@Override
	protected List<List<Object>> getRows(JiraServiceDesks obj) {
		List<List<Object>> result = new ArrayList<>();
		for (JiraServiceDesk desk : obj.getValues()) {
			result.add(Arrays.asList(
					desk.getId(),
					desk.getProjectId(),
					desk.getProjectName(),
					desk.getProjectKey()
					));
		}
		return result;
	}

	@Override
	public List<JiraServiceDesks> getObjects(Config config) throws Exception {
		MultivaluedMap<String, Object> header = getAuthenticationHeader(config);
		Map<String, Object> query = new HashMap<>();
		List<JiraServiceDesks> result = invokeRest(config, 
				"/rest/servicedeskapi/servicedesk", HttpMethod.GET, header, query, null);
		return result;
	}

}
