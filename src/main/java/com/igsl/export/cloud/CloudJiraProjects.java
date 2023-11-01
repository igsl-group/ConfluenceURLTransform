package com.igsl.export.cloud;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MultivaluedMap;

import com.igsl.ObjectData;
import com.igsl.config.Config;
import com.igsl.export.cloud.model.JiraProject;
import com.igsl.export.cloud.model.JiraProjects;
import com.igsl.export.dc.ObjectExport;

public class CloudJiraProjects extends BaseExport<JiraProjects> {

	public static final String COL_ID = "ID";
	public static final String COL_KEY = "KEY";
	public static final String COL_NAME = "NAME";
	public static final String COL_DESCRIPTION = "DESCRIPTION";
	public static final List<String> COL_LIST = Arrays.asList(
				COL_ID,
				COL_KEY,
				COL_NAME,
				COL_DESCRIPTION
			);
	
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
		return COL_LIST;
	}

	@Override
	protected List<ObjectData> getCSVRows(JiraProjects obj) {
		List<ObjectData> result = new ArrayList<>();
		for (JiraProject project : obj.getValues()) {
			List<String> list = Arrays.asList(
					project.getId(), project.getKey(), project.getName(), project.getDescription());
			result.add(new ObjectData(project.getId(), project.getKey(), COL_LIST, list));
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

	@Override
	protected ObjectExport getObjectExport() {
		return new com.igsl.export.dc.JiraProject();
	}

}
