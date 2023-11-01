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
import com.igsl.export.cloud.model.ConfluenceSpace;
import com.igsl.export.cloud.model.ConfluenceSpaces;
import com.igsl.export.dc.ObjectExport;

public class CloudConfluenceSpaces extends BaseExport<ConfluenceSpaces> {

	public static final String COL_ID = "ID";
	public static final String COL_KEY = "KEY";
	public static final String COL_NAME = "NAME";
	public static final List<String> COL_LIST = Arrays.asList(COL_ID, COL_KEY, COL_NAME);
	
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
		return COL_LIST;
	}

	@Override
	protected List<ObjectData> getCSVRows(ConfluenceSpaces obj) {
		List<ObjectData> result = new ArrayList<>();
		for (ConfluenceSpace space : obj.getResults()) {
			List<String> list = Arrays.asList(space.getId(), space.getKey(), space.getName());
			result.add(new ObjectData(space.getId(), space.getKey(), COL_LIST, list));
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

	@Override
	protected ObjectExport getObjectExport() {
		return new com.igsl.export.dc.ConfluenceSpace();
	}

}
