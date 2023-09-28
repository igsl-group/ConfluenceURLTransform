package com.igsl.export;

import java.io.FileWriter;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import com.igsl.config.Config;

public class JiraRapidBoard extends ObjectExport {

	private static final String SQL = 
			"SELECT rv.ID AS BOARDID, rv.NAME AS BOARDNAME " + 
			"FROM AO_60DB71_RAPIDVIEW rv";

	@Override
	public Path exportObjects(Config config) throws Exception {
		Connection conn = config.getConnections().getJiraConnection();
		Path p = getOutputPath(config);
		try (	FileWriter fw = new FileWriter(p.toFile(), false);
				CSVPrinter printer = new CSVPrinter(fw, CSVFormat.DEFAULT);
				PreparedStatement ps = conn.prepareStatement(SQL)) {
			printer.printRecord("BOARDID", "BOARDNAME");
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					String boardId = rs.getString(1);
					String boardName = rs.getString(2);
					printer.printRecord(boardId, boardName);
				}
			}
		}
		return p;
	}

}
