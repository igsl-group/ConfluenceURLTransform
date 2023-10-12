package com.igsl.export.dc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVRecord;

import com.igsl.ObjectData;

public class JiraSLA extends ObjectExport {

	private static final String SQL = 
			"SELECT t.ID, t.NAME, t.SERVICE_DESK_ID, p.PKEY AS PROJECTKEY " + 
			"FROM AO_54307E_TIMEMETRIC t " + 
			"JOIN AO_54307E_VIEWPORT v ON v.ID = t.SERVICE_DESK_ID " + 
			"JOIN PROJECT p ON p.ID = v.PROJECT_ID";
	public static final String COL_ID = "ID";
	public static final String COL_NAME = "NAME";
	public static final String COL_SERVICEDESKID = "SERVICE_DESK_ID";
	public static final String COL_PROJECTKEY = "PROJECTKEY";
	public static final List<String> COL_LIST = Arrays.asList(COL_ID, COL_NAME, COL_SERVICEDESKID, COL_PROJECTKEY);
	
	private PreparedStatement ps;
	private ResultSet rs;

	@Override
	public List<String> getHeaders() {
		return COL_LIST;
	}

	@Override
	public void startGetObjects() throws Exception {
		Connection conn = config.getConnections().getJiraConnection();
		ps = conn.prepareStatement(SQL);
		rs = ps.executeQuery();
	}

	@Override
	public List<String> getNextObject() throws Exception {
		if (rs.next()) {
			String id = rs.getString(1);
			String name = rs.getString(2);
			String serviceDeskId = rs.getString(3);
			String projectKey = rs.getString(4);
			return Arrays.asList(id, name, serviceDeskId, projectKey);
		}
		return null;
	}

	@Override
	public void stopGetObjects() throws Exception {
		close(rs);
		close(ps);
	}

	@Override
	protected String getObjectKey(CSVRecord r) throws Exception {
		String name = r.get(COL_NAME);
		String projectKey = r.get(COL_PROJECTKEY);
		return ObjectData.createUniqueKey(projectKey, name);
	}

	@Override
	protected String getObjectId(CSVRecord r) throws Exception {
		String id = r.get(COL_ID);
		return id;
	}
}
