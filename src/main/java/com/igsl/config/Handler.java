package com.igsl.config;

import java.util.ArrayList;
import java.util.List;

public class Handler implements ConfigInterface {
	private List<String> dc;
	private List<String> urlTransform;
	private List<String> cloud;
	private List<String> postMigrate;
	public List<String> getDc() {
		return dc;
	}
	public void setDc(List<String> dc) {
		this.dc = dc;
	}
	public List<String> getUrlTransform() {
		return urlTransform;
	}
	public void setUrlTransform(List<String> urlTransform) {
		this.urlTransform = urlTransform;
	}
	public List<String> getCloud() {
		return cloud;
	}
	public void setCloud(List<String> cloud) {
		this.cloud = cloud;
	}
	public List<String> getPostMigrate() {
		return postMigrate;
	}
	public void setPostMigrate(List<String> postMigrate) {
		this.postMigrate = postMigrate;
	}
	@Override
	public List<String> validate() {
		List<String> messages = new ArrayList<>();
		if (dc == null || dc.size() == 0) {
			messages.add("dc is not specified. Please provide DC export handlers (com.igsl.export.dc.ObjectExport subclass).");
		}
		if (urlTransform == null || urlTransform.size() == 0) {
			messages.add("urlTransform is not specified. Please provide URL handlers (com.igsl.handler.Handler subclass).");
		}
		if (cloud == null || cloud.size() == 0) {
			messages.add("cloud is not specified. Please provide Cloud export handlers (com.igsl.export.cloud.BaseExport subclass).");
		}
		if (postMigrate == null || postMigrate.size() == 0) {
			messages.add("postMigrate is not specified. Please provide post migrate handlers (com.igsl.handler.postmigrate.BasePostMigrate subclass).");
		}
		return messages;
	}
}
