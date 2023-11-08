package com.igsl.export.dc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVRecord;

import com.igsl.config.SQLConfig;

public class JiraCustomerPortal extends ObjectExport {

	private static final String SQL = SQLConfig.getInstance().getSQL(JiraCustomerPortal.class);
//			"SELECT ao.ID, ao.NAME, ao.DESCRIPTION, p.PKEY, ao.PROJECT_ID " + 
//			"FROM AO_54307E_VIEWPORT ao " + 
//			"JOIN PROJECT p ON p.ID = ao.PROJECT_ID ";
	public static final String COL_ID = "ID";
	public static final String COL_NAME = "NAME";
	public static final String COL_DESCRIPTION = "DESCRIPTION";
	public static final String COL_PROJECTKEY = "PROJECTKEY";
	public static final String COL_PROJECTID = "PROJECTID";
	public static final List<String> COL_LIST = Arrays.asList(
			COL_ID, COL_NAME, COL_DESCRIPTION, COL_PROJECTKEY, COL_PROJECTID);
	
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
			String description = rs.getString(3);
			String projectKey = rs.getString(4);
			String projectId = rs.getString(5);
			return Arrays.asList(id, name, description, projectKey, projectId);
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
		String projectKey = r.get(COL_PROJECTKEY);
		return projectKey;
	}

	@Override
	protected String getObjectId(CSVRecord r) throws Exception {
		String id = r.get(COL_ID);
		return id;
	}
}
