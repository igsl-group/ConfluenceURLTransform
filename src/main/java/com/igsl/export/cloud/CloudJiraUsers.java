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
import com.igsl.export.cloud.model.JiraUser;
import com.igsl.export.dc.ObjectExport;

public class CloudJiraUsers extends BaseExport<JiraUser> {

	public static final String COL_ID = "ID";
	public static final String COL_DISPLAY_NAME = "DISPLAY_NAME";
	public static final String COL_ACTIVE = "ACTIVE";
	public static final List<String> COL_LIST = Arrays.asList(COL_ID, COL_DISPLAY_NAME, COL_ACTIVE);
	
	public CloudJiraUsers() {
		super(JiraUser.class);
	}
	
	@Override
	public String getLimitParameter() {
		return "maxResults";
	}

	@Override
	public String getStartAtParameter() {
		return "start";
	}

	@Override
	protected List<String> getCSVHeaders() {
		return COL_LIST;
	}

	@Override
	protected List<ObjectData> getCSVRows(JiraUser obj) {
		List<ObjectData> result = new ArrayList<>();
		List<String> list = Arrays.asList(obj.getAccountId(), obj.getDisplayName(), Boolean.toString(obj.isActive()));
		result.add(new ObjectData(obj.getAccountId(), obj.getDisplayName(), COL_LIST, list));
		return result;
	}

	@Override
	protected List<JiraUser> _getObjects(Config config) throws Exception {
		List<JiraUser> result = new ArrayList<>();
		MultivaluedMap<String, Object> header = getAuthenticationHeader(config);
		Map<String, Object> query = new HashMap<>();
		result = invokeRest(config, 
				"/rest/api/3/users/search", 
				HttpMethod.GET, header, query, null);
		return result;
	}

	@Override
	protected ObjectExport getObjectExport() {
		return new com.igsl.export.dc.JiraUser();
	}

}
