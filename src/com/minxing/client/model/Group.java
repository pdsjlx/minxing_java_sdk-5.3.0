package com.minxing.client.model;

public class Group {

	final public static String PUBLIC = "public";/* 公开组 */
	final public static String PRIVATE = "private"; /* 私有组 */
	final public static String SUPPORT = "support"; /* 专家支持组 */
	final public static String NORMAL = "normal"; /* 普通组，默认类型 */

	Long id;
	private String name;
	private String description;
	private boolean publicGroup;
	private boolean supportType;
	private boolean hidden;

	public Group(Long _id) {
		id = _id;
	}

	public Group(Long _id, String _name, String _description,
			boolean _publicGroup, boolean _supportType, boolean _hidden) {

		id = _id;
		name = _name;
		description = _description;
		publicGroup = _publicGroup;
		supportType = _supportType;
		hidden = _hidden;

	}

	public Long getId() {
		return id;
	}

	public String toString() {
		return "<Group id:" + id + ", name:" + this.name + ", public:"
				+ this.isPublicGroup() + ", support_type:"
				+ this.isSupportType() + ">";
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public boolean isPublicGroup() {
		return publicGroup;
	}

	public boolean isSupportType() {
		return supportType;
	}

	public boolean isHidden() {
		return hidden;
	}

}
