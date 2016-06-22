package com.minxing.client.app;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.minxing.client.json.JSONException;
import com.minxing.client.json.JSONObject;
import com.minxing.client.model.Account;
import com.minxing.client.model.PostParameter;
/**
 * 支持敏推服务器3.0的sdk实现
 * 
 * @author helhades
 *
 */
public class MXPush extends Account {

	private String full_key;
	private String app_secret;
	private String pushUrl;
	
	private MXPush(String pushUrl, String full_key, String app_secret) {
		this.full_key = full_key;
		this.app_secret = app_secret;
		this.pushUrl = pushUrl;
	}
	/**
	 * 创建一个敏推接口对象 
	 * @param pushUrl 敏推服务器的url 形如 http://192.168.100.80:4567
	 * @param full_key 应用的app的full_key 从敏推管理页面的应用详情可查看到app_key的便是
	 * @param app_secret 应用的app的app_secret 从敏推管理页面的应用详情可查看到app_secret的便是
	 * @return
	 */
	public static MXPush newInstance(String pushUrl, String full_key,
			String app_secret) {
		return new MXPush(pushUrl, full_key, app_secret);
	}
	
	public boolean notificationMultiApn(String bundle_id,String token, String alert, String sound,
			String badge, Map<String, Object> custom) {
		List<PostParameter> ps = new ArrayList<PostParameter>();
		ps.add(new PostParameter("bundle_id", bundle_id));
		PostParameter p = new PostParameter("token", token);
		ps.add(p);
		PostParameter p1 = new PostParameter("alert", alert);
		ps.add(p1);
		addParameter(ps);
		addParameter(ps,sound,badge,custom);
		JSONObject o = post(pushUrl + "/notifications/multi_apn",
				ps.toArray(new PostParameter[ps.size()]), false);
		try {
			if (o.getString("code") != null) {
				return true;
			} else {
				return false;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean notificationApn(String token, String alert, String sound,
			String badge, Map<String, Object> custom) {
		List<PostParameter> ps = new ArrayList<PostParameter>();
		PostParameter p = new PostParameter("dev_token", token);
		ps.add(p);
		PostParameter p1 = new PostParameter("alert", alert);
		ps.add(p1);
		addParameter(ps);
		addParameter(ps,sound,badge,custom);
		JSONObject o = post(pushUrl + "/notifications/apn",
				ps.toArray(new PostParameter[ps.size()]), false);
		try {
			if (o.getString("code") != null) {
				return true;
			} else {
				return false;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	
	
	public boolean notificationMqtt(String dev_token,String message){
		List<PostParameter> ps = new ArrayList<PostParameter>();
		PostParameter p = new PostParameter("dev_token", dev_token);
		PostParameter p3 = new PostParameter("message", message);
		ps.add(p);
		ps.add(p3);
		addParameter(ps);
		JSONObject o = post(pushUrl + "/notifications/mqtt",
				ps.toArray(new PostParameter[ps.size()]), false);
		try {
			if (o.getString("code") != null) {
				return true;
			} else {
				return false;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	
	public boolean notificationMqtt(List<String> tokens,String message){
		List<PostParameter> ps = new ArrayList<PostParameter>();
		StringBuilder sb = new StringBuilder(tokens.get(0));
		for (int i = 1; i < tokens.size(); i++) {
			sb.append(",").append(tokens.get(i));
		}
		PostParameter p = new PostParameter("dev_tokens", sb.toString());
		PostParameter p3 = new PostParameter("message", message);
		ps.add(p);
		ps.add(p3);
		addParameter(ps);
		JSONObject o = post(pushUrl + "/notifications/mqtt",
				ps.toArray(new PostParameter[ps.size()]), false);
		try {
			if (o.getString("code") != null) {
				return true;
			} else {
				return false;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * 推送方法 使用该方法可以给任何已经注册在敏推服务器的设备推送信息
	 * @param token 设备的token
	 * @param alert 内容
	 * @param sound 声音提醒  主要针对apple机型
	 * @param badge 角标提醒 字符型的数字 主要针对apple机型
	 * @param custom 自定义信息 主要针对apple机型
	 * @return 成功返回true 这里说的成功是指信息已经发送给推送服务器 至于是否能到达手机 这里不做判断
	 */
	public boolean notificationAny(String token, String alert, String sound,
			String badge, Map<String, Object> custom) {
		List<PostParameter> ps = new ArrayList<PostParameter>();
		PostParameter p = new PostParameter("dev_token", token);
		ps.add(p);
		return notification(ps,alert, sound, badge, custom);
		
	}
	
	
	

	
	/**
	 * 推送方法 使用该方法可以给任何已经注册在敏推服务器的设备推送信息
	 * @param tokens 多个设备的token
	 * @param alert 内容
	 * @param sound 声音提醒  主要针对apple机型
	 * @param badge 角标提醒 字符型的数字 主要针对apple机型
	 * @param custom 自定义信息 主要针对apple机型
	 * @return 成功返回true 这里说的成功是指信息已经发送给推送服务器 至于是否能到达手机 这里不做判断
	 */
	public boolean notificationAny(List<String> tokens, String alert,
			String sound, String badge, Map<String, Object> custom) {
		StringBuilder sb = new StringBuilder(tokens.get(0));
		for (int i = 1; i < tokens.size(); i++) {
			sb.append(",").append(tokens.get(i));
		}
		List<PostParameter> ps = new ArrayList<PostParameter>();
		PostParameter p = new PostParameter("dev_tokens", sb.toString());
		ps.add(p);
		return notification(ps,alert, sound, badge, custom);
	}
	
	

	private String SHA1(String decript) {
		try {
			MessageDigest digest = java.security.MessageDigest
					.getInstance("SHA-1");
			digest.update(decript.getBytes());
			byte messageDigest[] = digest.digest();
			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			// 字节数组转换为 十六进制 数
			for (int i = 0; i < messageDigest.length; i++) {
				String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
				if (shaHex.length() < 2) {
					hexString.append(0);
				}
				hexString.append(shaHex);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	private boolean notification(List<PostParameter> ps,String alert, String sound, String badge,
			Map<String, Object> custom) {
		PostParameter p1 = new PostParameter("alert", alert);
		ps.add(p1);
		addParameter(ps);
		addParameter(ps,sound,badge,custom);
		JSONObject o = post(pushUrl + "/notifications/any",
				ps.toArray(new PostParameter[ps.size()]), false);
		try {
			if (o.getString("code") != null) {
				return true;
			} else {
				return false;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	private void addParameter(List<PostParameter> ps) {
		PostParameter p1 = new PostParameter("app_key", full_key);
		PostParameter p2 = new PostParameter("app_secret", app_secret);
		ps.add(p1);
		ps.add(p2);
		String t = String.valueOf(System.currentTimeMillis());
		PostParameter p4 = new PostParameter("timestamp", t);
		String signed = SHA1(full_key+":"+t+":"+app_secret);
		ps.add(p4);
		ps.add(new PostParameter("signed",signed));
	}

	private void addParameter(List<PostParameter> ps, String sound,
			String badge, Map<String, Object> custom) {
		if (sound != null) {
			ps.add(new PostParameter("sound", sound));
		}
		if (badge != null) {
			ps.add(new PostParameter("badge", badge));
		}
		if (custom != null) {
			JSONObject json = new JSONObject(custom);
			ps.add(new PostParameter("custom", json.toString()));
		}
	}

	@Override
	protected String beforeRequest(String url, List<PostParameter> paramsList,
			List<PostParameter> headersList) {
		// TODO Auto-generated method stub
		return null;
	}
}
