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
import com.igsl.export.cloud.model.JiraIssueType;
import com.igsl.export.dc.ObjectExport;

public class CloudJiraIssueTypes extends BaseExport<JiraIssueType> {

	public static final String COL_ID = "ID";
	public static final String COL_NAME = "NAME";
	public static final String COL_DESCRIPTION = "DESCRIPTION";
	public static final List<String> COL_LIST = Arrays.asList(
				COL_ID,
				COL_NAME,
				COL_DESCRIPTION
			);
	
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
		return COL_LIST;
	}

	@Override
	protected List<ObjectData> getCSVRows(JiraIssueType obj) {
		List<ObjectData> result = new ArrayList<>();
		List<String> list = Arrays.asList(obj.getId(), obj.getName(), obj.getDescription());
		result.add(new ObjectData(obj.getId(), obj.getName(), COL_LIST, list));
		return result;
	}

	@Override
	protected List<JiraIssueType> _getObjects(Config config) throws Exception {
		MultivaluedMap<String, Object> header = getAuthenticationHeader(config);
		Map<String, Object> query = new HashMap<>();
		List<JiraIssueType> result = invokeRest(config, "/rest/api/3/issuetype", HttpMethod.GET, header, query, null);
		return result;
	}

	@Override
	protected ObjectExport getObjectExport() {
		return new com.igsl.export.dc.JiraIssueType();
	}

}
