package com.igsl.export.dc;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igsl.CSV;
import com.igsl.Log;
import com.igsl.ObjectData;
import com.igsl.config.Config;

public abstract class ObjectExport {
	private static final Logger LOGGER = LogManager.getLogger(ObjectExport.class);
	public static final String IDENTIFIER_DELIMITER = "-";
	
	protected Config config;
	public void setConfig(Config config) {
		this.config = config;
	}
	
	protected Path getOutputPath(String dir) throws IOException {
		return Paths.get(dir, this.getClass().getSimpleName() + ".csv");
	}
	protected Path getOutputPath(Config config) throws IOException {
		return getOutputPath(config.getOutputDirectory().toFile().getAbsolutePath());
	}
	
	protected void close(Statement c) {
		if (c != null) {
			try {
				c.close();
			} catch (Exception ex) {
				Log.error(LOGGER, "Error closing statement", ex);
			}
		}
	}
	protected void close(ResultSet r) {
		if (r != null) {
			try {
				r.close();
			} catch (Exception ex) {
				Log.error(LOGGER, "Error closing resultset", ex);
			}
		}
	}
	
	/**
	 * Get CSV headers
	 */
	public abstract List<String> getHeaders();
	
	/**
	 * Start reading objects
	 */
	public abstract void startGetObjects() throws Exception;
	
	/**
	 * Return next object. Return null if no more items.
	 */
	public abstract List<String> getNextObject() throws Exception;
	
	/**
	 * Clean up reading objects. This is always invoked.
	 */
	public abstract void stopGetObjects() throws Exception;
	
	public final Path exportObjects() throws Exception {
		Path p = getOutputPath(config);
		try (	FileWriter fw = new FileWriter(p.toFile());
				CSVPrinter printer = new CSVPrinter(fw, CSV.getCSVWriteFormat(getHeaders()))) {
			startGetObjects();
			List<String> row = null;
			do {
				row = getNextObject();
				if (row != null) {
					CSV.printRecord(printer, row);
				}
			} while (row != null);
		} finally {
			stopGetObjects();
		}
		return p;
	}
	
	/**
	 * Get unique identifier from CSVRecord
	 */
	protected abstract String getObjectKey(CSVRecord r) throws Exception;
	
	/**
	 * Get internal ID from CSVRecord
	 */
	protected abstract String getObjectId(CSVRecord r) throws Exception;
	
	public final List<ObjectData> readObjects(Path dir) throws Exception {
		List<ObjectData> result = new ArrayList<>();
		Path p = getOutputPath(dir.toFile().getAbsolutePath());
		try (	FileReader fr = new FileReader(p.toFile()); 
				CSVParser parser = new CSVParser(fr, CSV.getCSVReadFormat())) {
			List<CSVRecord> list = parser.getRecords();
			for (CSVRecord r : list) {
				ObjectData od = new ObjectData(getObjectId(r), getObjectKey(r), r);
				Log.debug(LOGGER, "ObjectData: " + this.getClass().getSimpleName() + ": " + od.toString());
				result.add(od);
			}
		}
		return result;
	}
}
