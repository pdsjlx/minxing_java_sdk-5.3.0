package com.minxing.client.app;

import java.util.Arrays;

public class OcuMessageSendResult {

	private int count = 0;
	private Long[] user_ids = new Long[] {};
	private Long message_id = null;

	public OcuMessageSendResult(int _count,Long _message_id,Long[] _user_ids) {
		this.count = _count;
		this.user_ids = _user_ids;
		this.message_id = _message_id;
	}

	/**
	 * 发送的人数
	 * @return
	 */
	public int getCount() {
		return count;
	}

	/**
	 * 发送的用户Id列表
	 * @return
	 */
	public Long[] getUserIds() {
		return user_ids;
	}

	/**
	 * 产生的消息Id
	 * @return
	 */
	public Long getMessageId() {
		return message_id;
	}

	@Override
	public String toString() {
		return "OcuMessageSendResult [count=" + count + ", user_ids="
				+ Arrays.toString(user_ids) + ", message_id=" + message_id
				+ "]";
	}
	
	

}
