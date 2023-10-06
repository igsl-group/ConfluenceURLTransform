package com.igsl.export.cloud;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MultivaluedMap;

import com.igsl.config.Config;
import com.igsl.export.cloud.model.ConfluenceSpace;
import com.igsl.export.cloud.model.ConfluenceSpaces;

public class CloudConfluenceSpaces extends BaseExport<ConfluenceSpaces> {

	public CloudConfluenceSpaces() {
		super(ConfluenceSpaces.class);
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
		return Arrays.asList("ID", "KEY", "NAME");
	}

	@Override
	protected List<List<Object>> getRows(ConfluenceSpaces obj) {
		List<List<Object>> result = new ArrayList<>();
		for (ConfluenceSpace space : obj.getResults()) {
			result.add(Arrays.asList(space.getId(), space.getKey(), space.getName()));
		}
		return result;
	}

	@Override
	public List<ConfluenceSpaces> getObjects(Config config) throws Exception {
		MultivaluedMap<String, Object> header = getAuthenticationHeader(config);
		Map<String, Object> query = new HashMap<>();
		List<ConfluenceSpaces> result = invokeRest(config, "/wiki/api/v2/spaces", HttpMethod.GET, header, query, null);
		return result;
	}

}
