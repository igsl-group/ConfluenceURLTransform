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
import com.igsl.export.cloud.model.JiraAttachment;
import com.igsl.export.cloud.model.JiraIssue;
import com.igsl.export.cloud.model.JiraIssues;

public class CloudJiraAttachments extends BaseExport<JiraIssues> {

	public CloudJiraAttachments() {
		super(JiraIssues.class);
	}
	
	@Override
	public Path exportObjects(Config config) throws Exception {
		MultivaluedMap<String, Object> header = getAuthenticationHeader(config);
		Map<String, Object> query = new HashMap<>();
		query.put("fields", "attachment");
		List<JiraIssues> result = invokeRest(config, "/rest/api/3/search", HttpMethod.GET, header, query, null);
		Path p = getOutputPath(config);
		try (	FileWriter fw = new FileWriter(p.toFile());
				CSVPrinter printer = new CSVPrinter(fw, CSV.getCSVFormat())) {
			CSV.printRecord(printer, 
					"ATTACHMENTID", 
					"FILENAME", 
					"MIMETYPE", 
					"SIZE", 
					"AUTHOR_DISPLAYNAME",
					"AUTHOR_ACCOUNTID",
					"AUTHOR_NAME");
			for (JiraIssues issues : result) {
				for (JiraIssue issue : issues.getIssues()) {
					if (issue.getFields().getAttachment() != null) {
						for (JiraAttachment attachment : issue.getFields().getAttachment()) {
							CSV.printRecord(printer, 
									attachment.getId(),
									attachment.getFilename(),
									attachment.getMimeType(),
									attachment.getSize(),
									attachment.getAuthor().getDisplayName(),
									attachment.getAuthor().getAccountId(),
									attachment.getAuthor().getName());
						}
					}
				}
			}
		}
		return p;
	}

	@Override
	public String getLimitParameter() {
		return "maxResults";
	}

	@Override
	public String getStartAtParameter() {
		return "startAt";
	}

}
