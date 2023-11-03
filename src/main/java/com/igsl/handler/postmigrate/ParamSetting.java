package com.igsl.handler.postmigrate;

import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igsl.Log;

public class ParamSetting {
	private static final Logger LOGGER = LogManager.getLogger(ParamSetting.class);
	private Class<? extends BasePostMigrate> postMigrate;
	private String parameterName;
	private String newParameterName;
	private Class<?> baseExport;
	public ParamSetting(Class<? extends BasePostMigrate> postMigrate, 
			String parameterName, String newParameterName, 
			Class<?> baseExport) {
		this.postMigrate = postMigrate;
		this.parameterName = parameterName;
		if (newParameterName != null) {
			this.newParameterName = newParameterName;
		} else {
			this.newParameterName = parameterName;
		}
		this.baseExport = baseExport;
	}
	public String getReplacement(NameValuePair param, Map<String, Map<String, String>> mappings) throws Exception {
		if (mappings.containsKey(this.baseExport.getCanonicalName())) {
			Map<String, String> mapping = mappings.get(this.baseExport.getCanonicalName());
			if (mapping.containsKey(param.getValue())) {
				return mapping.get(param.getValue());
			} else {
				throw new Exception(postMigrate.getCanonicalName() + ": No match found for DC Id: " + param.getValue());
			}
		} else {
			throw new Exception(postMigrate.getCanonicalName() + 
					": Mapping not found for: " + this.baseExport.getCanonicalName());
		}
	}
	public String getParameterName() {
		return parameterName;
	}
	public Class<?> getBaseExport() {
		return baseExport;
	}
	public Class<? extends BasePostMigrate> getPostMigrate() {
		return postMigrate;
	}
	public String getNewParameterName() {
		return newParameterName;
	}
}