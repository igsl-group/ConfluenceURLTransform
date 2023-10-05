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
import com.igsl.export.cloud.model.ConfluenceSearchResult;
import com.igsl.export.cloud.model.ConfluenceUser;
import com.igsl.export.cloud.model.ConfluenceUsers;

public class CloudConfluenceUsers extends BaseExport<ConfluenceUsers> {

	public CloudConfluenceUsers() {
		super(ConfluenceUsers.class);
	}

	@Override
	public Path exportObjects(Config config) throws Exception {
		MultivaluedMap<String, Object> header = getAuthenticationHeader(config);
		Map<String, Object> query = new HashMap<>();
		query.put("cql", "type=user");
		List<ConfluenceUsers> result = invokeRest(config, "/wiki/rest/api/search/user", HttpMethod.GET, header, query, null);
		Path p = getOutputPath(config);
		try (	FileWriter fw = new FileWriter(p.toFile());
				CSVPrinter printer = new CSVPrinter(fw, CSV.getCSVFormat())) {
			CSV.printRecord(printer, "ACCOUNTID", "EMAIL", "DISPLAYNAME", "PUBLICNAME");
			for (ConfluenceUsers users : result) {
				for (ConfluenceSearchResult r : users.getResults()) {
					ConfluenceUser user = r.getUser();
					if (user != null) {
						CSV.printRecord(printer, 
								user.getAccountId(), user.getEmail(), user.getDisplayName(), user.getPublicName());
					}
				}
			}
		}
		return p;
	}

	@Override
	public String getLimitParameter() {
		return "limit";
	}

	@Override
	public String getStartAtParameter() {
		return "start";
	}

}
