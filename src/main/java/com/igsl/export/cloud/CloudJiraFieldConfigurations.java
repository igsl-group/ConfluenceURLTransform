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
import com.igsl.export.cloud.model.JiraFieldConfiguration;
import com.igsl.export.cloud.model.JiraFieldConfigurations;
import com.igsl.export.dc.ObjectExport;

public class CloudJiraFieldConfigurations extends BaseExport<JiraFieldConfigurations> {

	public static final String COL_ID = "ID";
	public static final String COL_NAME = "NAME";
	public static final String COL_DESCRIPTION = "DESCRIPTION";
	public static final String COL_ISDEFAULT = "IS_DEFAULT";
	public static final List<String> COL_LIST = Arrays.asList(
				COL_ID,
				COL_NAME,
				COL_DESCRIPTION,
				COL_ISDEFAULT
			);
	
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
		return COL_LIST;
	}

	@Override
	protected List<ObjectData> getCSVRows(JiraFieldConfigurations obj) {
		List<ObjectData> result = new ArrayList<>();
		for (JiraFieldConfiguration conf : obj.getValues()) {
			List<String> list = Arrays.asList(
					conf.getId(),
					conf.getName(),
					conf.getDescription(),
					Boolean.toString(conf.isIsDefault())
					);
			result.add(new ObjectData(conf.getId(), conf.getName(), COL_LIST, list));
		}
		return result;
	}

	@Override
	protected List<JiraFieldConfigurations> _getObjects(Config config) throws Exception {
		MultivaluedMap<String, Object> header = getAuthenticationHeader(config);
		Map<String, Object> query = new HashMap<>();
		List<JiraFieldConfigurations> result = invokeRest(config, 
				"/rest/api/3/fieldconfiguration", HttpMethod.GET, header, query, null);
		return result;
	}

	@Override
	protected ObjectExport getObjectExport() {
		return new com.igsl.export.dc.JiraFieldConfiguration();
	}

}
