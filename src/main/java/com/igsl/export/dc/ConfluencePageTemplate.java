package com.igsl.export.dc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVRecord;

import com.igsl.config.SQLConfig;

/**
 * Note: 
 * This includes both page templates and modified blueprints.
 * Blueprints are essentially default page templates. 
 * Once modified they become page templates (except for some flags so the UI list them as modified blueprints).
 * On Cloud, modified blueprints are treated as page templates.
 */
public class ConfluencePageTemplate extends ObjectExport {

	private static final String SQL = SQLConfig.getInstance().getSQL(ConfluencePageTemplate.class);
//			"SELECT TEMPLATEID, TEMPLATENAME, TEMPLATEDESC, CONTENT, " + 
//			"CASE WHEN MODULEKEY IS NOT NULL THEN 1 WHEN REFMODULEKEY IS NOT NULL THEN 1 ELSE 0 END AS BLUEPRINT " + 
//			"FROM PAGETEMPLATES " + 
//			"WHERE PREVVER IS NULL";
	public static final String COL_ID = "ID";
	public static final String COL_NAME = "NAME";
	public static final String COL_DESCRIPTION = "DESCRIPTION";
	public static final String COL_CONTENT = "CONTENT";
	public static final String COL_BLUEPRINT = "BLUEPRINT";
	public static final List<String> COL_LIST = Arrays.asList(
			COL_ID, COL_NAME, COL_DESCRIPTION, COL_CONTENT, COL_BLUEPRINT);
	
	private PreparedStatement ps;
	private ResultSet rs;

	@Override
	public List<String> getHeaders() {
		return COL_LIST;
	}

	@Override
	public void startGetObjects() throws Exception {
		Connection conn = config.getConnections().getConfluenceConnection();
		ps = conn.prepareStatement(SQL);
		rs = ps.executeQuery();
	}

	@Override
	public List<String> getNextObject() throws Exception {
		if (rs.next()) {
			String id = rs.getString(1);
			String name = rs.getString(2);
			String desc = rs.getString(3);
			String content = rs.getString(4);
			String blueprint = rs.getString(5);
			return Arrays.asList(id, name, desc, content, blueprint);
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
		String id = r.get(COL_ID);
		return id;
	}
}
