package com.igsl.handler.postmigrate;

import com.igsl.export.cloud.BaseExport;

public class MappingSetting {
	private BaseExport<?> baseExport;
	private String keyColumn;
	private String valueColumn;
	public MappingSetting(BaseExport<?> baseExport, String keyColumn, String valueColumn) {
		this.baseExport = baseExport;
		this.keyColumn = keyColumn;
		this.valueColumn = valueColumn;
	}
	public String getKeyColumn() {
		return keyColumn;
	}
	public String getValueColumn() {
		return valueColumn;
	}
	public BaseExport<?> getBaseExport() {
		return baseExport;
	}
}
