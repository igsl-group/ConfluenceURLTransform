package com.igsl.export;

import java.io.FileWriter;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import com.igsl.config.Config;

public class JiraCustomerPortal extends ObjectExport {

	private static final String SQL = "SELECT ID, NAME, DESCRIPTION, AO.KEY, PROJECT_ID FROM AO_54307E_VIEWPORT AO";
	
	@Override
	public Path exportObjects(Config config) throws Exception {
		Connection conn = config.getConnections().getJiraConnection();
		Path p = getOutputPath(config);
		try (	FileWriter fw = new FileWriter(p.toFile(), false);
				CSVPrinter printer = new CSVPrinter(fw, CSVFormat.DEFAULT);
				PreparedStatement ps = conn.prepareStatement(SQL)) {
			printer.printRecord("ID", "NAME", "DESCRIPTION", "PROJECTKEY", "PROJECTID");
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					String id = rs.getString(1);
					String name = rs.getString(2);
					String description = rs.getString(3);
					String projectKey = rs.getString(4);
					String projectId = rs.getString(5);
					printer.printRecord(id, name, description, projectKey, projectId);
				}
			}
		}
		return p;
	}

}
