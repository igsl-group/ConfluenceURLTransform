package com.igsl.config;

import java.util.List;

public class Cloud {
	private String domain;
	private String userName;
	private String apiToken;
	private float rate;
	private float period;
	private List<String> handlers;
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
	public List<String> getHandlers() {
		return handlers;
	}
	public void setHandlers(List<String> handlers) {
		this.handlers = handlers;
	}
	public float getRate() {
		return rate;
	}
	public void setRate(float rate) {
		this.rate = rate;
	}
	public float getPeriod() {
		return period;
	}
	public void setPeriod(float period) {
		this.period = period;
	}
}
