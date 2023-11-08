package com.igsl.export.dc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVRecord;

import com.igsl.config.SQLConfig;

public class JiraRapidBoard extends ObjectExport {

	private static final String SQL = SQLConfig.getInstance().getSQL(JiraRapidBoard.class);
//			"SELECT rv.ID AS BOARDID, rv.NAME AS BOARDNAME " + 
//			"FROM AO_60DB71_RAPIDVIEW rv";
	public static final String COL_BOARDID = "BOARDID";
	public static final String COL_BORADNAME = "BOARDNAME";
	public static final List<String> COL_LIST = Arrays.asList(COL_BOARDID, COL_BORADNAME);
	
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
			String boardId = rs.getString(1);
			String boardName = rs.getString(2);
			return Arrays.asList(boardId, boardName);
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
		String boardName = r.get(COL_BORADNAME);
		return boardName;
	}

	@Override
	protected String getObjectId(CSVRecord r) throws Exception {
		String boardId = r.get(COL_BOARDID);
		return boardId;
	}
}
