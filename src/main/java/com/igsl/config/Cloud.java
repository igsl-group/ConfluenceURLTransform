package com.igsl.config;

import java.util.ArrayList;
import java.util.List;

public class Cloud implements ConfigInterface {
	private String domain;
	private String userName;
	private String apiToken;
	private Float rate = 100F;
	private Float period = 1000F;
	@Override
	public List<String> validate() {
		List<String> messages = new ArrayList<>();
		if (domain == null || domain.isBlank()) {
			messages.add("domain is empty. Please provide Atlassian Cloud domain, e.g. \"kcwong.atlassian.net\"");
		}
		if (rate == null) {
			messages.add("rate is empty. Please provide max no. of REST API requests in period, e.g. 100");
		}
		if (period == null) {
			messages.add("period is empty. Please provide period in milliseconds, e.g. 1000");
		}
		return messages;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getApiToken() {
		return apiToken;
	}
	public void setApiToken(String apiToken) {
		this.apiToken = apiToken;
	}
	public Float getRate() {
		return rate;
	}
	public void setRate(Float rate) {
		this.rate = rate;
	}
	public Float getPeriod() {
		return period;
	}
	public void setPeriod(Float period) {
		this.period = period;
	}
}
