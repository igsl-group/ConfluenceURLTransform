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
import com.igsl.export.cloud.model.ConfluencePage;
import com.igsl.export.cloud.model.ConfluencePages;

public class CloudConfluencePages extends CloudExport<ConfluencePages> {

	public CloudConfluencePages() {
		super(ConfluencePages.class);
	}

	@Override
	public Path exportObjects(Config config) throws Exception {
		MultivaluedMap<String, Object> header = getAuthenticationHeader(config);
		Map<String, Object> query = new HashMap<>();
		query.put("limit", 1);
		query.put("body-format", "storage");
		List<ConfluencePages> result = invokeRest(config, "/wiki/api/v2/pages", HttpMethod.GET, header, query, null);
		Path p = getOutputPath(config);
		try (	FileWriter fw = new FileWriter(p.toFile());
				CSVPrinter printer = new CSVPrinter(fw, CSV.getCSVFormat())) {
			CSV.printRecord(printer, "ID", "TITLE", "SPACEID", "BODY");
			for (ConfluencePages pages : result) {
				for (ConfluencePage page : pages.getResults()) {
					CSV.printRecord(printer, 
							page.getId(), page.getTitle(), page.getSpaceId(), 
							page.getBody().getStorage().getValue());
				}
			}
		}
		return p;
	}

}
