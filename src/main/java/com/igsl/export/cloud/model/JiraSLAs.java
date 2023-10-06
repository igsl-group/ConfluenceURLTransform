package com.igsl.export.cloud.model;

import java.util.List;

public class JiraSLAs {
	private List<JiraSLA> timeMetrics;
	public List<JiraSLA> getTimeMetrics() {
		return timeMetrics;
	}
	public void setTimeMetrics(List<JiraSLA> timeMetrics) {
		this.timeMetrics = timeMetrics;
	}
}
