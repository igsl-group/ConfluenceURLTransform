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
import com.igsl.rest.RESTUtil;

public class CloudConfluencePages extends BaseExport<ConfluencePages> {

	private static final Logger LOGGER = LogManager.getLogger(CloudConfluencePages.class);
	public static final String COL_ID = "ID";
	public static final String COL_TITLE = "TITLE";
	public static final String COL_VERSION = "VERSION";
	public static final String COL_SPACEID = "SPACEID";
	public static final String COL_SPACEKEY = "SPACEKEY";
	public static final List<String> COL_LIST = Arrays.asList(
			COL_ID, COL_TITLE, COL_VERSION, COL_SPACEID, COL_SPACEKEY);
	
	private String spaceId;
	private String title;
	private boolean getVersions = false;
	
	@Override
	protected boolean allowOneToManyMapping() {
		return true;
	};
	
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
			List<String> list = Arrays.asList(
					page.getId(), 
					page.getTitle(), 
					Integer.toString(page.getVersion().getNumber()),
					page.getSpaceId(),
					page.getSpaceKey());
			String uniqueKey = ObjectData.createUniqueKey(
					page.getSpaceKey(), page.getTitle());
			result.add(new ObjectData(page.getId(), uniqueKey, COL_LIST, list));
		}
		return result;
	}

	@Override
	public List<ConfluencePages> getObjects(Config config) throws Exception {
		MultivaluedMap<String, Object> header = getAuthenticationHeader(config);
		Map<String, Object> query = new HashMap<>();
		query.put("body-format", "storage");
		if (spaceId != null) {
			query.put("space-id", spaceId);
		}
		if (title != null) {
			query.put("title", title);
		}
		List<ConfluencePages> result = invokeRest(config, "/wiki/api/v2/pages", HttpMethod.GET, header, query, null);
		/*
		if (getVersions) {
			// Get versions for each page
			ConfluencePages versionedPages = new ConfluencePages();
			versionedPages.setResults(new ArrayList<ConfluencePage>());
			for (ConfluencePages pages : result) {
				for (ConfluencePage page : pages.getResults()) {
					int version = page.getVersion().getNumber();
					// Test each version number
					while (version > 1) { 
						version--;
						query.put("version", version);
						List<ConfluencePage> versionList = RESTUtil.invokeCloudRest(
								ConfluencePage.class, 
								config, 
								"/wiki/api/v2/pages/" + page.getId(), 
								HttpMethod.GET, 
								header, 
								query, 
								null, 
								"");
						int count = 0;
						for (ConfluencePage p : versionList) {
							versionedPages.getResults().add(p);
							count++;
						}
						if (count == 0) {
							break;
						}
					}
				}
			}
			result.add(versionedPages);
		}
		*/
		// Get spaces
		CloudConfluenceSpaces spacesExport = new CloudConfluenceSpaces();
		Map<String, String> spaceList = new HashMap<>();
		for (ConfluenceSpaces spaces : spacesExport.getObjects(config)) {
			for (ConfluenceSpace space : spaces.getResults()) {
				spaceList.put(space.getId(), space.getKey());
			}
		}		
		// Resolve spaceKey
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

	public String getSpaceId() {
		return spaceId;
	}

	public void setSpaceId(String spaceId) {
		this.spaceId = spaceId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isGetVersions() {
		return getVersions;
	}

	public void setGetVersions(boolean getVersions) {
		this.getVersions = getVersions;
	}
}
