package com.igsl.export.cloud;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MultivaluedMap;

import com.igsl.config.Config;
import com.igsl.export.cloud.model.JiraFilter;
import com.igsl.export.cloud.model.JiraFilters;

public class CloudJiraFilters extends BaseExport<JiraFilters> {

	public CloudJiraFilters() {
		super(JiraFilters.class);
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
		return Arrays.asList("ID", "NAME", "DESCRIPTION", "OWNER_ACCOUNTID", "OWNER_DISPLAYNAME", "JQL");
	}

	@Override
	protected List<List<Object>> getRows(JiraFilters obj) {
		List<List<Object>> result = new ArrayList<>();
		for (JiraFilter filter: obj.getValues()) {
			result.add(Arrays.asList(
					filter.getId(),
					filter.getName(),
					filter.getDescription(),
					(filter.getOwner() != null)? filter.getOwner().getAccountId() : null,
					(filter.getOwner() != null)? filter.getOwner().getDisplayName() : null,
					filter.getJql()
					));
		}
		return result;
	}

	@Override
	public List<JiraFilters> getObjects(Config config) throws Exception {
		MultivaluedMap<String, Object> header = getAuthenticationHeader(config);
		Map<String, Object> query = new HashMap<>();
		query.put("expand", "jql");
		List<JiraFilters> result = invokeRest(config, 
				"/rest/api/3/filter/search", HttpMethod.GET, header, query, null);
		return result;
	}

}
