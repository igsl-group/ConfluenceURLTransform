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
import com.igsl.export.cloud.model.ConfluencePageTemplate;
import com.igsl.export.cloud.model.ConfluencePageTemplates;
import com.igsl.export.dc.ObjectExport;

public class CloudConfluenceBlueprints extends BaseExport<ConfluencePageTemplates> {

	private static final Logger LOGGER = LogManager.getLogger(CloudConfluenceBlueprints.class);
	public static final String COL_ID = "ID";
	public static final String COL_TITLE = "NAME";
	public static final List<String> COL_LIST = Arrays.asList(COL_ID, COL_TITLE);
	
	public CloudConfluenceBlueprints() {
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
					page.getTemplateId(), page.getName(), page.getDescription(), 
					page.getTemplateType(),
					page.getReferencingBlueprint(),
					page.getBody().getStorage().getValue());
			String uniqueKey = page.getName();
			result.add(new ObjectData(page.getTemplateId(), uniqueKey, list));
		}
		return result;
	}

	@Override
	public List<ConfluencePageTemplates> getObjects(Config config) throws Exception {
		MultivaluedMap<String, Object> header = getAuthenticationHeader(config);
		Map<String, Object> query = new HashMap<>();
		query.put("expand", "body");
		List<ConfluencePageTemplates> result = new ArrayList<>();
		List<ConfluencePageTemplates> defaultTemplates = invokeRest(
				config, "/wiki/rest/api/template/blueprint", HttpMethod.GET, header, query, null);
		// No information can be used to distinguish between default and modfied copies
		// Rely on ID format to use modified copies over default ones
		// For duplicated ones, the first found will be used
		Map<String, ConfluencePageTemplate> map = new HashMap<>();
		for (ConfluencePageTemplates templates : defaultTemplates) {
			for (ConfluencePageTemplate template : templates.getResults()) {
				if (template.getTemplateId().contains("-")) {
					// Override only if not found
					if (!map.containsKey(template.getName())) {
						map.put(template.getName(), template);
					}
				} else {
					// Always override
					map.put(template.getName(), template);
				}
			}
		}
		ConfluencePageTemplates t = new ConfluencePageTemplates();
		t.setResults(new ArrayList<>(map.values()));
		result.add(t);
		return result;
	}

	@Override
	protected ObjectExport getObjectExport() {
		return new com.igsl.export.dc.ConfluenceBlueprint();
	}
}
