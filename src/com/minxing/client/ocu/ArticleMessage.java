package com.minxing.client.ocu;

import java.util.ArrayList;
import java.util.List;

public class ArticleMessage implements Message {
	private List<Article> articles;
	private boolean secret;

	public ArticleMessage() {
		this(false);
	}

	public ArticleMessage(boolean secret) {
		articles = new ArrayList<Article>();
		this.secret = secret;
	}

	public List<Article> getArticles() {
		return articles;
	}

	public void addArticle(Article pt) {
		if (!articles.contains(pt)) {
			articles.add(pt);
		}
	}

	public void addArticles(List<Article> pts) {
		if (pts != null) {
			for (Article pt : pts) {
				addArticle(pt);
			}
		}
	}

	public Resource getMessageResource() {

		for (int i = 0, s = articles.size(); i < s; i++) {
			Article pt = articles.get(i);
			if ("resource".equals(pt.getType())) {
				Resource res = pt.getResource();
				return res;
			}

		}
		return null;
	}

	public String getBody() {
		if (articles.size() <= 0) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"article_count\":").append(articles.size()).append(",");
		if (secret) {
			sb.append("\"secret\":").append(secret).append(",");
		}

		sb.append("\"articles\":[");
		Article pt = null;
		for (int i = 0, s = articles.size(); i < s; i++) {
			pt = articles.get(i);
			if (i != 0) {
				sb.append(",");
			}
			sb.append("{");
			if ("resource".equals(pt.getType())) {
				sb.append("\"type\":\"resource\"").append(",");
				sb.append("\"resource_id\":\"").append(pt.getResourceId())
						.append("\"");
				if (pt.getTitle() != null && !pt.getTitle().trim().equals("")) {
					sb.append(",");
					sb.append("\"title\":\"").append(pt.getTitle())
							.append("\"");
				}

				if (pt.getPicUrl() != null && !pt.getPicUrl().trim().equals("")) {
					sb.append(",");
					sb.append("\"pic_url\":\"").append(pt.getPicUrl())
							.append("\"");
				}

				if (pt.getDescription() != null
						&& !pt.getDescription().trim().equals("")) {
					sb.append(",");
					sb.append("\"description\":\"").append(pt.getDescription())
							.append("\"");
				}

			} else {
				sb.append("\"title\":").append("\"").append(pt.getTitle())
						.append("\"").append(",");
				sb.append("\"description\":").append("\"")
						.append(pt.getDescription()).append("\"");
				if (pt.getPicUrl() != null) {
					sb.append(",");
					sb.append("\"pic_url\":").append("\"")
							.append(pt.getPicUrl()).append("\"");
				}
				if (pt.getApp_url() != null) {
					sb.append(",");
					sb.append("\"app_url\":").append("\"")
							.append(pt.getApp_url()).append("\"");
				}
				if (pt.getUrl() != null) {
					sb.append(",");
					sb.append("\"url\":").append("\"").append(pt.getUrl())
							.append("\"");
				}
				if (pt.getAction_label() != null) {
					sb.append(",");
					sb.append("\"action_label\":").append("\"")
							.append(pt.getAction_label()).append("\"");
				}
			}

			sb.append("}");
		}
		sb.append("]");
		sb.append("}");
		return sb.toString();
	}

	@Override
	public int messageType() {
		// TODO Auto-generated method stub
		return RICH_TEXT_MESSAGE;
	}
}
