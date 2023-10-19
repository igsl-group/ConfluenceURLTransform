package com.igsl.handler.postmigrate;

import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igsl.Log;

public class ParamSetting {
	private static final Logger LOGGER = LogManager.getLogger(ParamSetting.class);
	private String parameterName;
	private Class<?> baseExport;
	public ParamSetting(String parameterName, Class<?> baseExport) {
		this.parameterName = parameterName;
		this.baseExport = baseExport;
	}
	public String getReplacement(NameValuePair param, Map<String, Map<String, String>> mappings) {
		if (mappings.containsKey(this.baseExport.getClass().getCanonicalName())) {
			Map<String, String> mapping = mappings.get(this.baseExport.getClass().getCanonicalName());
			if (mapping.containsKey(param.getValue())) {
				return mapping.get(param.getValue());
			} else {
				Log.warn(LOGGER, "No match found for DC Id: " + param.getValue());
				return param.getValue();
			}
		} else {
			Log.error(LOGGER, "Mapping not found for: " + this.baseExport.getClass().getCanonicalName());
			return param.getValue();
		}
	}
	public String getParameterName() {
		return parameterName;
	}
	public Class<?> getBaseExport() {
		return baseExport;
	}
}