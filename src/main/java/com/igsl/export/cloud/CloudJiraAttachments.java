package com.igsl.export.cloud;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MultivaluedMap;

import com.igsl.ObjectData;
import com.igsl.config.Config;
import com.igsl.export.cloud.model.JiraAttachment;
import com.igsl.export.cloud.model.JiraIssue;
import com.igsl.export.cloud.model.JiraIssues;
import com.igsl.export.dc.ObjectExport;

public class CloudJiraAttachments extends BaseExport<JiraIssues> {

	public static final String COL_ATTACHMENTID = "ATTACHMENTID";
	public static final String COL_FILENAME = "FILENAME";
	public static final String COL_MIMETYPE = "MIMETYPE"; 
	public static final String COL_SIZE = "SIZE";
	public static final String COL_AUTHORDISPLAYNAME = "AUTHOR_DISPLAYNAME";
	public static final String COL_AUTHORACCOUNTID = "AUTHOR_ACCOUNTID";
	public static final String COL_AUTHORNAME = "AUTHOR_NAME";
	public static final List<String> COL_LIST = 
			Arrays.asList(
				COL_ATTACHMENTID,
				COL_FILENAME,
				COL_MIMETYPE,
				COL_SIZE,
				COL_AUTHORDISPLAYNAME,
				COL_AUTHORACCOUNTID,
				COL_AUTHORNAME);
	
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
		return COL_LIST;
	}

	@Override
	protected List<ObjectData> getCSVRows(JiraIssues obj) {
		List<ObjectData> result = new ArrayList<>();
		for (JiraIssue issue : obj.getIssues()) {
			if (issue.getFields().getAttachment() != null) {
				for (JiraAttachment attachment : issue.getFields().getAttachment()) {
					List<String> list = Arrays.asList(
							attachment.getId(),
							attachment.getFilename(),
							attachment.getMimeType(),
							Long.toString(attachment.getSize()),
							attachment.getAuthor().getDisplayName(),
							attachment.getAuthor().getAccountId(),
							attachment.getAuthor().getName());
					String uniqueKey = ObjectData.createUniqueKey(issue.getKey(), attachment.getFilename());
					result.add(new ObjectData(attachment.getId(), uniqueKey, COL_LIST, list));
				}
			}
		}
		return result;
	}

	@Override
	protected List<JiraIssues> _getObjects(Config config) throws Exception {
		MultivaluedMap<String, Object> header = getAuthenticationHeader(config);
		Map<String, Object> query = new HashMap<>();
		query.put("fields", "attachment");
		List<JiraIssues> result = invokeRest(config, "/rest/api/3/search", HttpMethod.GET, header, query, null);
		return result;
	}

	@Override
	protected ObjectExport getObjectExport() {
		return new com.igsl.export.dc.JiraAttachment();
	}

}
