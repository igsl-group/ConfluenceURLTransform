package com.igsl.export.cloud;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.commons.csv.CSVPrinter;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.igsl.CSV;
import com.igsl.Log;
import com.igsl.config.Config;
import com.igsl.export.cloud.model.Linked;
import com.igsl.export.cloud.model.Paged;

public abstract class BaseExport<T> {
	private static final Logger LOGGER = LogManager.getLogger(BaseExport.class);	
	public static final String SCHEME = "https://";
	public static final String ENCODDING = "ASCII";	
	private static final ObjectMapper OM = new ObjectMapper()
			.enable(SerializationFeature.INDENT_OUTPUT)
			.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
			.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	protected static final JacksonJsonProvider JACKSON_JSON_PROVIDER = 
			new JacksonJaxbJsonProvider()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
			.configure(SerializationFeature.INDENT_OUTPUT, true);
	
	protected final Class<T> templateClass;
	public BaseExport(Class<T> templateClass) {
		this.templateClass = templateClass;
	}
	
	@JsonIgnore
	public abstract String getLimitParameter();
	@JsonIgnore
	public abstract String getStartAtParameter();
	
	protected MultivaluedMap<String, Object> getAuthenticationHeader(Config config) throws Exception {
		MultivaluedMap<String, Object> result = new MultivaluedHashMap<>();
		String headerValue = "Basic " + 
				Base64.getEncoder().encodeToString(
						(config.getCloud().getUserName() + ":" + config.getCloud().getApiToken())
							.getBytes(ENCODDING));
		List<Object> values = new ArrayList<>();
		values.add(headerValue);
		result.put("Authorization", values);
		return result;
	}
	
	protected List<T> invokeRestCore(
			Config config, 
			String path, 
			String method, 
			MultivaluedMap<String, Object> headers, 
			Map<String, Object> queryParameters, 
			Object data,
			int... successStatuses) throws Exception {
		List<T> result = new ArrayList<>();
		Client client = ClientBuilder.newClient();
		client.register(JACKSON_JSON_PROVIDER);
		URI uri = new URI(SCHEME + config.getCloud().getDomain()).resolve(path);
		WebTarget target = client.target(uri);
		Log.debug(LOGGER, "uri: " + uri.toASCIIString() + " " + method);
		if (queryParameters != null) {
			for (Map.Entry<String, Object> query : queryParameters.entrySet()) {
				Log.debug(LOGGER, "query: " + query.getKey() + " = " + query.getValue());
				target = target.queryParam(query.getKey(), query.getValue());
			}
		}
		Builder builder = target.request();
		if (headers != null) {
			builder = builder.headers(headers);
			for (Map.Entry<String, List<Object>> header : headers.entrySet()) {
				for (Object o : header.getValue()) {
					Log.debug(LOGGER, "header: " + header.getKey() + " = " + o);
				}
			}
		}
		Response response = null;
		switch (method) {
		case HttpMethod.DELETE:
			response = builder.delete();
			break;
		case HttpMethod.GET:
			response = builder.get();
			break;
		case HttpMethod.HEAD:
			response = builder.head();
			break;
		case HttpMethod.OPTIONS:
			response = builder.options();
			break;
		case HttpMethod.POST:
			response = builder.post(Entity.entity(data, MediaType.APPLICATION_JSON));
			break;
		case HttpMethod.PUT:
			response = builder.put(Entity.entity(data, MediaType.APPLICATION_JSON));
			break;
		default:
			throw new Exception("Invalid method \"" + method + "\"");
		}
		boolean success = false;
		int status = response.getStatus();
		Log.debug(LOGGER, uri.toASCIIString() + " status = " + status);
		if (successStatuses != null && successStatuses.length != 0) {
			for (int successStatus : successStatuses) {
				if (successStatus == status) {
					success = true;
					break;
				}
			}
		} else {
			success = (status == HttpStatus.SC_OK);
		}
		String body = response.readEntity(String.class);
		Log.debug(LOGGER, uri.toASCIIString() + " body = " + body);
		if (success) {
			ObjectReader reader = OM.readerFor(templateClass);
			MappingIterator<T> it = reader.readValues(body);
			while (it.hasNext()) {
				result.add(it.next());
			}
			Log.debug(LOGGER, uri.toASCIIString() + " JSON = " + OM.writeValueAsString(result));
			return result;
		} else {
			throw new Exception("HTTP Resposne: " + status + ", message: " + body);
		}
	}
	
	// Generic REST API invoke
	protected List<T> invokeRest(
			Config config, 
			String path, 
			String method, 
			MultivaluedMap<String, Object> headers, 
			Map<String, Object> queryParameters, 
			Object data,
			int... successStatuses) throws Exception {
		List<T> result = new ArrayList<>();		
		if (Linked.class.isAssignableFrom(templateClass)) {
			boolean hasNext = false;
			Map<String, Object> nextParameters = new HashMap<>();
			nextParameters.putAll(queryParameters);
			
			// TODO Test paging
			nextParameters.put(getLimitParameter(), 1);
			
			do {
				List<T> items = invokeRestCore(config, path, method, headers, nextParameters, data, successStatuses);
				if (items != null) {
					result.addAll(items);
				}
				for (T item : items) {
					Linked linkedItem = (Linked) item;
					Map<String, Object> linkedParameters = linkedItem.getNext();
					if (linkedParameters != null) {
						hasNext = true;
						Log.debug(LOGGER, "More items");
						for (Map.Entry<String, Object> entry : linkedParameters.entrySet()) {
							Log.debug(LOGGER, "entry: " + entry.getKey() + " = " + entry.getValue());
						}
						nextParameters.putAll(linkedParameters);
					} else {
						Log.debug(LOGGER, "No more items");
						hasNext = false;
					}
				}
			} while (hasNext);
		} else if (Paged.class.isAssignableFrom(templateClass)) {
			boolean hasNext = false;
			Map<String, Object> nextParameters = new HashMap<>();
			nextParameters.putAll(queryParameters);
			
			// TODO Test paging
			nextParameters.put(getLimitParameter(), 1);
			
			do {
				List<T> items = invokeRestCore(config, path, method, headers, nextParameters, data);
				if (items != null) {
					result.addAll(items);
				}
				for (T item : items) {
					Paged pagedItem = (Paged) item;
					int total = pagedItem.getPageTotal();
					int size = pagedItem.getPageSize();
					int startAt = pagedItem.getPageStartAt();
					if (total != -1) {
						if (startAt + size < total) {
							nextParameters.put(getStartAtParameter(), startAt + size);
							hasNext = true;
							Log.debug(LOGGER, "More items");
						} else {
							hasNext = false;
							Log.debug(LOGGER, "No more items");
						}
					} else {
						// Some REST APIs return incorrect total which is equal to size. 
						// So to be safe, total is set to -1, and one extra call is made
						if (size != 0) {
							nextParameters.put(getStartAtParameter(), startAt + size);
							hasNext = true;
							Log.debug(LOGGER, "More items");
						} else {
							hasNext = false;
							Log.debug(LOGGER, "No more items");
						}
					}
				}
			} while (hasNext);			
		} else {
			List<T> items = invokeRestCore(config, path, method, headers, queryParameters, data);
			if (items != null) {
				result.addAll(items);
			}
		}
		return result;
	}
	
	protected Path getOutputPath(Config config) throws IOException {
		return Paths.get(config.getOutputDirectory().toFile().getAbsolutePath(), this.getClass().getSimpleName() + ".csv");
	}
	
	public Path exportObjects(Config config) throws Exception {
		Path csvPath = getOutputPath(config);
		List<String> headers = getCSVHeaders();
		try (	FileWriter fw = new FileWriter(csvPath.toFile());
				CSVPrinter printer = new CSVPrinter(fw, CSV.getCSVFormat())) {
			CSV.printRecord(printer, headers);
			List<T> objects = getObjects(config);
			for (T obj : objects) {
				List<List<Object>> rows = getRows(obj);
				if (rows != null) {
					for (List<Object> row : rows) {
						CSV.printRecord(printer, row);
					}
				}
			}
		}
		return csvPath;
	}
	
	protected abstract List<String> getCSVHeaders();
	protected abstract List<List<Object>> getRows(T obj);
	public abstract List<T> getObjects(Config config) throws Exception;
}