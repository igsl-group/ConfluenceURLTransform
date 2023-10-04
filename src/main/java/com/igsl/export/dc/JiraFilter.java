package com.igsl.export.dc;

import java.io.FileWriter;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.csv.CSVPrinter;

import com.igsl.CSV;
import com.igsl.config.Config;

public class JiraFilter extends ObjectExport {

	private static final String SQL = 
			"SELECT id, filtername, description, authorname, groupname, projectid, reqcontent FROM searchrequest";
	
	@Override
	public Path exportObjects(Config config) throws Exception {
		Connection conn = config.getConnections().getJiraConnection();
		Path p = getOutputPath(config);
		try (	FileWriter fw = new FileWriter(p.toFile(), false);
				CSVPrinter printer = new CSVPrinter(fw, CSV.getCSVFormat());
				PreparedStatement ps = conn.prepareStatement(SQL)) {
			CSV.printRecord(printer, "ID", "FILTERNAME", "DESCRIPTION", "AUTHOR", "GROUP", "PROJECT", "CONTENT");
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					String id = rs.getString(1);
					String filterName = rs.getString(2);
					String description = rs.getString(3);
					String authorName = rs.getString(4);
					String groupName = rs.getString(5);
					String projectId = rs.getString(6);
					String content = rs.getString(7);
					CSV.printRecord(printer, id, filterName, description, authorName, groupName, projectId, content);
				}
			}
		}
		return p;
	}

}
