package com.igsl.export.cloud;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igsl.ObjectData;
import com.igsl.config.Config;
import com.igsl.export.cloud.model.ConfluencePage;
import com.igsl.export.cloud.model.ConfluencePageTemplate;
import com.igsl.export.cloud.model.ConfluencePageTemplates;
import com.igsl.export.cloud.model.ConfluencePages;
import com.igsl.export.cloud.model.ConfluenceSpace;
import com.igsl.export.cloud.model.ConfluenceSpaces;
import com.igsl.export.dc.ObjectExport;

public class CloudConfluencePageTemplates extends BaseExport<ConfluencePageTemplates> {

	private static final Logger LOGGER = LogManager.getLogger(CloudConfluencePageTemplates.class);
	public static final String COL_ID = "ID";
	public static final String COL_TITLE = "TITLE";
	public static final List<String> COL_LIST = Arrays.asList(COL_ID, COL_TITLE);
	
	public CloudConfluencePageTemplates() {
		super(ConfluencePageTemplates.class);
	}
	
	@Override
	public String getLimitParameter() {
		return "limit";
	}

	@Override
	public String getStartAtParameter() {
		return "start";
	}

	@Override
	protected List<String> getCSVHeaders() {
		return COL_LIST;
	}

	@Override
	protected List<ObjectData> getCSVRows(ConfluencePageTemplates obj) {
		List<ObjectData> result = new ArrayList<>();
		for (ConfluencePageTemplate page : obj.getResults()) {
			List<String> list = Arrays.asList(
					page.getTemplateId(), page.getName(),
					page.getDescription(), 
					page.getReferencingBlueprint(),
					page.getBody().getStorage().getValue());
			String uniqueKey = page.getName();
			result.add(new ObjectData(page.getTemplateId(), uniqueKey, COL_LIST, list));
		}
		return result;
	}

	@Override
	public List<ConfluencePageTemplates> getObjects(Config config) throws Exception {
		MultivaluedMap<String, Object> header = getAuthenticationHeader(config);
		Map<String, Object> query = new HashMap<>();
		query.put("expand", "body");
		List<ConfluencePageTemplates> result = invokeRest(
				config, "/wiki/rest/api/template/page", HttpMethod.GET, header, query, null);
		// Filter ConfluencePageTemaplte with non-empty referencingBlueprint
		// Those are modified default blueprints
		for (ConfluencePageTemplates templates : result) {
			List<ConfluencePageTemplate> toRemove = new ArrayList<>();
			for (ConfluencePageTemplate template : templates.getResults()) {
				if (template.getReferencingBlueprint() != null && 
					!template.getReferencingBlueprint().isBlank()) {
					toRemove.add(template);
				}
			}
			templates.getResults().removeAll(toRemove);
		}
		return result;
	}

	@Override
	protected ObjectExport getObjectExport() {
		return new com.igsl.export.dc.ConfluencePageTemplate();
	}
}
