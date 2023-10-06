package com.igsl.export.cloud;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MultivaluedMap;

import com.igsl.config.Config;
import com.igsl.export.cloud.model.ConfluencePage;
import com.igsl.export.cloud.model.ConfluencePages;

public class CloudConfluencePages extends BaseExport<ConfluencePages> {

	public CloudConfluencePages() {
		super(ConfluencePages.class);
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
		return Arrays.asList("ID", "TITLE", "SPACEID");
	}

	@Override
	protected List<List<Object>> getRows(ConfluencePages obj) {
		List<List<Object>> result = new ArrayList<>();
		for (ConfluencePage page : obj.getResults()) {
			result.add(Arrays.asList(page.getId(), page.getTitle(), page.getSpaceId()));
		}
		return result;
	}

	@Override
	public List<ConfluencePages> getObjects(Config config) throws Exception {
		MultivaluedMap<String, Object> header = getAuthenticationHeader(config);
		Map<String, Object> query = new HashMap<>();
		query.put("body-format", "storage");
		List<ConfluencePages> result = invokeRest(config, "/wiki/api/v2/pages", HttpMethod.GET, header, query, null);
		return result;
	}

}
