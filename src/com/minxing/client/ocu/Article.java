package com.minxing.client.ocu;

import com.minxing.client.utils.StringUtil;


public class Article {
		private String title;
		private String description;
		private String picUrl;
		private String url;
		private String app_url;
		private String resourceId;
		private String type;
		private Resource resource;
		private String action_label;
		
		public Article(String title, String desc, String picUrl, String url, String app_url){
			this.title = title;
			this.description = desc;
			this.picUrl = picUrl;
			this.url = url;
			this.app_url = app_url;
		}

		public Article(Resource resource){
			this(resource,"","");
		}
		
		public Article(Resource resource, String title, String picUrl){
			this(resource, title, picUrl, null);
		}
		
		public Article(Resource resource, String title, String picUrl, String description){
			this.title = title;
			this.picUrl = picUrl;
			this.type = "resource";
			this.resource = resource;
			this.description = description;
		}
		
		public Article(String resourceId, String title, String picUrl){
			this.title = title;
			this.picUrl = picUrl;
			this.type = "resource";
			this.resourceId = resourceId;
		}
		
		public void setResourceId(String resourceId) {
			this.resourceId = resourceId;
		}

		public String getTitle() {
			return StringUtil.convertContent(title);
		}

		public String getDescription() {
			return StringUtil.convertContent(description);
		}

		public String getPicUrl() {
			return StringUtil.convertContent(picUrl);
		}

		public String getUrl() {
			return StringUtil.convertContent(url);
		}

		public String getApp_url() {
			return StringUtil.convertContent(app_url);
		}

		public String getResourceId() {
			return this.resource.getId().toString();
		}

		public String getType() {
			return StringUtil.convertContent(type);
		}

		public Resource getResource() {
			return this.resource;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((app_url == null) ? 0 : app_url.hashCode());
			result = prime * result
					+ ((description == null) ? 0 : description.hashCode());
			result = prime * result
					+ ((picUrl == null) ? 0 : picUrl.hashCode());
			result = prime * result
					+ ((resource == null) ? 0 : resource.hashCode());
			result = prime * result
					+ ((resourceId == null) ? 0 : resourceId.hashCode());
			result = prime * result + ((title == null) ? 0 : title.hashCode());
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			result = prime * result + ((url == null) ? 0 : url.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Article other = (Article) obj;
			if (app_url == null) {
				if (other.app_url != null)
					return false;
			} else if (!app_url.equals(other.app_url))
				return false;
			if (description == null) {
				if (other.description != null)
					return false;
			} else if (!description.equals(other.description))
				return false;
			if (picUrl == null) {
				if (other.picUrl != null)
					return false;
			} else if (!picUrl.equals(other.picUrl))
				return false;
			if (resource == null) {
				if (other.resource != null)
					return false;
			} else if (!resource.equals(other.resource))
				return false;
			if (resourceId == null) {
				if (other.resourceId != null)
					return false;
			} else if (!resourceId.equals(other.resourceId))
				return false;
			if (title == null) {
				if (other.title != null)
					return false;
			} else if (!title.equals(other.title))
				return false;
			if (type == null) {
				if (other.type != null)
					return false;
			} else if (!type.equals(other.type))
				return false;
			if (url == null) {
				if (other.url != null)
					return false;
			} else if (!url.equals(other.url))
				return false;
			return true;
		}

		public void setAction_label(String label) {
			this.action_label = label;
		}

		public String getAction_label() {
			return action_label;
		}
		

}
