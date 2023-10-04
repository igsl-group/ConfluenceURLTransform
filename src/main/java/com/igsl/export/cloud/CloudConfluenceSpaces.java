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
import com.igsl.export.cloud.model.ConfluenceSpace;
import com.igsl.export.cloud.model.ConfluenceSpaces;

public class CloudConfluenceSpaces extends CloudExport<ConfluenceSpaces> {

	public CloudConfluenceSpaces() {
		super(ConfluenceSpaces.class);
	}

	@Override
	public Path exportObjects(Config config) throws Exception {
		MultivaluedMap<String, Object> header = getAuthenticationHeader(config);
		Map<String, Object> query = new HashMap<>();
		query.put("limit", 1);
		List<ConfluenceSpaces> result = invokeRest(config, "/wiki/api/v2/spaces", HttpMethod.GET, header, query, null);
		Path p = getOutputPath(config);
		try (	FileWriter fw = new FileWriter(p.toFile());
				CSVPrinter printer = new CSVPrinter(fw, CSV.getCSVFormat())) {
			CSV.printRecord(printer, "ID", "KEY", "NAME");
			for (ConfluenceSpaces spaces : result) {
				for (ConfluenceSpace space : spaces.getResults()) {
					CSV.printRecord(printer, space.getId(), space.getKey(), space.getName());
				}
			}
		}
		return p;
	}

}
