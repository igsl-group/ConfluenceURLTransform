package com.igsl.export.cloud;

import java.io.FileWriter;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.csv.CSVPrinter;

import com.igsl.CSV;
import com.igsl.config.Config;
import com.igsl.export.cloud.model.JiraDashboard;
import com.igsl.export.cloud.model.JiraDashboards;

public class CloudJiraDashboards extends BaseExport<JiraDashboards> {

	public CloudJiraDashboards() {
		super(JiraDashboards.class);
	}

	@Override
	public String getLimitParameter() {
		return "maxResults";
	}

	@Override
	public String getStartAtParameter() {
		return "startAt";
	}

	@Override
	public Path exportObjects(Config config) throws Exception {
		MultivaluedMap<String, Object> header = getAuthenticationHeader(config);
		Map<String, Object> query = new HashMap<>();
		List<JiraDashboards> result = invokeRest(config, 
				"/rest/api/3/dashboard", HttpMethod.GET, header, query, null);
		Path p = getOutputPath(config);
		try (	FileWriter fw = new FileWriter(p.toFile());
				CSVPrinter printer = new CSVPrinter(fw, CSV.getCSVFormat())) {
			CSV.printRecord(printer, "ID", "NAME", "OWNER_ACCOUNTID", "OWNER_DISPLAYNAME");
			for (JiraDashboards boards : result) {
				for (JiraDashboard board : boards.getDashboards()) {
					CSV.printRecord(printer, 
							board.getId(),
							board.getName(),
							(board.getOwner() != null)? board.getOwner().getAccountId() : null,
							(board.getOwner() != null)? board.getOwner().getDisplayName() : null);
				}
			}
		}
		return p;
	}

}
