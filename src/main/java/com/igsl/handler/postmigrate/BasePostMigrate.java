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

import com.google.protobuf.Message;
import com.igsl.CSV;
import com.igsl.Log;
import com.igsl.config.Config;
import com.igsl.export.cloud.BaseExport;
import com.igsl.handler.Handler;
import com.igsl.handler.HandlerResult;
import com.igsl.handler.HandlerResultType;
import com.igsl.handler.URLPattern;

public abstract class BasePostMigrate extends Handler {

	private static final Logger LOGGER = LogManager.getLogger(BasePostMigrate.class);
	
	protected Pattern hostRegex;
	protected List<MappingSetting> mappingSettings = new ArrayList<>();
	protected List<URLSetting> urlSettings = new ArrayList<>();
	protected List<PathSetting> pathSettings = new ArrayList<>();
	protected Map<String, ParamSetting> paramSettings = new HashMap<>();
	protected Map<String, Map<String, String>> mappings = new HashMap<>();
	protected boolean mappingsLoaded = false;
	
	public BasePostMigrate(
			Config config, 
			String hostRegex, 
			List<MappingSetting> mappingSettings,
			List<URLSetting> urlSettings) {
		super(config);
		this.hostRegex = Pattern.compile(hostRegex);
		if (mappingSettings != null) {
			this.mappingSettings = mappingSettings;
		}
		if (urlSettings != null) {
			this.urlSettings = urlSettings;
		}
	}
	
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
		if (urlSettings != null) {
			this.urlSettings = urlSettings;
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
		if (mappingsLoaded) {
			Log.debug(LOGGER, "Mappings already loaded for " + this.getClass().getCanonicalName());
			return;
		}
		Log.debug(LOGGER, "loadMappings() start for " + this.getClass().getCanonicalName());
		mappings = new HashMap<>();
		for (MappingSetting setting: mappingSettings) {
			Map<String, String> mapping = new HashMap<>();
			Path p = setting.getBaseExport().getMappingPath(config.getCloudExportDirectory().toFile().getAbsolutePath());
			Log.debug(LOGGER, "loadMappings() from file: " + p.toFile().getAbsolutePath());
			try (	FileReader fr = new FileReader(p.toFile()); 
					CSVParser parser = new CSVParser(fr, CSV.getCSVReadFormat())) {
				parser.forEach(new Consumer<CSVRecord>() {
					@Override
					public void accept(CSVRecord t) {
						// Only accept matched items
						String result = t.get(BaseExport.COL_NOTE);
						if (BaseExport.NOTE_MATCHED.equals(result)) {
							String key = t.get(setting.getKeyColumn());
							String value = t.get(setting.getValueColumn());
							Log.debug(LOGGER, "loadMappings() data: [" + key + "] = [" + value + "]");
							mapping.put(key, value);
						}
					}
				});
			}
			Log.debug(LOGGER, "loadMappings() saved under: " + setting.getBaseExport().getClass().getCanonicalName());
			mappings.put(setting.getBaseExport().getClass().getCanonicalName(), mapping);
		}
		Log.debug(LOGGER, "loadMappings() done for " + this.getClass().getCanonicalName());
	}
	
	/**
	 * Return patterns this handler accepts
	 */
	protected abstract URLPattern[] getPatterns();
	
	@Override
	protected boolean _accept(URI uri) {
		boolean hostMatched = false;
		boolean pathMatched = false;
		String host = uri.getHost();
		if (host == null) {
			if (hostRegex == null) {
				hostMatched = true;
			}
		} else {
			host += ((uri.getPort() == -1)? "" : ":" + uri.getPort());
			hostMatched = hostRegex.matcher(host).matches();
		}
		String path = uri.getPath();
		String query = uri.getQuery();
		Log.debug(LOGGER, this.getClass().getSimpleName() + " path: [" + path + "]");
		Log.debug(LOGGER, this.getClass().getSimpleName() + " query: [" + query + "]");
		for (URLPattern p : getPatterns()) {
			Log.debug(LOGGER, this.getClass().getSimpleName() + " vs pattern: [" + p.getPathPattern() + "]");
			if (p.match(path, query)) {
				pathMatched = true;
				Log.debug(LOGGER, "Path matched");
				break;
			}
		}
		return hostMatched && pathMatched;
	}
	
	@Override
	public HandlerResult handle(URI uri, String text) throws Exception {
		loadMappings();
		Log.debug(LOGGER, "Handling URI: " + uri.toASCIIString());
		URIBuilder parser = new URIBuilder(uri);
		List<NameValuePair> params = parser.getQueryParams();
		URIBuilder builder = new URIBuilder();
		builder.setScheme(config.getUrlTransform().getToScheme());
		builder.setHost(config.getUrlTransform().getConfluenceToHost());
		String originalPath = uri.getPath();
		Log.debug(LOGGER, "Path: " + originalPath);
		List<String> messages = new ArrayList<>();
		// Remap URL
		if (urlSettings.size() != 0) {
			boolean urlMatched = false;
			for (URLSetting setting : urlSettings) {
				Pattern pattern = setting.getPathPattern();
				Log.debug(LOGGER, this.getClass().getCanonicalName() + "vs [" + pattern.toString() + "]");
				Matcher m = pattern.matcher(originalPath);
				if (m.matches()) {
					urlMatched = true;
					try {
						setting.process(originalPath, params, this.mappings);
						builder.setPathSegments(addPathSegments(setting.getPath()));
						if (setting.getParameters() != null) {
							for (Map.Entry<String, String> param : setting.getParameters().entrySet()) {
								builder.addParameter(param.getKey(), param.getValue());
							}
						}
					} catch (Exception ex) {
						builder.setPath(originalPath);
						builder.setParameters(params);
						messages.add(ex.getMessage());
					}
				}
			}
			if (!urlMatched) {
				Log.warn(LOGGER, 
						this.getClass().getCanonicalName() + 
						" Path does not match url pattern, path unchanged");
				builder.setPath(originalPath);
				builder.setParameters(params);
			}
		} else {
			// Remap path
			if (pathSettings.size() != 0) {
				boolean pathMatched = false;
				for (PathSetting setting : pathSettings) {
					Pattern pattern = setting.getPathPattern();
					Matcher m = pattern.matcher(originalPath);
					if (m.matches()) {
						StringBuilder sb = new StringBuilder();
						String replacement;
						try {
							replacement = setting.getReplacement(m, mappings);
						} catch (Exception ex) {
							replacement = "$0";
							messages.add(ex.getMessage());
						}
						m.appendReplacement(sb, replacement);
						m.appendTail(sb);
						builder.setPathSegments(addPathSegments(sb.toString()));
						Log.debug(LOGGER, "Path changed: [" + originalPath + "] => [" + sb.toString() + "]");
						pathMatched = true;
					}
				}
				if (!pathMatched) {
					Log.warn(LOGGER, 
							this.getClass().getCanonicalName() + 
							" Path does not match pattern, path unchanged");
					builder.setPathSegments(addPathSegments(originalPath));
				}
			} else {
				Log.debug(LOGGER, this.getClass().getCanonicalName() + " No PathSetting, path unchanged");
				builder.setPathSegments(addPathSegments(originalPath));
			}
			// Remap parameters
			for (NameValuePair param : params) {
				Log.debug(LOGGER, this.getClass().getCanonicalName() + 
						" Param: [" + param.getName() + "] = [" + param.getValue() + "]");
				if (paramSettings.containsKey(param.getName())) {
					ParamSetting setting = paramSettings.get(param.getName());
					String replacement;
					try {
						replacement = setting.getReplacement(param, mappings);
					} catch (Exception ex) {
						replacement = param.getValue();
						messages.add(ex.getMessage());
					}
					Log.debug(LOGGER, this.getClass().getCanonicalName() + 
							" Param changed: [" + param.getValue() + "] => [" + replacement + "]");
					builder.addParameter(setting.getNewParameterName(), replacement);
				} else {
					// Add parameter as is
					Log.debug(LOGGER, this.getClass().getCanonicalName() + 
							" No ParamSetting, param unchanged");
					builder.addParameter(param.getName(), param.getValue());
				}
			}
		}
		// Add fragment
		builder.setFragment(uri.getFragment());
		if (messages.size() == 0) {
			return new HandlerResult(builder.build());
		} else {
			StringBuilder sb = new StringBuilder();
			for (String msg : messages) {
				sb.append(msg).append("; ");
			}
			return new HandlerResult(builder.build(), HandlerResultType.WARN, sb.toString());
		}
	}
}
