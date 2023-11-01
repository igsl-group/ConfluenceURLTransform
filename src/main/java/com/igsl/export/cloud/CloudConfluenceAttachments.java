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
import com.igsl.export.cloud.model.ConfluenceAttachment;
import com.igsl.export.cloud.model.ConfluenceAttachments;
import com.igsl.export.cloud.model.ConfluencePage;
import com.igsl.export.cloud.model.ConfluencePages;
import com.igsl.export.dc.ObjectExport;

public class CloudConfluenceAttachments extends BaseExport<ConfluenceAttachments> {

	public static final String COL_ID = "ID";
	public static final String COL_TITLE = "TITLE";
	public static final String COL_VERSION = "VERSION";
	public static final String COL_PAGE_TITLE = "PAGE_TITLE";
	public static final String COL_PAGE_VERSION = "PAGE_VERSION";
	public static final String COL_SPACE_ID = "SPACE_ID";
	public static final String COL_SPACE_KEY = "SPACE_KEY";
	public static final List<String> COL_LIST = 
			Arrays.asList(COL_ID, COL_TITLE, COL_VERSION, COL_PAGE_TITLE, COL_PAGE_VERSION, COL_SPACE_ID, COL_SPACE_KEY);
	
	public CloudConfluenceAttachments() {
		super(ConfluenceAttachments.class);
	}
	
	@Override
	public String getLimitParameter() {
		return "limit";
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
	protected List<ObjectData> getCSVRows(ConfluenceAttachments obj) {
		List<ObjectData> result = new ArrayList<>();
		for (ConfluenceAttachment attachment : obj.getResults()) {
			List<String> list = Arrays.asList(
					attachment.getId(), 
					attachment.getTitle(),
					Integer.toString(attachment.getVersion().getNumber()),
					attachment.getPage().getTitle(),
					Integer.toString(attachment.getPage().getVersion().getNumber()),
					attachment.getPage().getSpaceId(),
					attachment.getPage().getSpaceKey());
			String uniqueKey = ObjectData.createUniqueKey(
					attachment.getPage().getSpaceKey(),
					attachment.getPage().getTitle(),
					attachment.getTitle());
			result.add(new ObjectData(attachment.getId(), uniqueKey, COL_LIST, list));
		}
		return result;
	}

	@Override
	public List<ConfluenceAttachments> getObjects(Config config) throws Exception {
		MultivaluedMap<String, Object> header = getAuthenticationHeader(config);
		Map<String, Object> query = new HashMap<>();
		List<ConfluenceAttachments> result = invokeRest(config, 
				"/wiki/api/v2/attachments", HttpMethod.GET, header, query, null);
		// Resolve page
		CloudConfluencePages pagesExport = new CloudConfluencePages();
		List<ConfluencePages> pagesList = pagesExport.getObjects(config);
		for (ConfluenceAttachments attachments: result) {
			for (ConfluenceAttachment attachment : attachments.getResults()) {
				String pageId = attachment.getPageId();
				for (ConfluencePages pages : pagesList) {
					for (ConfluencePage page : pages.getResults()) {
						if (page.getId().equals(pageId)) {
							attachment.setPage(page);
							break;
						}
					}
				}
			}
		}
		return result;
	}

	@Override
	protected ObjectExport getObjectExport() {
		return new com.igsl.export.dc.ConfluenceAttachment();
	}
}
