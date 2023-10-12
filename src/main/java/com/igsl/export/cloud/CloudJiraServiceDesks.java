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
import com.igsl.export.cloud.model.JiraServiceDesk;
import com.igsl.export.cloud.model.JiraServiceDesks;
import com.igsl.export.dc.JiraCustomerPortal;
import com.igsl.export.dc.ObjectExport;

public class CloudJiraServiceDesks extends BaseExport<JiraServiceDesks> {

	public static final String COL_ID = "ID";
	public static final String COL_PROJECTID = "PROJECTID";
	public static final String COL_PROJECTNAME = "PROJECTNAME";
	public static final String COL_PROJECTKEY = "PROJECTKEY";
	public static final List<String> COL_LIST = Arrays.asList(
				COL_ID,
				COL_PROJECTID,
				COL_PROJECTNAME,
				COL_PROJECTKEY
			);
	
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
		return COL_LIST;
	}

	@Override
	protected List<ObjectData> getCSVRows(JiraServiceDesks obj) {
		List<ObjectData> result = new ArrayList<>();
		for (JiraServiceDesk desk : obj.getValues()) {
			List<String> list = Arrays.asList(
					desk.getId(),
					desk.getProjectId(),
					desk.getProjectName(),
					desk.getProjectKey()
					);
			result.add(new ObjectData(desk.getId(), desk.getProjectKey(), list));
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

	@Override
	protected ObjectExport getObjectExport() {
		return new JiraCustomerPortal();
	}

}
