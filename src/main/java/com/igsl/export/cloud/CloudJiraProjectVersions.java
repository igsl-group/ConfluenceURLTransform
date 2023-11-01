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
import com.igsl.export.cloud.model.JiraProjectVersion;
import com.igsl.export.cloud.model.JiraProjectVersions;
import com.igsl.export.cloud.model.JiraProjects;
import com.igsl.export.dc.ObjectExport;

public class CloudJiraProjectVersions extends BaseExport<JiraProjectVersions> {

	public static final String COL_ID = "ID";
	public static final String COL_NAME = "NAME";
	public static final String COL_DESCRIPTION = "DESCRIPTION";
	public static final String COL_PROJECTKEY = "PROJECTKEY";
	public static final List<String> COL_LIST = Arrays.asList(
				COL_ID,
				COL_NAME,
				COL_DESCRIPTION,
				COL_PROJECTKEY
			);
	
	public CloudJiraProjectVersions() {
		super(JiraProjectVersions.class);
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
	protected List<ObjectData> getCSVRows(JiraProjectVersions obj) {
		List<ObjectData> result = new ArrayList<>();
		for (JiraProjectVersion ver : obj.getValues()) {
			List<String> list = Arrays.asList(ver.getId(), ver.getName(), ver.getDescription(), obj.getProject().getKey());
			String uniqueKey = ObjectData.createUniqueKey(obj.getProject().getKey(), ver.getName());
			result.add(new ObjectData(ver.getId(), uniqueKey, COL_LIST, list));
		}
		return result;
	}

	@Override
	public List<JiraProjectVersions> getObjects(Config config) throws Exception {
		List<JiraProjectVersions> result = new ArrayList<>();
		MultivaluedMap<String, Object> header = getAuthenticationHeader(config);
		CloudJiraProjects projects = new CloudJiraProjects();
		List<JiraProjects> projectList = projects.getObjects(config);
		for (JiraProjects list : projectList) {
			for (JiraProject project : list.getValues()) {
				Map<String, Object> query = new HashMap<>();
				List<JiraProjectVersions> versions = invokeRest(config, 
						"/rest/api/3/project/" + project.getKey() + "/version", HttpMethod.GET, header, query, null);
				// Set project
				for (JiraProjectVersions version : versions) {
					version.setProject(project);
				}
				result.addAll(versions);
			}
		}
		return result;
	}

	@Override
	protected ObjectExport getObjectExport() {
		return new com.igsl.export.dc.JiraProjectVersion();
	}
}
