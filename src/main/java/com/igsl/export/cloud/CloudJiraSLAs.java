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
import com.igsl.export.cloud.model.JiraSLA;
import com.igsl.export.cloud.model.JiraSLAs;
import com.igsl.export.cloud.model.JiraServiceDesk;
import com.igsl.export.cloud.model.JiraServiceDesks;
import com.igsl.export.dc.ObjectExport;

public class CloudJiraSLAs extends BaseExport<JiraSLAs> {

	public static final String COL_ID = "ID";
	public static final String COL_PROJECTKEY = "PROJECTKEY";
	public static final String COL_NAME = "NAME";
	public static final List<String> COL_LIST = Arrays.asList(COL_ID, COL_PROJECTKEY, COL_NAME);
	
	public CloudJiraSLAs() {
		super(JiraSLAs.class);
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
	protected List<ObjectData> getCSVRows(JiraSLAs obj) {
		List<ObjectData> result = new ArrayList<>();
		for (JiraSLA sla : obj.getTimeMetrics()) {
			List<String> list = Arrays.asList(sla.getId(), sla.getProjectKey(), sla.getName());
			String uniqueKey = ObjectData.createUniqueKey(sla.getProjectKey(), sla.getName());
			result.add(new ObjectData(sla.getId(), uniqueKey, COL_LIST, list));
		}
		return result;
	}

	@Override
	public List<JiraSLAs> getObjects(Config config) throws Exception {
		List<JiraSLAs> result = new ArrayList<>();
		MultivaluedMap<String, Object> header = getAuthenticationHeader(config);
		CloudJiraServiceDesks deskExport = new CloudJiraServiceDesks();
		List<JiraServiceDesks> desksList = deskExport.getObjects(config);
		for (JiraServiceDesks desks : desksList) {
			for (JiraServiceDesk desk : desks.getValues()) {
				Map<String, Object> query = new HashMap<>();
				List<JiraSLAs> slaList = invokeRest(config, 
						"/rest/servicedesk/1/servicedesk/agent/" + desk.getProjectKey() + "/sla/metrics", 
						HttpMethod.GET, header, query, null);
				result.addAll(slaList);
			}
		}
		return result;
	}

	@Override
	protected ObjectExport getObjectExport() {
		return new com.igsl.export.dc.JiraSLA();
	}

}
