package com.igsl.export.dc;

import java.io.FileWriter;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.csv.CSVPrinter;

import com.igsl.CSV;
import com.igsl.config.Config;

public class JiraProject extends ObjectExport {

	private static final String SQL = "SELECT ID, PKEY, PNAME FROM PROJECT";
	
	@Override
	public Path exportObjects(Config config) throws Exception {
		Connection conn = config.getConnections().getJiraConnection();
		Path p = getOutputPath(config);
		try (	FileWriter fw = new FileWriter(p.toFile(), false);
				CSVPrinter printer = new CSVPrinter(fw, CSV.getCSVFormat());
				PreparedStatement ps = conn.prepareStatement(SQL)) {
			CSV.printRecord(printer, "ID", "PROJECTKEY", "PROJECTNAME");
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					String id = rs.getString(1);
					String projectKey = rs.getString(2);
					String projectName = rs.getString(3);
					CSV.printRecord(printer, id, projectKey, projectName);
				}
			}
		}
		return p;
	}

}
