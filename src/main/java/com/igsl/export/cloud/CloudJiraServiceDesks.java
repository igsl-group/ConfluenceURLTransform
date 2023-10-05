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
import com.igsl.export.cloud.model.JiraServiceDesk;
import com.igsl.export.cloud.model.JiraServiceDesks;

public class CloudJiraServiceDesks extends BaseExport<JiraServiceDesks> {

	public CloudJiraServiceDesks() {
		super(JiraServiceDesks.class);
	}
	
	@Override
	public Path exportObjects(Config config) throws Exception {
		MultivaluedMap<String, Object> header = getAuthenticationHeader(config);
		Map<String, Object> query = new HashMap<>();
		List<JiraServiceDesks> result = invokeRest(config, 
				"/rest/servicedeskapi/servicedesk", HttpMethod.GET, header, query, null);
		Path p = getOutputPath(config);
		try (	FileWriter fw = new FileWriter(p.toFile());
				CSVPrinter printer = new CSVPrinter(fw, CSV.getCSVFormat())) {
			CSV.printRecord(printer, "ID", "PROJECTID", "PROJECTNAME", "PROJECTKEY");
			for (JiraServiceDesks desks : result) {
				for (JiraServiceDesk desk : desks.getValues()) {
					CSV.printRecord(printer, 
							desk.getId(),
							desk.getProjectId(),
							desk.getProjectName(),
							desk.getProjectKey());
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
