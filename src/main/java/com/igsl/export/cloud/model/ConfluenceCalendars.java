package com.igsl.export.cloud.model;

import java.util.List;

public class ConfluenceCalendars implements Paged {
	@Override
	public int getPageTotal() {
		return -1;
	}
	@Override
	public int getPageSize() {
		if (payload != null) {
			return payload.size();
		} else {
			return 0;
		}
	}
	@Override
	public int getPageStartAt() {
		return -1;
	}
	private List<ConfluenceCalendar> payload;
	public List<ConfluenceCalendar> getPayload() {
		return payload;
	}
	public void setPayload(List<ConfluenceCalendar> payload) {
		this.payload = payload;
	}
}
