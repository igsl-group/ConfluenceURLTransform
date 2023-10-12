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
import com.igsl.export.cloud.model.JiraFilter;
import com.igsl.export.cloud.model.JiraFilters;
import com.igsl.export.dc.ObjectExport;
import com.mysql.cj.x.protobuf.MysqlxDatatypes.Array;

public class CloudJiraFilters extends BaseExport<JiraFilters> {

	public static final String COL_ID = "ID";
	public static final String COL_NAME = "NAME";
	public static final String COL_DESCRIPTION = "DESCRIPTION";
	public static final String COL_OWNERACCOUNTID = "OWNER_ACCOUNTID";
	public static final String COL_OWNERDISPLAYNAME = "OWNER_DISPLAYNAME";
	public static final String COL_JQL = "JQL";
	public static final List<String> COL_LIST = Arrays.asList(
				COL_ID,
				COL_NAME,
				COL_DESCRIPTION,
				COL_OWNERACCOUNTID,
				COL_OWNERDISPLAYNAME,
				COL_JQL
			);
	
	public CloudJiraFilters() {
		super(JiraFilters.class);
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
	protected List<ObjectData> getCSVRows(JiraFilters obj) {
		List<ObjectData> result = new ArrayList<>();
		for (JiraFilter filter: obj.getValues()) {
			List<String> list = Arrays.asList(
					filter.getId(),
					filter.getName(),
					filter.getDescription(),
					(filter.getOwner() != null)? filter.getOwner().getAccountId() : null,
					(filter.getOwner() != null)? filter.getOwner().getDisplayName() : null,
					filter.getJql()
					);
			result.add(new ObjectData(filter.getId(), filter.getName(), list));
		}
		return result;
	}

	@Override
	public List<JiraFilters> getObjects(Config config) throws Exception {
		MultivaluedMap<String, Object> header = getAuthenticationHeader(config);
		Map<String, Object> query = new HashMap<>();
		query.put("expand", "jql");
		List<JiraFilters> result = invokeRest(config, 
				"/rest/api/3/filter/search", HttpMethod.GET, header, query, null);
		return result;
	}

	@Override
	protected ObjectExport getObjectExport() {
		return new com.igsl.export.dc.JiraFilter();
	}

}
