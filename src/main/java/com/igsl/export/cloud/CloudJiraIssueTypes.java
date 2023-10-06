package com.igsl.export.cloud;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MultivaluedMap;

import com.igsl.config.Config;
import com.igsl.export.cloud.model.JiraIssueType;

public class CloudJiraIssueTypes extends BaseExport<JiraIssueType> {

	public CloudJiraIssueTypes() {
		super(JiraIssueType.class);
	}

	@Override
	public String getLimitParameter() {
		return "limit";
	}

	@Override
	public String getStartAtParameter() {
		return "startAt";
	}

	@Override
	protected List<String> getCSVHeaders() {
		return Arrays.asList("ID", "NAME", "DESCRIPTION");
	}

	@Override
	protected List<List<Object>> getRows(JiraIssueType obj) {
		List<List<Object>> result = new ArrayList<>();
		result.add(Arrays.asList(obj.getId(), obj.getName(), obj.getDescription()));
		return result;
	}

	@Override
	public List<JiraIssueType> getObjects(Config config) throws Exception {
		MultivaluedMap<String, Object> header = getAuthenticationHeader(config);
		Map<String, Object> query = new HashMap<>();
		List<JiraIssueType> result = invokeRest(config, "/rest/api/3/issuetype", HttpMethod.GET, header, query, null);
		return result;
	}

}
