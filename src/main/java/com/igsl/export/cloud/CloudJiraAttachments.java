package com.igsl.export.cloud;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MultivaluedMap;

import com.igsl.config.Config;
import com.igsl.export.cloud.model.JiraAttachment;
import com.igsl.export.cloud.model.JiraIssue;
import com.igsl.export.cloud.model.JiraIssues;

public class CloudJiraAttachments extends BaseExport<JiraIssues> {

	public CloudJiraAttachments() {
		super(JiraIssues.class);
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
	protected List<String> getCSVHeaders() {
		return Arrays.asList(
				"ATTACHMENTID", 
				"FILENAME", 
				"MIMETYPE", 
				"SIZE", 
				"AUTHOR_DISPLAYNAME",
				"AUTHOR_ACCOUNTID",
				"AUTHOR_NAME"
				);
	}

	@Override
	protected List<List<Object>> getRows(JiraIssues obj) {
		List<List<Object>> result = new ArrayList<>();
		for (JiraIssue issue : obj.getIssues()) {
			if (issue.getFields().getAttachment() != null) {
				for (JiraAttachment attachment : issue.getFields().getAttachment()) {
					result.add(Arrays.asList(
							attachment.getId(),
							attachment.getFilename(),
							attachment.getMimeType(),
							attachment.getSize(),
							attachment.getAuthor().getDisplayName(),
							attachment.getAuthor().getAccountId(),
							attachment.getAuthor().getName()));
				}
			}
		}
		return result;
	}

	@Override
	public List<JiraIssues> getObjects(Config config) throws Exception {
		MultivaluedMap<String, Object> header = getAuthenticationHeader(config);
		Map<String, Object> query = new HashMap<>();
		query.put("fields", "attachment");
		List<JiraIssues> result = invokeRest(config, "/rest/api/3/search", HttpMethod.GET, header, query, null);
		return result;
	}

}
