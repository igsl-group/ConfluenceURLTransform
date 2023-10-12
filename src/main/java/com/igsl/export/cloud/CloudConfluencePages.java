package com.igsl.export.cloud;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igsl.ObjectData;
import com.igsl.config.Config;
import com.igsl.export.cloud.model.ConfluencePage;
import com.igsl.export.cloud.model.ConfluencePages;
import com.igsl.export.cloud.model.ConfluenceSpace;
import com.igsl.export.cloud.model.ConfluenceSpaces;
import com.igsl.export.dc.ObjectExport;

public class CloudConfluencePages extends BaseExport<ConfluencePages> {

	private static final Logger LOGGER = LogManager.getLogger(CloudConfluencePages.class);
	public static final String COL_ID = "ID";
	public static final String COL_TITLE = "TITLE";
	public static final String COL_SPACEID = "SPACEID";
	public static final List<String> COL_LIST = Arrays.asList(COL_ID, COL_TITLE, COL_SPACEID);
	
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
		return COL_LIST;
	}

	@Override
	protected List<ObjectData> getCSVRows(ConfluencePages obj) {
		List<ObjectData> result = new ArrayList<>();
		for (ConfluencePage page : obj.getResults()) {
			List<String> list = Arrays.asList(page.getId(), page.getTitle(), page.getSpaceId());
			String uniqueKey = ObjectData.createUniqueKey(page.getSpaceKey(), page.getTitle());
			result.add(new ObjectData(page.getId(), uniqueKey, list));
		}
		return result;
	}

	@Override
	public List<ConfluencePages> getObjects(Config config) throws Exception {
		MultivaluedMap<String, Object> header = getAuthenticationHeader(config);
		Map<String, Object> query = new HashMap<>();
		query.put("body-format", "storage");
		List<ConfluencePages> result = invokeRest(config, "/wiki/api/v2/pages", HttpMethod.GET, header, query, null);
		// Resolve spaceKey
		CloudConfluenceSpaces spacesExport = new CloudConfluenceSpaces();
		Map<String, String> spaceList = new HashMap<>();
		for (ConfluenceSpaces spaces : spacesExport.getObjects(config)) {
			for (ConfluenceSpace space : spaces.getResults()) {
				spaceList.put(space.getId(), space.getKey());
			}
		}		
		for (ConfluencePages pages : result) {
			for (ConfluencePage page : pages.getResults()) {
				if (spaceList.containsKey(page.getSpaceId())) {
					page.setSpaceKey(spaceList.get(page.getSpaceId()));
				}
			}
		}
		return result;
	}

	@Override
	protected ObjectExport getObjectExport() {
		return new com.igsl.export.dc.ConfluencePage();
	}
}
