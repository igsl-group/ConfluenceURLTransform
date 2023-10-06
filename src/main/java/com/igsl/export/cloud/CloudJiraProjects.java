package com.igsl.export.cloud;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MultivaluedMap;

import com.igsl.config.Config;
import com.igsl.export.cloud.model.JiraProject;
import com.igsl.export.cloud.model.JiraProjects;

public class CloudJiraProjects extends BaseExport<JiraProjects> {

	public CloudJiraProjects() {
		super(JiraProjects.class);
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
		return Arrays.asList("ID", "KEY", "NAME", "DESCRIPTION");
	}

	@Override
	protected List<List<Object>> getRows(JiraProjects obj) {
		List<List<Object>> result = new ArrayList<>();
		for (JiraProject project : obj.getValues()) {
			result.add(Arrays.asList(project.getId(), project.getKey(), project.getName(), project.getDescription()));
		}
		return result;
	}

	@Override
	public List<JiraProjects> getObjects(Config config) throws Exception {
		MultivaluedMap<String, Object> header = getAuthenticationHeader(config);
		Map<String, Object> query = new HashMap<>();
		List<JiraProjects> result = invokeRest(config, "/rest/api/3/project/search", HttpMethod.GET, header, query, null);
		return result;
	}

}
