package com.igsl.export.dc;

import java.io.FileWriter;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.csv.CSVPrinter;

import com.igsl.CSV;
import com.igsl.config.Config;

public class ConfluencePage extends ObjectExport {

	private static final String SQL = 
			"SELECT c.CONTENTID, c.TITLE, s.SPACEKEY " + 
			"FROM CONTENT c " + 
			"JOIN SPACES s ON s.SPACEID = c.SPACEID " + 
			"WHERE c.CONTENTTYPE = 'PAGE'";
	
	@Override
	public Path exportObjects(Config config) throws Exception {
		Connection conn = config.getConnections().getConfluenceConnection();
		Path p = getOutputPath(config);
		try (	FileWriter fw = new FileWriter(p.toFile(), false);
				CSVPrinter printer = new CSVPrinter(fw, CSV.getCSVFormat());
				PreparedStatement ps = conn.prepareStatement(SQL)) {
			CSV.printRecord(printer, "PAGEID", "PAGENAME", "SPACEKEY");
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					String pageId = rs.getString(1);
					String pageName = rs.getString(2);
					String spaceKey = rs.getString(3);
					CSV.printRecord(printer, pageId, pageName, spaceKey);
				}
			}
		}
		return p;
	}

}
