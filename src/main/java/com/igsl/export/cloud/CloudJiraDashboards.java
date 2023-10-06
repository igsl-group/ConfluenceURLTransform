package com.igsl.export.cloud;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MultivaluedMap;

import com.igsl.config.Config;
import com.igsl.export.cloud.model.JiraDashboard;
import com.igsl.export.cloud.model.JiraDashboards;

public class CloudJiraDashboards extends BaseExport<JiraDashboards> {

	public CloudJiraDashboards() {
		super(JiraDashboards.class);
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
		return Arrays.asList("ID", "NAME", "OWNER_ACCOUNTID", "OWNER_DISPLAYNAME");
	}

	@Override
	protected List<List<Object>> getRows(JiraDashboards obj) {
		List<List<Object>> result = new ArrayList<>();
		for (JiraDashboard board : obj.getDashboards()) {
			result.add(Arrays.asList(
					board.getId(),
					board.getName(),
					(board.getOwner() != null)? board.getOwner().getAccountId() : null,
					(board.getOwner() != null)? board.getOwner().getDisplayName() : null
					));
		}
		return result;
	}

	@Override
	public List<JiraDashboards> getObjects(Config config) throws Exception {
		MultivaluedMap<String, Object> header = getAuthenticationHeader(config);
		Map<String, Object> query = new HashMap<>();
		List<JiraDashboards> result = invokeRest(config, 
				"/rest/api/3/dashboard", HttpMethod.GET, header, query, null);
		return result;
	}

}
