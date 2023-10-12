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
import com.igsl.export.cloud.model.JiraBoard;
import com.igsl.export.cloud.model.JiraBoards;
import com.igsl.export.dc.JiraRapidBoard;
import com.igsl.export.dc.ObjectExport;

public class CloudJiraBoards extends BaseExport<JiraBoards> {

	public static final String COL_ID = "ID";
	public static final String COL_NAME = "NAME";
	public static final String COL_TYPE = "TYPE";
	public static final List<String> COL_LIST = 
			Arrays.asList(COL_ID, COL_NAME, COL_TYPE);
	
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
		return COL_LIST;
	}

	@Override
	protected List<ObjectData> getCSVRows(JiraBoards obj) {
		List<ObjectData> result = new ArrayList<>();
		for (JiraBoard board : obj.getValues()) {
			List<String> list = Arrays.asList(board.getId(), board.getName(), board.getType());
			result.add(new ObjectData(board.getId(), board.getName(), list));
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

	@Override
	protected ObjectExport getObjectExport() {
		return new JiraRapidBoard();
	}

}
