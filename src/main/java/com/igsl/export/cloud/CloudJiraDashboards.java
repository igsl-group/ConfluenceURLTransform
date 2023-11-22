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
import com.igsl.export.cloud.model.JiraDashboard;
import com.igsl.export.cloud.model.JiraDashboards;
import com.igsl.export.dc.ObjectExport;

public class CloudJiraDashboards extends BaseExport<JiraDashboards> {

	public static final String COL_ID = "ID";
	public static final String COL_NAME = "NAME";
	public static final String COL_ACCOUNTID = "ACCOUNTID";
	public static final String COL_OWNERDISPLAYNAME = "OWNER_DISPLAYNAME";
	public static final List<String> COL_LIST = Arrays.asList(
				COL_ID,
				COL_NAME,
				COL_ACCOUNTID,
				COL_OWNERDISPLAYNAME
			);
	
	public CloudJiraDashboards() {
		super(JiraDashboards.class);
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
	protected List<ObjectData> getCSVRows(JiraDashboards obj) {
		List<ObjectData> result = new ArrayList<>();
		for (JiraDashboard board : obj.getDashboards()) {
			List<String> list = Arrays.asList(
					board.getId(),
					board.getName(),
					(board.getOwner() != null)? board.getOwner().getAccountId() : null,
					(board.getOwner() != null)? board.getOwner().getDisplayName() : null
					);
			result.add(new ObjectData(board.getId(), board.getName(), COL_LIST, list));
		}
		return result;
	}

	@Override
	protected List<JiraDashboards> _getObjects(Config config) throws Exception {
		MultivaluedMap<String, Object> header = getAuthenticationHeader(config);
		Map<String, Object> query = new HashMap<>();
		List<JiraDashboards> result = invokeRest(config, 
				"/rest/api/3/dashboard", HttpMethod.GET, header, query, null);
		return result;
	}

	@Override
	protected ObjectExport getObjectExport() {
		return new com.igsl.export.dc.JiraDashboard();
	}

}
