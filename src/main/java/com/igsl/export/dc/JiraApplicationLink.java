package com.igsl.export.dc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVRecord;

public class JiraApplicationLink extends ObjectExport {

	private static final String SQL = 
			"SELECT SUBSTR(a.property_key,16,36) as APPKEY, " + 
			"b.propertyvalue as NAME " +   
			"FROM propertyentry a " + 
			"join propertystring b on a.id = b.id " +  
			"where a.property_key like 'applinks.admin%name'";
	public static final String COL_APPKEY = "APPKEY";
	public static final String COL_NAME = "NAME";
	public static final List<String> COL_LIST = Arrays.asList(COL_APPKEY, COL_NAME);
	
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
			String appKey = rs.getString(1);
			String name = rs.getString(2);
			return Arrays.asList(appKey, name);
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
		return name;
	}

	@Override
	protected String getObjectId(CSVRecord r) throws Exception {
		String appKey = r.get(COL_APPKEY);
		return appKey;
	}
}
