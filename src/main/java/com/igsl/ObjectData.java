package com.igsl;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectData {
	private static final ObjectMapper OM = new ObjectMapper();
	private static final Logger LOGGER = LogManager.getLogger(ObjectData.class);
	private static final String DELIMITER = "-";
	private String id;
	private String uniqueKey;
	private List<String> csvData;
	@JsonIgnore
	private boolean mapped;
	public static String createUniqueKey(String... parts) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String part : parts) {
			if (!first) {
				sb.append(DELIMITER);
			} else {
				first = false;
			}
			sb.append(part);
		}
		return sb.toString();
	}
	public ObjectData(String id, String uniqueKey, List<String> csvData) {
		this.id = id;
		this.uniqueKey = uniqueKey;
		this.csvData = new ArrayList<>();
		this.csvData.addAll(csvData);
		this.mapped = false;
	}
	@Override
	public String toString() {
		try {
			return OM.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			Log.error(LOGGER, "Error serializing ObjectData", e);
			return null;
		}
	}
	public List<String> getCsvData() {
		return csvData;
	}
	public String getId() {
		return id;
	}
	public String getUniqueKey() {
		return uniqueKey;
	}
	public boolean isMapped() {
		return mapped;
	}
	public void setMapped(boolean mapped) {
		this.mapped = mapped;
	}
}
