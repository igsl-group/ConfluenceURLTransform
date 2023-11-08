package com.igsl.export.dc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVRecord;

import com.igsl.config.SQLConfig;

public class JiraDashboard extends ObjectExport {

	private static final String SQL = SQLConfig.getInstance().getSQL(JiraDashboard.class);
//	private static final String SQL = "SELECT ID, PAGENAME, DESCRIPTION FROM PortalPage";
	public static final String COL_ID = "ID";
	public static final String COL_PAGENAME = "PAGENAME";
	public static final String COL_DESCRIPTION = "DESCRIPTION";
	public static final List<String> COL_LIST = Arrays.asList(
			COL_ID, COL_PAGENAME, COL_DESCRIPTION);
	
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
			String pageName = rs.getString(2);
			String description = rs.getString(3);
			return Arrays.asList(id, pageName, description);
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
		String pageName = r.get(COL_PAGENAME);
		return pageName;
	}

	@Override
	protected String getObjectId(CSVRecord r) throws Exception {
		String id = r.get(COL_ID);
		return id;
	}
}
