package com.igsl.handler.postmigrate;

import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igsl.CSV;
import com.igsl.Log;
import com.igsl.config.Config;
import com.igsl.export.cloud.BaseExport;
import com.igsl.handler.Handler;
import com.igsl.handler.HandlerResult;

public abstract class BasePostMigrate extends Handler {

	private static final Logger LOGGER = LogManager.getLogger(BasePostMigrate.class);
	
	protected Pattern hostRegex;
	protected List<MappingSetting> mappingSettings = new ArrayList<>();
	protected List<PathSetting> pathSettings = new ArrayList<>();
	protected Map<String, ParamSetting> paramSettings = new HashMap<>();
	protected Map<String, Map<String, String>> mappings = new HashMap<>();
	
	public BasePostMigrate(
			Config config, 
			String hostRegex, 
			List<MappingSetting> mappingSettings,
			List<PathSetting> pathSettings, 
			List<ParamSetting> paramSettings) {
		super(config);
		this.hostRegex = Pattern.compile(hostRegex);
		if (mappingSettings != null) {
			this.mappingSettings = mappingSettings;
		}
		if (pathSettings != null) {
			this.pathSettings = pathSettings;
		}
		if (paramSettings != null) {
			for (ParamSetting setting : paramSettings) {
				this.paramSettings.put(setting.getParameterName(), setting);
			}
		}
	}
	
	protected void loadMappings() throws IOException {
		mappings = new HashMap<>();
		for (MappingSetting setting: mappingSettings) {
			Map<String, String> mapping = new HashMap<>();
			Path p = setting.getBaseExport().getMappingPath(config.getCloudExportDirectory().toFile().getAbsolutePath());
			try (	FileReader fr = new FileReader(p.toFile()); 
					CSVParser parser = new CSVParser(fr, CSV.getCSVReadFormat())) {
				parser.forEach(new Consumer<CSVRecord>() {
					@Override
					public void accept(CSVRecord t) {
						String key = t.get(setting.getKeyColumn());
						String value = t.get(setting.getValueColumn());
						mapping.put(key, value);
					}
				});				
			}
			mappings.put(setting.getBaseExport().getClass().getCanonicalName(), mapping);
		}
	}
	
	@Override
	protected boolean _accept(URI uri) {
		String host = uri.getHost();
		if (host == null) {
			if (hostRegex != null) {
				return false;
			}
			return true;
		} else {
			host += ((uri.getPort() == -1)? "" : ":" + uri.getPort());
			boolean r = hostRegex.matcher(host).matches();
			return r;
		}
	}
	
	@Override
	public HandlerResult handle(URI uri, String text) throws Exception {
		loadMappings();
		URIBuilder parser = new URIBuilder(uri);
		List<NameValuePair> params = parser.getQueryParams();
		URIBuilder builder = new URIBuilder();
		builder.setScheme(config.getUrlTransform().getToScheme());
		builder.setHost(config.getUrlTransform().getConfluenceToHost());
		String originalPath = uri.getPath();
		// Remove base path
		if (originalPath.startsWith(config.getUrlTransform().getConfluenceFromBasePath())) {
			originalPath = originalPath.substring(config.getUrlTransform().getConfluenceFromBasePath().length());
		}
		// Remap path
		if (pathSettings.size() != 0) {
			for (PathSetting setting : pathSettings) {
				Pattern pattern = setting.getPathPattern();
				Matcher m = pattern.matcher(originalPath);
				if (m.matches()) {
					StringBuilder sb = new StringBuilder();
					String replacement = setting.getReplacement(m, mappings);
					m.appendReplacement(sb, replacement);
					m.appendTail(sb);
					builder.setPathSegments(addPathSegments(
							config.getUrlTransform().getConfluenceToBasePath(),
							sb.toString()));
				} else {
					Log.warn(LOGGER, "Path does not match pattern: " + uri.toASCIIString());
					builder.setPathSegments(addPathSegments(
							config.getUrlTransform().getConfluenceToBasePath(),
							originalPath));
				}
			}
		} else {
			builder.setPathSegments(addPathSegments(
					config.getUrlTransform().getConfluenceToBasePath(),
					originalPath));
		}
		// Remap parameters
		for (NameValuePair param : params) {
			if (paramSettings.containsKey(param.getName())) {
				ParamSetting setting = paramSettings.get(param.getName());
				String replacement = setting.getReplacement(param, mappings);
				builder.addParameter(param.getName(), replacement);
			} else {
				// Add parameter as is
				builder.addParameter(param.getName(), param.getValue());
			}
		}
		// Add fragment
		builder.setFragment(uri.getFragment());
		return new HandlerResult(builder.build());
	}
}
