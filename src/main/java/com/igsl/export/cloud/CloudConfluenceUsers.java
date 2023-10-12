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
import com.igsl.export.cloud.model.ConfluenceSearchResult;
import com.igsl.export.cloud.model.ConfluenceUser;
import com.igsl.export.cloud.model.ConfluenceUsers;
import com.igsl.export.dc.ObjectExport;

public class CloudConfluenceUsers extends BaseExport<ConfluenceUsers> {

	public static final String COL_ACCOUNTID = "ACCOUNTID";
	public static final String COL_EMAIL = "EMAIL";
	public static final String COL_DISPLAYNAME = "DISPLAYNAME";
	public static final String COL_PUBLICNAME = "PUBLICNAME";
	public static final List<String> COL_LIST = 
			Arrays.asList(COL_ACCOUNTID, COL_EMAIL, COL_DISPLAYNAME, COL_PUBLICNAME);
	
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
		return COL_LIST;
	}

	@Override
	protected List<ObjectData> getCSVRows(ConfluenceUsers obj) {
		List<ObjectData> result = new ArrayList<>();
		for (ConfluenceSearchResult r : obj.getResults()) {
			ConfluenceUser user = r.getUser();
			List<String> list = Arrays.asList(
					user.getAccountId(), user.getEmail(), user.getDisplayName(), user.getPublicName());
			result.add(new ObjectData(user.getAccountId(), user.getDisplayName(), list));
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

	@Override
	protected ObjectExport getObjectExport() {
		return new com.igsl.export.dc.ConfluenceUser();
	}

}
