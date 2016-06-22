package com.minxing.client.model;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

public class Graph {
	Long id;
	String url;
	String title;
	String image_url;
	String description;
	String app_url;
	String type = "page";

	public Graph() {
	}

	public void setId(Long _id) {
		this.id = _id;
	}

	public Long getId() {
		return this.id;
	}

	public void setAppURL(String app_url) {
		this.app_url = app_url;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setImageURL(String image_url) {
		this.image_url = image_url;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setURL(String url) {
		this.url = url;
	}

	public void setType(String _type) {
		this.type = _type;
	}

	public String getType() {
		return this.type;
	}

	public String getURL() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public String getImageURL() {
		return image_url;
	}

	public String getDescription() {
		return description;
	}

	public String getAppURL() {
		return app_url;
	}

	public String toJson() {
		Map<String, String> link = new HashMap<String, String>();
		if (this.url != null) {
			link.put("url", this.url);
		}

		if (this.title != null) {
			link.put("title", this.title);
		}

		if (this.image_url != null) {
			link.put("image_url", this.image_url);
		}

		if (this.description != null) {
			link.put("description", this.description);
		}
		link.put("type", "link");

		Map<String, Object> share_link = new HashMap<String, Object>();
		share_link.put("share_link", link);

		try {
			String story = new ObjectMapper().writeValueAsString(share_link);
			return story;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

}
