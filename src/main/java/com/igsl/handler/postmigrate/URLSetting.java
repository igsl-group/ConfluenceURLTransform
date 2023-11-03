package com.igsl.handler.postmigrate;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;

/**
 * A combination of PathSetting and ParamSetting, in cases where you need to combine information from both.
 */
public abstract class URLSetting {
	private Class<? extends BasePostMigrate> postMigrate;
	
	private Pattern pathPattern;
	private Class<?> pathBaseExport;
	
	private String parameterName;
	private String newParameterName;
	private Class<?> paramBaseExport;
	
	public URLSetting(
			Class<? extends BasePostMigrate> postMigrate,
			Pattern pathPattern,
			Class<?> pathBaseExport,
			String parameterName, String newParameterName, 
			Class<?> paramBaseExport) {
		this.postMigrate = postMigrate;
		this.pathPattern = pathPattern;
		this.pathBaseExport = pathBaseExport;
		this.parameterName = parameterName;
		if (newParameterName != null) {
			this.newParameterName = newParameterName;
		} else {
			this.newParameterName = parameterName;
		}
		this.paramBaseExport = paramBaseExport;
	}
	
	public abstract void process(
			String path, List<NameValuePair> query, Map<String, Map<String, String>> mappings) throws Exception;
	public abstract String getPath();
	public abstract Map<String, String> getParameters();

	public Class<? extends BasePostMigrate> getPostMigrate() {
		return postMigrate;
	}

	public Pattern getPathPattern() {
		return pathPattern;
	}

	public Class<?> getPathBaseExport() {
		return pathBaseExport;
	}

	public String getParameterName() {
		return parameterName;
	}

	public String getNewParameterName() {
		return newParameterName;
	}

	public Class<?> getParamBaseExport() {
		return paramBaseExport;
	}
}
