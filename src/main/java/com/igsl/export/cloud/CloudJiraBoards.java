package com.igsl.export.cloud;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MultivaluedMap;

import com.igsl.config.Config;
import com.igsl.export.cloud.model.JiraBoard;
import com.igsl.export.cloud.model.JiraBoards;

public class CloudJiraBoards extends BaseExport<JiraBoards> {

	public CloudJiraBoards() {
		super(JiraBoards.class);
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
		return Arrays.asList("ID", "NAME", "TYPE");
	}

	@Override
	protected List<List<Object>> getRows(JiraBoards obj) {
		List<List<Object>> result = new ArrayList<>();
		for (JiraBoard board : obj.getValues()) {
			result.add(Arrays.asList(board.getId(), board.getName(), board.getType()));
		}
		return result;
	}

	@Override
	public List<JiraBoards> getObjects(Config config) throws Exception {
		MultivaluedMap<String, Object> header = getAuthenticationHeader(config);
		Map<String, Object> query = new HashMap<>();
		List<JiraBoards> result = invokeRest(config, 
				"/rest/agile/1.0/board", HttpMethod.GET, header, query, null);
		return result;
	}

}
