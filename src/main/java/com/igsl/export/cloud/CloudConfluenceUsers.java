package com.igsl.export.cloud;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MultivaluedMap;

import com.igsl.config.Config;
import com.igsl.export.cloud.model.ConfluenceSearchResult;
import com.igsl.export.cloud.model.ConfluenceUser;
import com.igsl.export.cloud.model.ConfluenceUsers;

public class CloudConfluenceUsers extends BaseExport<ConfluenceUsers> {

	public CloudConfluenceUsers() {
		super(ConfluenceUsers.class);
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
		return Arrays.asList("ACCOUNTID", "EMAIL", "DISPLAYNAME", "PUBLICNAME");
	}

	@Override
	protected List<List<Object>> getRows(ConfluenceUsers obj) {
		List<List<Object>> result = new ArrayList<>();
		for (ConfluenceSearchResult r : obj.getResults()) {
			ConfluenceUser user = r.getUser();
			result.add(Arrays.asList(
					user.getAccountId(), user.getEmail(), user.getDisplayName(), user.getPublicName()
					));
		}
		return result;
	}

	@Override
	public List<ConfluenceUsers> getObjects(Config config) throws Exception {
		MultivaluedMap<String, Object> header = getAuthenticationHeader(config);
		Map<String, Object> query = new HashMap<>();
		query.put("cql", "type=user");
		List<ConfluenceUsers> result = invokeRest(config, "/wiki/rest/api/search/user", HttpMethod.GET, header, query, null);
		return result;
	}

}
