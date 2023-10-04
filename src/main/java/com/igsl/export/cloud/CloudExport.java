package com.igsl.export.cloud;

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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.igsl.Log;
import com.igsl.config.Config;
import com.igsl.export.cloud.model.Linked;
import com.igsl.export.cloud.model.Paged;

public abstract class CloudExport<T> {
	private static final Logger LOGGER = LogManager.getLogger(CloudExport.class);	
	public static final String SCHEME = "https://";
	public static final String ENCODDING = "ASCII";	
	private static final ObjectMapper OM = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
	protected static final JacksonJsonProvider JACKSON_JSON_PROVIDER = 
			new JacksonJaxbJsonProvider()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
			.configure(SerializationFeature.INDENT_OUTPUT, true);
	
	protected final Class<T> templateClass;
	public CloudExport(Class<T> templateClass) {
		this.templateClass = templateClass;
	}
	
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
	
	protected T invokeRestCore(
			Config config, 
			String path, 
			String method, 
			MultivaluedMap<String, Object> headers, 
			Map<String, Object> queryParameters, 
			Object data) throws Exception {
		T result = null;
		Client client = ClientBuilder.newClient();
		client.register(JACKSON_JSON_PROVIDER);
		URI uri = new URI(SCHEME + config.getCloud().getDomain()).resolve(path);
		WebTarget target = client.target(uri);
		Log.debug(LOGGER, "uri: " + uri.toASCIIString());
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
		
		// TODO Check response code

		String body = response.readEntity(String.class);
		ObjectReader reader = OM.readerFor(templateClass);
		result = reader.readValue(body);
		ObjectWriter writer = OM.writerFor(templateClass);
		Log.debug(LOGGER, uri.toASCIIString() + " = " + writer.writeValueAsString(result));
		return result;
	}
	
	// Generic REST API invoke
	protected List<T> invokeRest(
			Config config, 
			String path, 
			String method, 
			MultivaluedMap<String, Object> headers, 
			Map<String, Object> queryParameters, 
			Object data) throws Exception {
		List<T> result = new ArrayList<>();		
		if (Linked.class.isAssignableFrom(templateClass)) {
			boolean hasNext = false;
			Map<String, Object> nextParameters = new HashMap<>();
			nextParameters.putAll(queryParameters);
			do {
				T item = invokeRestCore(config, path, method, headers, nextParameters, data);
				if (item != null) {
					result.add(item);
				}
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
			} while (hasNext);
		} else if (Paged.class.isAssignableFrom(templateClass)) {
			int total = 0;
			int start = 0;
			int size = 0;
			boolean hasNext = false;
			Map<String, Object> nextParameters = new HashMap<>();
			nextParameters.putAll(queryParameters);
			do {
				T item = invokeRestCore(config, path, method, headers, nextParameters, data);
				if (item != null) {
					result.add(item);
				}
				Paged pagedItem = (Paged) item;
				total = pagedItem.getTotal();
				size = pagedItem.getSize();
				start += size;
				nextParameters.put(pagedItem.getStartParameterName(), start);
				hasNext = (start > total);
				if (hasNext) {
					Log.debug(LOGGER, "More items");
				} else {
					Log.debug(LOGGER, "No more items");
				}
			} while (hasNext);			
		} else {
			T item = invokeRestCore(config, path, method, headers, queryParameters, data);
			if (item != null) {
				result.add(item);
			}
		}
		return result;
	}
	
	protected Path getOutputPath(Config config) throws IOException {
		return Paths.get(config.getOutputDirectory().toFile().getAbsolutePath(), this.getClass().getSimpleName() + ".csv");
	}
	
	public abstract Path exportObjects(Config config) throws Exception;
}
