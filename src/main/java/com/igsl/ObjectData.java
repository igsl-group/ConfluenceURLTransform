package com.igsl;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
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
	private CSVRecord csvRecord;
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
	public ObjectData(String id, String uniqueKey, CSVRecord csvRecord) {
		this.id = id;
		this.uniqueKey = uniqueKey;
		this.csvRecord = csvRecord;
		this.mapped = false;
	}
	public ObjectData(String id, String uniqueKey, List<String> columns, List<String> data) {
		this.id = id;
		this.uniqueKey = uniqueKey;
		// TODO Worth doing it this way?
		StringBuilder buffer = new StringBuilder();
		try (CSVPrinter printer = new CSVPrinter(buffer, CSV.getCSVWriteFormat(columns))) {
			CSV.printRecord(printer, data);
		} catch (IOException ioex) {
			Log.error(LOGGER, "Error formating data into CSVRecord", ioex);
		}
		try (	StringReader reader = new StringReader(buffer.toString()); 
				CSVParser parser = new CSVParser(reader, CSV.getCSVReadFormat())) {
			this.csvRecord = parser.getRecords().get(0);
		} catch (IOException ioex) {
			Log.error(LOGGER, "Error formating data into CSVRecord", ioex);
		}
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
	public CSVRecord getCsvRecord() {
		return csvRecord;
	}
}
