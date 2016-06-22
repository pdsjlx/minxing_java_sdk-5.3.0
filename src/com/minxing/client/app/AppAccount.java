package com.minxing.client.app;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.ParameterParser;
import org.apache.commons.httpclient.util.URIUtil;

import com.minxing.client.http.HttpClient;
import com.minxing.client.http.Response;
import com.minxing.client.json.JSONArray;
import com.minxing.client.json.JSONException;
import com.minxing.client.json.JSONObject;
import com.minxing.client.model.Account;
import com.minxing.client.model.ApiErrorException;
import com.minxing.client.model.Conversation;
import com.minxing.client.model.Graph;
import com.minxing.client.model.Group;
import com.minxing.client.model.MxException;
import com.minxing.client.model.MxVerifyException;
import com.minxing.client.model.PostParameter;
import com.minxing.client.model.ShareLink;
import com.minxing.client.ocu.AppMessage;
import com.minxing.client.ocu.ArticleMessage;
import com.minxing.client.ocu.Message;
import com.minxing.client.ocu.Resource;
import com.minxing.client.ocu.TextMessage;
import com.minxing.client.ocu.UserInfo;
import com.minxing.client.organization.Department;
import com.minxing.client.organization.Network;
import com.minxing.client.organization.User;
import com.minxing.client.utils.HMACSHA1;
import com.minxing.client.utils.StringUtil;
import com.minxing.client.utils.UrlEncoder;

public class AppAccount extends Account {

	private String _token = null;
	private String _loginName;
	private String _serverURL;
	private long _currentUserId = 0;
	private String client_id;
	private String secret;

	private AppAccount(String serverURL, String token) {
		this._serverURL = serverURL;
		this._token = token;
		client.setToken(this._token);
		client.setTokenType("Bearer");
	}

	private AppAccount(String serverURL, String app_id, String secret) {
		this._serverURL = serverURL;
		this.client_id = app_id;
		this.secret = secret;
		client.setTokenType("MAC");
	}

	private AppAccount(String serverURL, String loginName, String password,
			String clientId) {
		this._serverURL = serverURL;
		this.client_id = clientId;
		PostParameter grant_type = new PostParameter("grant_type", "password");
		PostParameter login_name = new PostParameter("login_name", loginName);
		PostParameter passwd = new PostParameter("password", password);
		PostParameter app_id = new PostParameter("app_id", clientId);
		PostParameter[] params = new PostParameter[] { grant_type, login_name,
				passwd, app_id };

		try {
			URL aURL = new URL(_serverURL);
			String host = aURL.getHost();
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
			messageDigest.update((host + ":" + loginName).getBytes());

			String cm = StringUtil.bytesToHex(messageDigest.digest());
			PostParameter checksum = new PostParameter("X-CLIENT-CHECKSUM", cm);
			PostParameter[] header = new PostParameter[] { checksum };

			HttpClient _client = new HttpClient();
			Response return_rsp = _client.post(serverURL + "/oauth2/token",
					params, header, false);

			if (return_rsp.getStatusCode() == 200) {

				JSONObject o = return_rsp.asJSONObject();
				try {
					_token = o.getString("access_token");
					client.setToken(this._token);
					client.setTokenType("Bearer");

				} catch (JSONException e) {
					throw new MxException("解析返回值出错", e);
				}
			} else {
				throw new MxException("HTTP " + return_rsp.getStatusCode()
						+ ": " + return_rsp.getResponseAsString());
			}

		} catch (Exception e) {
			throw new MxException(e);
		}

	}

	/**
	 * 设置API调用的用户身份，消息按照这个身份发出
	 * 
	 * @param loginName
	 *            登录名
	 */
	public void setFromUserLoginName(String loginName) {
		this._loginName = loginName;

	}

	/**
	 * 设置API调用的用户身份，消息按照这个身份发出
	 * 
	 * @param userId
	 *            用户对象的Id.
	 */
	public void setFromUserId(long userId) {
		this._currentUserId = userId;
	}

	/**
	 * 使用接入端的Token登录系统
	 * 
	 * @param serverURL
	 *            服务器的访问地址
	 * @param bearerToken
	 *            bearerToken，从接入端的配置中获取
	 * @return
	 */
	public static AppAccount loginByAccessToken(String serverURL,
			String bearerToken) {
		return new AppAccount(serverURL, bearerToken);
	}

	/**
	 * 使用接入端的appid、appsecret登录系统，
	 * 
	 * @param serverURL
	 *            系统的url.
	 * @param app_id
	 *            接入端应用的Id,在接入端管理的页面上可以找到。
	 * @param secret
	 *            接入端应用的秘钥，可以在接入端的页面上看到。
	 * @return
	 */
	public static AppAccount loginByAppSecret(String serverURL, String app_id,
			String secret) {
		return new AppAccount(serverURL, app_id, secret);
	}

	/**
	 * 使用用户名密码方式登录系统
	 * 
	 * @param serverURL
	 *            服务器的访问地址
	 * @param loginName
	 *            系统登录名
	 * @param password
	 *            用户密码
	 * @param clientId
	 *            使用的注册客户端，可以设置为4,表示PC的客户端。0-web 1-ios 2-android
	 * @return
	 */
	public static AppAccount loginByPassword(String serverURL,
			String loginName, String password, String clientId) {

		return new AppAccount(serverURL, loginName, password, clientId);
	}

	// //////////////////////////////////////////////////////////////////////////
	/**
	 * url拼接
	 */
	@Override
	protected String beforeRequest(String url, List<PostParameter> paramsList,
			List<PostParameter> headersList) {

		if (this._currentUserId != 0L) {
			PostParameter as_user = new PostParameter("X-AS-USER",
					this._currentUserId);
			headersList.add(as_user);
		} else if (this._loginName != null && this._loginName.length() > 0) {
			PostParameter as_user = new PostParameter("X-AS-USER",
					this._loginName);
			headersList.add(as_user);
		}

		headersList.add(new PostParameter("User-Agent", "MySuperUserAgent"));

		String _url = "";

		if (url.trim().startsWith("http://")
				|| url.trim().startsWith("https://")) {
			_url = url;
		} else {
			if (!url.trim().startsWith("/")) {
				url = "/" + url.trim();
			}
			// url = rootUrl + apiPrefix + url;
			url = _serverURL + url;
			_url = url;
		}

		if ("MAC".equals(client.getTokenType())) {

			long time = System.currentTimeMillis();

			String token = UrlEncoder.encode(this.client_id
					+ ":"
					+ HMACSHA1.getSignature(_url + "?timestamp=" + time,
							this.secret));

			client.setToken(token);
			client.setTokenType("MAC");
			headersList.add(new PostParameter("timestamp", "" + time));

		}

		return _url;
	}

	// ////////////////////////////////////////////////////////////////////

	/**
	 * rest api通道，get方法API调用
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws MxException
	 */
	// public JSONObject get(String url, Map<String, String> params)
	// throws MxException {
	// PostParameter[] pps = createParams(params);
	// return this.get(url, pps);
	// }
	//
	public Response get(String url, Map<String, String> params)
			throws MxException {
		PostParameter[] pps = createParams(params);
		return this.getForResponse(url, pps, new PostParameter[0], true);
	}

	/**
	 * rest api通道，post方法API调用
	 * 
	 * @param url
	 * @param params
	 * @param headers
	 * @return
	 * @throws MxException
	 */
	// public JSONObject post(String url, Map<String, String> params,
	// Map<String, String> headers) throws MxException {
	// PostParameter[] pps = createParams(params);
	// PostParameter[] hs = createParams(headers);
	// return this.post(url, pps, hs, true);
	// }

	public Response post(String url, Map<String, String> params,
			Map<String, String> headers) throws MxException {
		PostParameter[] pps = createParams(params);
		PostParameter[] hs = createParams(headers);
		return this.postForResponse(url, pps, hs, true);
	}

	/**
	 * rest api通道，post方法API调用,上传文件
	 * 
	 * @param url
	 * @param params
	 * @param headers
	 * @param file
	 * @return
	 * @throws MxException
	 */
	public JSONArray post(String url, Map<String, String> params,
			Map<String, String> headers, File file) throws MxException {
		PostParameter[] pps = createParams(params);
		PostParameter[] hs = createParams(headers);
		return this.post(url, pps, hs, file, true);
	}

	/**
	 * rest api通道，put方法API调用
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws MxException
	 */
	public JSONObject put(String url, Map<String, String> params)
			throws MxException {
		PostParameter[] pps = createParams(params);
		return this.put(url, pps);
	}

	/**
	 * rest api通道，delete方法的API调用
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws MxException
	 */
	public JSONObject delete(String url, Map<String, String> params)
			throws MxException {
		PostParameter[] pps = createParams(params);
		return this.delete(url, pps);
	}

	private PostParameter[] createParams(Map<String, String> params) {
		if (params == null) {
			return new PostParameter[0];
		}
		PostParameter[] pps = new PostParameter[params.size()];
		int i = 0;
		for (Iterator<String> it = params.keySet().iterator(); it.hasNext();) {
			String key = it.next();
			String value = params.get(key);
			PostParameter p = new PostParameter(key, value);
			pps[i++] = p;
		}
		return pps;
	}

	/**
	 * 发送文件到会话聊天中
	 * 
	 * @param conversation_id
	 * @param file
	 * @return
	 */
	public long[] uploadConversationFile(String conversation_id, File file) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("conversation_id", conversation_id);
		Map<String, String> headers = new HashMap<String, String>();

		JSONArray arr = null;
		long[] filesArray = new long[] {};
		try {
			arr = this.post("api/v1/uploaded_files", params, headers, file);
			filesArray = new long[arr.length()];
			for (int i = 0; i < arr.length(); i++) {
				JSONObject o = arr.getJSONObject(i);
				filesArray[i] = o.getLong("id");
			}

		} catch (Exception e) {
			throw new MxException(e);
		}

		return filesArray;
	}

	/**
	 * 发送文件到会话聊天中
	 * 
	 * @param conversation_id
	 * @param file
	 * @return
	 */
	public InputStream downloadFile(Long fileId) {

		Map<String, String> params = new HashMap<String, String>();
		PostParameter[] pps = createParams(params);

		try {
			InputStream response = this.getForStream("/files/" + fileId, pps,
					pps, true);
			return response;

		} catch (Exception e) {
			throw new MxException(e);
		}

	}

	/**
	 * 上传头像
	 * 
	 * @param loginName
	 *            登录名 给这个登录名的用户上传头像
	 * @param avatarPath
	 *            头像文件的路径
	 * @return
	 */
	public boolean uploadUserAvatar(String loginName, String avatarPath) {
		this.setFromUserLoginName(loginName);
		File file = new File(avatarPath);
		if (!file.exists()) {
			throw new MxException("头像文件不存在 avatarPath = " + avatarPath);
		}
		Map<String, String> params = new HashMap<String, String>();
		params.put("auto_save", "true");
		Map<String, String> headers = new HashMap<String, String>();

		JSONArray arr = null;
		try {
			arr = this.post("api/v1/photos", params, headers, file);

			JSONObject o = arr.getJSONObject(0);
			return Integer.parseInt(o.get("id").toString()) > 0 ? true : false;
		} catch (Exception e) {
			return false;
		}

	}

	/**
	 * 获得某个用户的Id.
	 * 
	 * @param loginname
	 *            用户登录名
	 * @return 用户的Id.
	 */
	public Long getIdByLoginname(String loginname) {

		try {
			User u = findUserByLoginname(loginname);
			return u.getId();
		} catch (Exception e) {
			throw new MxException(e);
		}
	}

	/**
	 * 获得某个用户
	 * 
	 * @param loginname
	 * @return
	 */
	public User findUserByLoginname(String loginname) {
		return findUserByLoginname(null, loginname);
	}

//	/**
//	 * 得到某个部门下的全部用户
//	 * 
//	 * @param departmentCode
//	 *            部门代码
//	 * @param networkId
//	 *            网络部门
//	 * @return 用户的列表
//	 * 
//	 * @deprecated use getAllUsersInDepartment instead.
//	 */
//	public List<UserInfo> getAllUsersInDepartment(String networkId,
//			String departmentCode) {
//		ArrayList<UserInfo> users = new ArrayList<UserInfo>();
//		try {
//			JSONArray arrs = this.getJSONArray("/api/v1/departments/dept/"
//					+ departmentCode + "/" + networkId);
//			for (int i = 0; i < arrs.length(); i++) {
//				JSONObject o = (JSONObject) arrs.get(i);
//				UserInfo u = new UserInfo();
//				u.setAccount_id(o.getInt("account_id"));
//				u.setId(o.getInt("id"));
//				u.setName(o.getString("name"));
//				u.setLogin_name(o.getString("login_name"));
//				users.add(u);
//			}
//		} catch (JSONException e) {
//			throw new MxException("解析Json出错.", e);
//		}
//		return users;
//	}

	/**
	 * 得到某个部门下的全部用户,包括子部门和兼职用户
	 * 
	 * @param departmentCode
	 *            部门代码或者部门引用的Id
	 * @return 用户的列表
	 * 
	 */
	public List<UserInfo> getAllUsersInDepartment(String departmentCode,boolean includeSubDevision) {
		ArrayList<UserInfo> users = new ArrayList<UserInfo>();
		try {
			JSONArray arrs = this
					.getJSONArray("/api/v1/departments/all_users?dept_code="
							+ departmentCode+ "&include_subdivision=" + includeSubDevision);
			for (int i = 0; i < arrs.length(); i++) {
				JSONObject o = (JSONObject) arrs.get(i);
				UserInfo u = new UserInfo();
				u.setAccount_id(o.getInt("account_id"));
				u.setId(o.getInt("id"));
				u.setName(o.getString("name"));
				u.setLogin_name(o.getString("login_name"));
				users.add(u);
			}
		} catch (JSONException e) {
			throw new MxException("解析Json出错.", e);
		}
		return users;
	}

	/**
	 * 获得某个网络下的用户信息
	 * 
	 * @param network_name
	 *            网络名称，例如 abc.com
	 * @param loginname
	 *            要查询的用户的登录名称
	 * @return 账户对应的网络用户，如果找不到则抛出MxException.
	 */
	public User findUserByLoginname(String network_name, String loginname) {

		try {
			PostParameter[] params = null;
			PostParameter login_name_p = new PostParameter("login_name",
					loginname);
			if (network_name != null) {
				PostParameter network_name_p = new PostParameter(
						"network_name", network_name);
				params = new PostParameter[] { login_name_p, network_name_p };
			} else {
				params = new PostParameter[] { login_name_p };
			}

			JSONObject o = this.get("/api/v1/users/by_login_name", params);

			User user = null;
			if (o != null && o.getLong("id") != null && o.getLong("id") > 0) {
				user = new User();
				user.setId(o.getLong("id"));
				user.setLoginName(o.getString("login_name"));

				user.setEmail(o.getString("email"));
				user.setName(o.getString("name"));
				user.setTitle(o.getString("title"));
				user.setCellvoice1(o.getString("cellvoice1"));
				user.setCellvoice2(o.getString("cellvoice2"));
				user.setWorkvoice(o.getString("workvoice"));
				user.setEmpCode(o.getString("emp_code"));

				JSONArray depts = o.getJSONArray("departs");
				if (depts != null && depts.length() > 0) {
					Department[] allDept = new Department[depts.length()];
					for (int j = 0, n = depts.length(); j < n; j++) {
						JSONObject dobj = depts.getJSONObject(j);

						Department udept = new Department();
						udept.setCode(dobj.getString("dept_ref_id"));
						udept.setShortName(dobj.getString("dept_short_name"));
						udept.setFull_name(dobj.getString("dept_full_name"));
						udept.setTitle(dobj.getString("title"));
						udept.setDisplay_order(dobj.getString("display_order"));
						if (j == 0) {
							user.setDisplay_order(dobj
									.getString("display_order"));
						}
						allDept[j] = udept;
					}
					user.setAllDepartments(allDept);
				}
			}

			return user;
		} catch (JSONException e) {
			throw new MxException("解析Json出错.", e);
		}

	}

	public Department findDepartmentByDeptCode(String dept_code) {

		Department dept = null;

		try {

			JSONObject o = this.get("/api/v1/departments/" + dept_code
					+ "/by_dept_code");
			if (o.getString("dept_code") != null
					&& !"".equals(o.getString("dept_code"))) {
				dept = new Department();
				dept.setId(o.getLong("id"));
				dept.setDept_code(o.getString("dept_code"));
				dept.setShortName(o.getString("short_name"));
				dept.setFull_name(o.getString("full_name"));
				dept.setDisplay_order(o.getString("display_order"));
				dept.setParent_dept_code(o.getString("parent_dept_code"));
			}
			return dept;
		} catch (JSONException e) {
			throw new MxException("解析Json出错.", e);
		}
	}

	/**
	 * 获得全部的部门信息
	 * 
	 * @return
	 */
	public List<Department> getAllDepartments() {
		ArrayList<Department> departments = new ArrayList<Department>();
		try {
			JSONArray arrs = this.getJSONArray("/api/v1/networks/departments");
			for (int i = 0; i < arrs.length(); i++) {
				JSONObject o = (JSONObject) arrs.get(i);
				Department dept = new Department();
				dept.setId(o.getLong("id"));
				dept.setCode(o.getString("code"));
				dept.setFull_name(o.getString("full_name"));
				dept.setShortName(o.getString("short_name"));
				dept.setDisplay_order(o.getString("display_order"));
				dept.setLevel(o.getInt("level"));
				dept.setParentDeptId(o.getLong("parent_dept_id"));

				departments.add(dept);
			}
		} catch (JSONException e) {
			throw new MxException("解析Json出错.", e);
		}
		return departments;
	}

	protected List<User> getAllUsers(int page, int pageSize) {
		ArrayList<User> users = new ArrayList<User>();
		try {

			PostParameter p1 = new PostParameter("size",
					String.valueOf(pageSize));
			PostParameter p2 = new PostParameter("page", String.valueOf(page));
			PostParameter[] params = new PostParameter[] { p1, p2 };

			JSONArray arrs = this
					.getJSONArray("/api/v1/networks/users", params);

			Map<String, String> deptHash = new HashMap<String, String>();

			for (int i = 0; i < arrs.length(); i++) {
				JSONObject o = (JSONObject) arrs.get(i);
				User u = new User();
				u.setId(o.getLong("id"));
				u.setName(o.getString("name"));
				u.setLoginName(o.getString("login_name"));

				u.setCellvoice1(o.getString("cell_phone"));
				u.setCellvoice2(o.getString("preferred_mobile"));
				u.setEmail(o.getString("email"));
				u.setEmpCode(o.getString("dept_ref_id"));
				u.setNetworkId(o.getLong("network_id"));
				u.setRoleCode(o.getInt("role_code"));
				u.setSuspended(o.getBoolean("suspended"));
				u.setAvatarUrl(o.getString("avatar_url"));
				u.setEmpCode(o.getString("emp_code"));

				JSONArray depts = o.getJSONArray("departs");
				Department[] allDept = new Department[depts.length()];
				for (int j = 0, n = depts.length(); j < n; j++) {
					JSONObject dobj = depts.getJSONObject(j);

					Department udept = new Department();
					udept.setCode(dobj.getString("dept_code"));
					udept.setShortName(dobj.getString("dept_short_name"));
					udept.setFull_name(dobj.getString("dept_full_name"));
					udept.setTitle(dobj.getString("title"));
					udept.setDisplay_order(dobj.getString("display_order"));

					String code = udept.getCode();
					if (code != null && !code.equals("")
							&& !code.equals("null")) {
						if (deptHash.containsKey(code)) {
							udept.setParent_dept_code(deptHash.get(code));
						} else {
							JSONObject r = this.get("/api/v1/departments/"
									+ code + "/by_dept_code");
							String parent_code = r
									.getString("parent_dept_code");
							udept.setParent_dept_code(parent_code);
							deptHash.put(code, parent_code);
						}
					}

					allDept[j] = udept;
				}
				u.setAllDepartments(allDept);

				users.add(u);
			}
		} catch (JSONException e) {
			throw new MxException("解析Json出错.", e);
		}
		return users;
	}

	/**
	 * 导出全部的用户，包括了管理员，普通用户，公众号
	 * 
	 * @param pageSize
	 *            每次循环导出的用户大小，最大100
	 * @return UserPackage对象。
	 */
	public UserPackage exportUsers(int pageSize) {
		return new UserPackage(this, pageSize);
	}

	/**
	 * 给出多个loginName，返回login name 对应的用户列表.
	 * 
	 * @param network_name
	 * @param loginNames
	 * @return
	 */
	public User[] findUserByLoginNames(String[] loginNames) {

		try {

			if (loginNames == null || loginNames.length == 0) {
				return new User[] {};
			}

			PostParameter ssoKey = new PostParameter("sso_key", "login_name");
			StringBuilder loginNameString = new StringBuilder();
			for (int i = 0; i < loginNames.length; i++) {
				if (i > 0) {
					loginNameString.append(",");

				}
				loginNameString.append(loginNames[i]);
			}
			PostParameter ssoKeyValues = new PostParameter("key_values",
					loginNameString.toString());

			PostParameter[] params = new PostParameter[] { ssoKey, ssoKeyValues };

			JSONObject o = this.get("/api/v1/networks/about_user", params);
			JSONArray users = o.getJSONArray("items");
			ArrayList<User> userList = new ArrayList<User>();
			for (int i = 0; i < users.length(); i++) {
				JSONObject u = users.getJSONObject(i);
				User user = null;
				if (u.getLong("id") > 0) {
					user = new User();
					user.setId(u.getLong("id"));
					user.setLoginName(u.getString("login_name"));

					user.setEmail(u.getString("email"));
					user.setName(u.getString("name"));
					user.setTitle(u.getString("login_name"));
					user.setCellvoice1(u.getString("cellvoice1"));
					user.setCellvoice2(u.getString("cellvoice2"));
					user.setWorkvoice(u.getString("workvoice"));
					user.setEmpCode(u.getString("emp_code"));
				}

				if (user != null) {
					userList.add(user);
				}

			}

			return userList.toArray(new User[userList.size()]);

		} catch (JSONException e) {
			throw new MxException("解析Json出错.", e);
		}

	}

	/**
	 * 给出多个loginName，返回login name 对应的用户列表.
	 * 
	 * @param network_name
	 * @param loginNames
	 * @return
	 */
	public User[] findUserByIds(Long[] ids) {

		try {

			if (ids == null || ids.length == 0) {
				return new User[] {};
			}

			PostParameter ssoKey = new PostParameter("sso_key", "user_id");
			StringBuilder loginNameString = new StringBuilder();
			for (int i = 0; i < ids.length; i++) {
				if (i > 0) {
					loginNameString.append(",");

				}
				loginNameString.append(ids[i]);
			}
			PostParameter ssoKeyValues = new PostParameter("key_values",
					loginNameString.toString());

			PostParameter[] params = new PostParameter[] { ssoKey, ssoKeyValues };

			JSONObject o = this.get("/api/v1/networks/about_user", params);
			JSONArray users = o.getJSONArray("items");
			ArrayList<User> userList = new ArrayList<User>();
			for (int i = 0; i < users.length(); i++) {
				JSONObject u = users.getJSONObject(i);
				User user = null;
				if (u.getLong("id") > 0) {
					user = new User();
					user.setId(u.getLong("id"));
					user.setLoginName(u.getString("login_name"));

					user.setEmail(u.getString("email"));
					user.setName(u.getString("name"));
					user.setTitle(u.getString("login_name"));
					user.setCellvoice1(u.getString("cellvoice1"));
					user.setCellvoice2(u.getString("cellvoice2"));
					user.setWorkvoice(u.getString("workvoice"));
					user.setEmpCode(u.getString("emp_code"));
				}

				if (user != null) {
					userList.add(user);
				}

			}

			return userList.toArray(new User[userList.size()]);

		} catch (JSONException e) {
			throw new MxException("解析Json出错.", e);
		}

	}

	/**
	 * 根据用户给的查询条件，查询用户.
	 * 
	 * @param q
	 *            查询条件，用户姓名，pinyin，或者电话(至少5字符)
	 * @param limit
	 *            限制返回的数目。
	 * @return 查询到的用户列表
	 */
	public User[] searchUser(String q, int limit) {

		try {

			PostParameter query = new PostParameter("q", q);

			int _limit = 20;
			if (limit > 0) {
				_limit = limit;
			}
			PostParameter ret_limit = new PostParameter("limit",
					String.valueOf(_limit));

			PostParameter[] params = new PostParameter[] { query, ret_limit };

			JSONObject o = this.get("/api/v1/departments/search", params);
			JSONArray users = o.getJSONArray("items");
			ArrayList<User> userList = new ArrayList<User>();
			for (int i = 0; i < users.length(); i++) {
				JSONObject u = users.getJSONObject(i);
				User user = null;
				if (u.getLong("id") > 0) {
					user = new User();
					user.setId(u.getLong("id"));
					user.setLoginName(u.getString("login_name"));

					user.setEmail(u.getString("email"));
					user.setName(u.getString("name"));
					user.setTitle(u.getString("login_name"));
					user.setCellvoice1(u.getString("cellvoice1"));
					user.setCellvoice2(u.getString("cellvoice2"));
					user.setWorkvoice(u.getString("workvoice"));
					user.setEmpCode(u.getString("emp_code"));

					Department udept = new Department();
					udept.setCode(u.getString("dept_code"));
					udept.setId(u.getLong("dept_id"));
					udept.setFull_name(u.getString("dept_name"));
					user.setAllDepartments(new Department[] { udept });

				}

				if (user != null) {
					userList.add(user);
				}

			}

			return userList.toArray(new User[userList.size()]);

		} catch (JSONException e) {
			throw new MxException("解析Json出错.", e);
		}

	}

	
	/**
	 * 添加用户的联系人，请先使用setFromUserLoginName设置被添加人账户
	 * @param loginNames 增加的联系人登录名列表
	 */
	public void addUserContract(String[] loginNames) {

	}

	/**
	 * 删除用户的联系人，请先使用setFromUserLoginName设置被添加人账户
	 * @param loginNames 移除的联系人列表
	 */
	public void removeUserContract(String[] loginNames) {

	}

	/**
	 * 列出用户的常用联系人
	 * @return
	 */
	public User[] listUserContract() {
		return null;
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////
	// Create Conversation
	//
	/**
	 * 发送消息到会话中。需要调用setFromUserLoginname()设置发送者身份
	 * 
	 * @param login_names
	 *            创建会话的用户列表，不需要包括创建人自己
	 * @param message
	 *            消息内容,如果不提供，只会得到一条系统消息
	 * @return Conversation对象和对象的Id。
	 */
	public Conversation createConversation(String[] login_names, String message) {
		return createConversation(login_names, message, null);
	}

	/**
	 * 创建一个Graph的conversation。
	 * 
	 * @param login_names
	 *            创建会话的用户列表，不包括创建人自身.
	 * @param message
	 *            消息内容，如果不提供，则忽略这个参数。
	 * @param g
	 *            Graph对象，可以包含任何链接地址的对象.
	 * @return Conversation对象和对象的Id。
	 */
	public Conversation createConversationWithGraph(String[] login_names,
			String message, Graph g) {
		Map<String, String> params = new HashMap<String, String>();
		if (g != null) {
			params.put("title", g.getTitle());
			params.put("type", g.getType());
			params.put("url", g.getURL());
			params.put("image", g.getImageURL());
			params.put("app_url", g.getAppURL());
			params.put("description", g.getDescription());
		}

		Map<String, String> headers = new HashMap<String, String>();

		JSONObject return_json = this.post("/api/v1/graphs", params, headers)
				.asJSONObject();
		try {
			Long graph_id = return_json.getLong("id");
			if (graph_id != null && graph_id > 0) {
				return createConversation(login_names, message, graph_id);
			} else {
				throw new MxException("无效的Graph id:" + graph_id);
			}

		} catch (JSONException e) {
			throw new MxException("解析Json出错.", e);
		}

	}

	private Conversation createConversation(String[] login_names,
			String messageBody, Long graphId) {
		// 会话id，web上打开一个会话，从url里获取。比如社区管理员创建个群聊，里面邀请几个维护人员进来

		Map<String, String> params = new HashMap<String, String>();
		if (messageBody != null) {
			params.put("body", messageBody);
		}

		StringBuilder user_ids = new StringBuilder();
		for (int i = 0, n = login_names.length; i < n; i++) {
			User u = findUserByLoginname(null, login_names[i]);
			if (u != null) {
				if (i > 0) {
					user_ids.append(",");
				}

				user_ids.append(u.getId());

			}
		}

		params.put("direct_to_user_ids", user_ids.toString());

		if (graphId != null && graphId > 0) {
			params.put("attached[]", String.format("graph:%d", graphId));
		}

		Map<String, String> headers = new HashMap<String, String>();

		JSONObject return_json = this.post("/api/v1/conversations", params,
				headers).asJSONObject();

		Conversation created = null;
		try {
			JSONArray references_itmes = return_json.getJSONArray("references");
			for (int i = 0, n = references_itmes.length(); i < n; i++) {
				JSONObject r = references_itmes.getJSONObject(i);

				if ("conversation".equals(r.getString("type"))) {
					long convesation_id = r.getLong("id");
					created = new Conversation(convesation_id);
					break;
				}

			}

		} catch (JSONException e) {
			throw new MxException("解析Json出错.", e);
		}
		return created;
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////
	// Send messages
	//

	/**
	 * 发送消息到会话中。需要调用setFromUserLoginname()设置发送者身份
	 * 
	 * @param sender_login_name
	 *            发送用户的账户名字，该账户做为消息的发送人
	 * @param conversation_id
	 *            会话的Id
	 * @param message
	 *            消息内容
	 * @return
	 */
	public TextMessage sendConversationMessage(String conversation_id,
			String message) {
		// 会话id，web上打开一个会话，从url里获取。比如社区管理员创建个群聊，里面邀请几个维护人员进来

		Map<String, String> params = new HashMap<String, String>();
		params.put("body", message);
		Map<String, String> headers = new HashMap<String, String>();

		JSONObject return_json = this.post(
				"/api/v1/conversations/" + conversation_id + "/messages",
				params, headers).asJSONObject();

		try {
			return TextMessage.fromJSON(return_json.getJSONArray("items")
					.getJSONObject(0));
		} catch (JSONException e) {
			throw new MxException("解析Json出错.", e);
		}

	}

	/**
	 * 发送文件到会话中。需要调用setFromUserLoginname()设置发送者身份
	 * 
	 * @param conversation_id
	 * @param file
	 * @return
	 */
	public TextMessage sendConversationFileMessage(String conversation_id,
			File file) {
		long[] file_ids = uploadConversationFile(conversation_id, file);
		Map<String, String> params = new HashMap<String, String>();
		for (int i = 0; i < file_ids.length; i++) {
			params.put("attached[]",
					String.format("uploaded_file:%d", file_ids[i]));
		}
		Map<String, String> headers = new HashMap<String, String>();

		JSONObject return_json = this.post(
				"/api/v1/conversations/" + conversation_id + "/messages",
				params, headers).asJSONObject();
		try {
			return TextMessage.fromJSON(return_json.getJSONArray("items")
					.getJSONObject(0));
		} catch (JSONException e) {
			throw new MxException("解析Json出错.", e);
		}
	}

	/**
	 * 发送消息到工作圈中。需要调用setFromUserLoginname()设置发送者身份
	 * 
	 * @param groupId
	 * @param message
	 * @return
	 */
	public TextMessage sendTextMessageToGroup(long groupId, String message) {
		return sendTextMessageToGroup(groupId, message, null);
	}

	/**
	 * 发送分享消息到工作圈中。需要调用setFromUserLoginname()设置发送者身份
	 * 
	 * @param groupId
	 * @param message
	 * @param shareLink
	 * @return
	 */
	public TextMessage sendSharelinkToGroup(long groupId, String message,
			ShareLink shareLink) {
		return sendTextMessageToGroup(groupId, message, shareLink.toJson());
	}

	/**
	 * 发送消息到工作圈中。需要调用setFromUserLoginname()设置发送者身份
	 * 
	 * @param groupId
	 * @param message
	 * @param story
	 * @return
	 */
	public TextMessage sendTextMessageToGroup(long groupId, String message,
			String story) {

		Map<String, String> params = new HashMap<String, String>();
		params.put("group_id", String.valueOf(groupId));
		params.put("body", message);

		if (story != null) {
			params.put("story", story);
		}

		Map<String, String> headers = new HashMap<String, String>();

		JSONObject new_message = this.post("/api/v1/messages", params, headers)
				.asJSONObject();
		try {
			return TextMessage.fromJSON(new_message.getJSONArray("items")
					.getJSONObject(0));
		} catch (JSONException e) {
			throw new MxException("解析Json出错.", e);
		}

	}

	/**
	 * 发送消息到与某人的聊天中。需要调用setFromUserLoginname()设置发送者身份
	 * 
	 * @param u
	 * @param message
	 * @return
	 */
	public TextMessage sendMessageToUser(User u, String message) {
		// 会话id，web上打开一个会话，从url里获取。比如社区管理员创建个群聊，里面邀请几个维护人员进来
		if (u.getId() == null || u.getId() == 0) {
			String login_name = u.getLoginName();
			if (login_name == null) {
				throw new MxException("User参数缺少id或者loginName属性.");
			}
			User user = findUserByLoginname(login_name);
			if (user == null) {
				throw new MxException("找不到对应" + login_name + "的用户。");
			}

			u.setId(user.getId());
		}

		return sendMessageToUser(u.getId(), message);

	}

	/**
	 * 发送消息到与某人的聊天会话中。需要调用setFromUserLoginname()设置发送者身份
	 * 
	 * @param toUserId
	 * @param message
	 * @return
	 */
	public TextMessage sendMessageToUser(long toUserId, String message) {
		// 会话id，web上打开一个会话，从url里获取。比如社区管理员创建个群聊，里面邀请几个维护人员进来

		Map<String, String> params = new HashMap<String, String>();
		params.put("body", message);
		Map<String, String> headers = new HashMap<String, String>();

		JSONObject new_message = this.post(
				"/api/v1/conversations/to_user/" + toUserId, params, headers)
				.asJSONObject();
		try {
			return TextMessage.fromJSON(new_message.getJSONArray("items")
					.getJSONObject(0));
		} catch (JSONException e) {
			throw new MxException("解析Json出错.", e);
		}
	}

	public Long createOcuResource(String title, String sub_title,
			String author, String create_time, String pic_url, String content,
			String ocuId, String ocuSecret) {

		Map<String, String> params = new HashMap<String, String>();

		params.put("title", title);
		params.put("sub_title", sub_title);
		params.put("author", author);
		params.put("create_time", create_time);
		params.put("pic_url", pic_url);
		params.put("content", content);
		params.put("ocu_id", ocuId);
		params.put("ocu_secret", ocuSecret);
		Map<String, String> headers = new HashMap<String, String>();

		JSONObject result_json = this.post(
				"/api/v1/conversations/ocu_resources", params, headers)
				.asJSONObject();

		try {

			Long resource_id = result_json.getLong("resource_id");

			return resource_id;
		} catch (JSONException e) {
			throw new MxException("解析Json出错.", e);
		}

	}

	/**
	 * 发送公众号消息
	 * 
	 * @param toUserIds
	 *            用户的login_name数组，如果传null,则是给订阅的所有人发消息
	 * @param message
	 *            消息对象数据，可以是复杂文本，也可以是简单对象
	 * @param ocuId
	 *            公众号的id
	 * @param ocuSecret
	 *            公众号的秘钥，校验是否可以发送
	 * @return
	 */
	public OcuMessageSendResult sendOcuMessageToUsers(String[] toUserIds,
			Message message, String ocuId, String ocuSecret) {
		return sendOcuMessageToUsers(null, toUserIds, message, ocuId, ocuSecret);

	}

	/**
	 * 发送公众号消息,指定社区id
	 * 
	 * @param toUserIds
	 *            用户的login_name数组，如果传null,则是给订阅的所有人发消息
	 * @param network_id
	 *            用户的社区
	 * @param message
	 *            消息对象数据，可以是复杂文本，也可以是简单对象
	 * @param ocuId
	 *            公众号的id
	 * @param ocuSecret
	 *            公众号的秘钥，校验是否可以发送
	 * @return
	 */
	public OcuMessageSendResult sendOcuMessageToUsers(String network_id,
			String[] toUserIds, Message message, String ocuId, String ocuSecret) {
		String direct_to_user_ids = "";

		if (message instanceof ArticleMessage) {
			Resource res = ((ArticleMessage) message).getMessageResource();
			if (res != null && res.getId() == null) {
				Long res_id = createOcuResource(res.getTitle(),
						res.getSubTitle(), res.getAuthor(),
						res.getCreateTime(), res.getPicUrl(), res.getContent(),
						ocuId, ocuSecret);
				res.setId(res_id);
			}
		}

		Map<String, String> params = new HashMap<String, String>();

		params.put("body", message.getBody());
		params.put("content_type", String.valueOf(message.messageType()));

		if (toUserIds != null && toUserIds.length > 0) {
			StringBuffer sb = new StringBuffer(toUserIds[0]);
			for (int i = 1; i < toUserIds.length; i++) {
				sb.append(",").append(toUserIds[i]);

			}
			direct_to_user_ids = sb.toString();
		}

		if (network_id != null)
			params.put("network_id", network_id);
		params.put("direct_to_user_ids", direct_to_user_ids);
		params.put("ocu_id", ocuId);
		params.put("ocu_secret", ocuSecret);
		Map<String, String> headers = new HashMap<String, String>();

		JSONObject result_json = this.post(
				"/api/v1/conversations/ocu_messages", params, headers)
				.asJSONObject();

		try {
			int count = result_json.getInt("count");
			Long messageId = result_json.getLong("message_id");
			JSONArray user_ids_json = result_json.getJSONArray("to_user_ids");

			Long[] user_ids = null;
			if (user_ids_json != null) {
				user_ids = new Long[user_ids_json.length()];

				for (int i = 0; i < user_ids.length; i++) {
					user_ids[i] = user_ids_json.getLong(i);
				}
			}

			OcuMessageSendResult result = new OcuMessageSendResult(count,
					messageId, user_ids);
			return result;
		} catch (JSONException e) {
			throw new MxException("解析Json出错.", e);
		}

	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//

	/**
	 * 创建任意用户的Web端 SSOToken,使用这个API，需要接入端能够拥有创建SSOToken的权限
	 * 
	 * @param loginName
	 *            需要创建token的账户loginName.
	 * @return 正常调用将返回 Web端的SSOToken.
	 */
	public String createMXSSOToken(String loginName) {

		Map<String, String> params = new HashMap<String, String>();
		params.put("login_name", loginName);

		Map<String, String> headers = new HashMap<String, String>();

		try {
			JSONObject json = this.post("/api/v1/oauth/mx_sso_token", params,
					headers).asJSONObject();
			return json.getString("token");

		} catch (JSONException e) {
			throw new MxException(e);
		}

	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//

	/**
	 * 向移动设备推送自定义的消息
	 * 
	 * @param user_ids
	 *            发送的消息，文本格式，使用','分割，例如'1,2,3'
	 * @param message
	 *            发送的消息，文本格式，可以自定内容的编码，系统会将内容发送到接受的移动设备上。
	 * @param alert
	 *            iOS通知栏消息，对Android无效，走Apple的Apn发送出去。文本格式,例如'您收到一条新消息'
	 * @param alert_extend
	 *            iOS apn推送的隐藏字段，放在custom字段,
	 *            json的字段,例如:"{'a': '1920-10-11 11:20'}"。
	 * @return 实际发送了多少个用户，user_ids中有无效的用户将被剔除。
	 * @throws ApiErrorException
	 *             当调用数据出错时抛出。
	 */
	public int pushMessage(String user_ids, String message, String alert,
			String alert_extend) throws ApiErrorException {

		try {

			HashMap<String, String> params = new HashMap<String, String>();
			params.put("to_user_ids", user_ids);
			params.put("message", message);
			params.put("alert", alert);
			params.put("alert_extend", alert_extend);

			Map<String, String> headers = new HashMap<String, String>();

			JSONObject json_result = post("/api/v1/push", params, headers)
					.asJSONObject();
			int send_to = json_result.getInt("send_count");

			return send_to;

		} catch (JSONException e) {
			throw new ApiErrorException("返回JSON错误", 500, e);
		}

	}

	/**
	 * 
	 * @param loginName
	 * @param message
	 * @return 产生的消息id。可以用来追踪消息
	 * @throws ApiErrorException
	 */
	public int pushAppMessage(String appId, String loginName, AppMessage message)
			throws ApiErrorException {

		try {

			HashMap<String, String> params = new HashMap<String, String>();
			params.put("login_name", loginName);
			params.put("message", message.getBody());

			Map<String, String> headers = new HashMap<String, String>();
			StringBuilder sb = new StringBuilder("/api/v1/push/apps/");

			JSONObject json_result = post(
					sb.append(appId).append("/messages").toString(), params,
					headers).asJSONObject();
			int mid = json_result.getInt("message_id");

			return mid;

		} catch (JSONException e) {
			throw new ApiErrorException("返回JSON错误", 500, e);
		}

	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//

	/**
	 * 向移动设备推送自定义的消息,根据给出来的app id,向下载App的全部用户推送消息。
	 * 
	 * @param appId
	 *            将消息发送给全部app下载用户的appId。
	 * @param message
	 *            发送的消息，文本格式，可以自定内容的编码，系统会将内容发送到接受的移动设备上。
	 * @param alert
	 *            iOS通知栏消息，对Android无效，走Apple的Apn发送出去。文本格式,例如'您收到一条新消息'
	 * @param alert_extend
	 *            iOS apn推送的隐藏字段，放在custom字段,
	 *            json的字段,例如:"{'a': '1920-10-11 11:20'}"。
	 * @return 实际发送了多少个用户，user_ids中有无效的用户将被剔除。
	 * @throws ApiErrorException
	 *             当调用数据出错时抛出。
	 */
	public int pushMessageToAllAppUsers(int appId, String message,
			String alert, String alert_extend) throws ApiErrorException {

		try {

			HashMap<String, String> params = new HashMap<String, String>();

			params.put("message", message);
			params.put("alert", alert);
			params.put("alert_extend", alert_extend);

			Map<String, String> headers = new HashMap<String, String>();

			JSONObject json_result = post("/api/v1/push/apps/" + appId, params,
					headers).asJSONObject();
			int send_to = json_result.getInt("send_count");

			return send_to;

		} catch (JSONException e) {
			throw new ApiErrorException("返回JSON错误", 500, e);
		}

	}

	/**
	 * 向移动设备推送自定义的消息,根据给出来的app id,向下载App的全部用户推送消息。
	 * 
	 * @param appId
	 *            将消息发送给全部app下载用户的appId。
	 * @param message
	 *            发送的消息，文本格式，可以自定内容的编码，系统会将内容发送到接受的移动设备上。
	 * @param alert
	 *            iOS通知栏消息，对Android无效，走Apple的Apn发送出去。文本格式,例如'您收到一条新消息'
	 * @param alert_extend
	 *            iOS apn推送的隐藏字段，放在custom字段,
	 *            json的字段,例如:"{'a': '1920-10-11 11:20'}"。
	 * @return 实际发送了多少个用户，user_ids中有无效的用户将被剔除。
	 * @throws ApiErrorException
	 *             当调用数据出错时抛出。
	 */
	public int pushMessageToAllDepartmentUsers(String departmentCode,
			String message, String alert, String alert_extend)
			throws ApiErrorException {

		try {

			HashMap<String, String> params = new HashMap<String, String>();

			params.put("message", message);
			params.put("alert", alert);
			params.put("alert_extend", alert_extend);

			Map<String, String> headers = new HashMap<String, String>();

			JSONObject json_result = post(
					"/api/v1/push/department/" + departmentCode, params,
					headers).asJSONObject();
			int send_to = json_result.getInt("send_count");

			return send_to;

		} catch (JSONException e) {
			throw new ApiErrorException("返回JSON错误", 500, e);
		}

	}

	/**
	 * 获得一个会话的全部消息消息的消息文本，第一条消息就是主消息。
	 * 
	 * @param threadId
	 *            会话的Id
	 * @return Message的数组
	 * @throws ApiErrorException
	 */
	public Message[] getAllMessagesInThread(Long threadId)
			throws ApiErrorException {

		Map<String, String> params = new HashMap<String, String>();
		params.put("limit", "500");
		params.put("with_starter", "true");
		JSONObject o = get("/api/v1/messages/in_thread/" + threadId, params)
				.asJSONObject();
		try {
			JSONArray items = o.getJSONArray("items");
			Message[] messages = new Message[items.length()];
			for (int i = 0; i < items.length(); i++) {
				Message m = TextMessage.fromJSON(items.getJSONObject(i));
				messages[i] = m;
			}
			return messages;
		} catch (JSONException e) {
			throw new ApiErrorException("返回JSON错误", 500, e);
		}

	}

	/**
	 * 人员组织同步接口，增加机构部门
	 * 
	 * @param departement
	 * @return
	 * @throws ApiErrorException
	 */
	public Department createDepartment(Department departement)
			throws ApiErrorException {

		try {

			HashMap<String, String> params = departement.toHash();
			Map<String, String> headers = new HashMap<String, String>();

			JSONObject json_result = post("/api/v1/departments", params,
					headers).asJSONObject();
			int code = json_result.getInt("code");

			if (code > 0 && code != 200 && code != 201) {

				String msg = json_result.getString("message");
				throw new ApiErrorException(code, msg);

			}

			departement.setId(json_result.getLong("id"));
			departement.setNetwork_name(json_result.getString("network_name"));

			return departement;

		} catch (JSONException e) {
			throw new ApiErrorException("返回JSON错误", 500, e);
		}

	}

	/**
	 * 人员组织同步接口，更新部门数据
	 * 
	 * @param departement
	 *            更新的部门对象
	 * @throws ApiErrorException
	 */
	public Department updateDepartment(Department departement)
			throws ApiErrorException {

		try {

			HashMap<String, String> params = departement.toHash();

			JSONObject json_result = put(
					"/api/v1/departments/" + departement.getDept_code(), params);

			int code = json_result.getInt("code");

			if (code > 0 && code != 200 && code != 201) {

				String msg = json_result.getString("message");
				throw new ApiErrorException(code, msg);

			}
			departement.setId(json_result.getLong("id"));
			departement.setNetworkId(json_result.getLong("network_id"));

			return departement;

		} catch (JSONException e) {
			throw new ApiErrorException("返回JSON错误", 500, e);
		}

	}

	/**
	 * 人员组织同步接口，删除某个部门
	 * 
	 * @param departmentCode
	 *            需要删除的部门代码
	 * @param deleteWithUsers
	 *            是否连同部门下的人员一起删除
	 * @throws ApiErrorException
	 */

	public void deleteDepartment(String departmentCode, boolean deleteWithUsers)
			throws ApiErrorException {

		try {

			HashMap<String, String> params = new HashMap<String, String>();
			if (deleteWithUsers) {
				params.put("force", "true");
			}

			JSONObject json_result = delete("/api/v1/departments/"
					+ departmentCode, params);
			int code = json_result.getInt("code");

			if (code != 200 && code != 201) {

				String msg = json_result.getString("message");
				throw new ApiErrorException(code, msg);

			}

		} catch (JSONException e) {
			throw new ApiErrorException("返回JSON错误", 500, e);
		}

	}

	/**
	 * 人员组织同步接口，增加用户
	 * 
	 * @param user
	 * @return
	 * @throws ApiErrorException
	 */
	public User addNewUser(User user) throws ApiErrorException {

		try {

			HashMap<String, String> params = user.toHash();
			Map<String, String> headers = new HashMap<String, String>();

			JSONObject json_result = post("/api/v1/users", params, headers)
					.asJSONObject();
			int code = json_result.getInt("code");

			if (code > 0 && code != 200 && code != 201) {

				String msg = json_result.getString("message");
				throw new ApiErrorException(code, msg);

			}
			user.setId(json_result.getLong("id"));
			return user;

		} catch (JSONException e) {
			throw new ApiErrorException("返回JSON错误", 500, e);
		}

	}

	/**
	 * 人员组织同步接口，增加用户
	 * 
	 * @param user
	 * @return
	 * @throws ApiErrorException
	 */

	/**
	 * 为用户新增一个兼职部门
	 * 
	 * @param userLoginName
	 *            要处理的用户
	 * @param departmentCode
	 *            兼职部门的code
	 * @param displayOrder
	 *            用户在兼职部门的显示顺序，必须是一个整数，例如“20”,如果不是数字，则被设置为0。
	 * @param title
	 *            兼职部门的职务
	 * @return true 如果创建成功, 失败则抛出异常
	 * @throws ApiErrorException
	 */
	public boolean addUserDepartment(String userLoginName,
			String departmentCode, String displayOrder, String title)
			throws ApiErrorException {

		try {

			User user = this.findUserByLoginname(userLoginName);
			if (user == null) {
				throw new ApiErrorException(400, "无法找到用户:" + userLoginName);
			}

			HashMap<String, String> params = new HashMap<String, String>();

			params.put("dept_code", departmentCode);
			params.put("display_order", displayOrder);
			params.put("title", title);

			Map<String, String> headers = new HashMap<String, String>();

			JSONObject json_result = post(
					"/api/v1/users/" + user.getId() + "/departments", params,
					headers).asJSONObject();
			int code = json_result.getInt("code");

			if (code > 0 && code != 200 && code != 201) {
				String msg = json_result.getString("message");
				throw new ApiErrorException(code, msg);
			}

			return true;

		} catch (JSONException e) {
			throw new ApiErrorException("返回JSON错误", 500, e);
		}

	}

	/**
	 * 删除用户的部门或者兼职部门
	 * 
	 * @param userLoginName
	 *            要删除用户的登录名称
	 * @param departmentCode
	 *            需要删除的部门代码
	 * @return
	 * @throws ApiErrorException
	 */
	public boolean removeUserDepartment(String userLoginName,
			String departmentCode) throws ApiErrorException {

		try {

			User user = this.findUserByLoginname(userLoginName);
			if (user == null) {
				throw new ApiErrorException(400, "无法找到用户:" + userLoginName);
			}

			HashMap<String, String> params = new HashMap<String, String>();
			JSONObject json_result = delete("/api/v1/users/" + user.getId()
					+ "/departments/" + departmentCode, params);
			int code = json_result.getInt("code");

			if (code > 0 && code != 200 && code != 201) {
				String msg = json_result.getString("message");
				throw new ApiErrorException(code, msg);
			}

			return true;

		} catch (JSONException e) {
			throw new ApiErrorException("返回JSON错误", 500, e);
		}

	}

	/**
	 * 人员组织同步接口，更新用户
	 * 
	 * @param user
	 * @throws ApiErrorException
	 */
	public void updateUser(User user) throws ApiErrorException {

		HashMap<String, String> params = user.toHash();
		put("/api/v1/users/" + user.getId(), params);

	}

	/**
	 * 人员组织同步接口，如果一个用户在多个社区里，该接口只删除指定社区的用户信息
	 * 
	 * @param user
	 * @throws ApiErrorException
	 */
	public void deleteUser(User user) throws ApiErrorException {
		deleteUser(user, false);
	}

	/**
	 * 人员组织同步接口，删除该用户所有社区的信息
	 * 
	 * @param user
	 * @throws ApiErrorException
	 */
	public void deleteUserWithAccount(User user) throws ApiErrorException {
		deleteUser(user, true);
	}

	/**
	 * 根据loginname删除用户
	 * 
	 * @param loginName
	 * @throws ApiErrorException
	 */
	public void deleteUserByLoginName(String loginName)
			throws ApiErrorException {
		User u = new User();
		u.setLoginName(loginName);
		deleteUser(u, false);
	}

	private void deleteUser(User user, boolean withDeleteAccount)
			throws ApiErrorException {

		try {

			HashMap<String, String> params = new HashMap<String, String>();
			params.put("login_name", user.getLoginName());
			if (withDeleteAccount) {
				params.put("with_account", "true");
			}

			JSONObject json_result = delete("/api/v1/users/" + user.getId(),
					params);
			int code = json_result.getInt("code");

			if (code != 200 && code != 201) {

				String msg = json_result.getString("message");
				throw new ApiErrorException(code, msg);

			}

		} catch (JSONException e) {
			throw new ApiErrorException("返回JSON错误", 500, e);
		}

	}

	/**
	 * 创建社区
	 * 
	 * @param network
	 * @return
	 * @throws ApiErrorException
	 */
	public Network createNetwork(Network network) throws ApiErrorException {

		try {

			HashMap<String, String> params = network.toHash();
			Map<String, String> headers = new HashMap<String, String>();

			JSONObject json_result = post("/api/v1/networks", params, headers)
					.asJSONObject();
			int code = json_result.getInt("code");

			if (code > 0 && code != 200 && code != 201) {

				String msg = json_result.getString("message");
				throw new ApiErrorException(code, msg);

			}

			network.setId(json_result.getLong("id"));
			return network;

		} catch (JSONException e) {
			throw new ApiErrorException("返回JSON错误", 500, e);
		}

	}

	/**
	 * 更新社区
	 * 
	 * @param network
	 * @throws ApiErrorException
	 */
	public void updateNetwork(Network network) throws ApiErrorException {

		try {

			HashMap<String, String> params = network.toHash();

			JSONObject json_result = put("/api/v1/networks", params);

			int code = json_result.getInt("code");

			if (code != 200 && code != 201) {

				String msg = json_result.getString("message");
				throw new ApiErrorException(code, msg);

			}

		} catch (JSONException e) {
			throw new ApiErrorException("返回JSON错误", 500, e);
		}

	}

	/**
	 * 删除社区
	 * 
	 * @param name
	 * @throws ApiErrorException
	 */
	public void deleteNetwork(String name) throws ApiErrorException {

		try {

			HashMap<String, String> params = new HashMap<String, String>();
			params.put("name", name);

			JSONObject json_result = delete("/api/v1/networks", params);
			int code = json_result.getInt("code");

			if (code != 200 && code != 201) {
				String msg = json_result.getString("message");
				throw new ApiErrorException(code, msg);

			}

		} catch (JSONException e) {
			throw new ApiErrorException("返回JSON错误", 500, e);
		}

	}

	/**
	 * 校验应用商店的应用携带的SSOTOken是否有效，通过连接minxing服务器，检查token代表的敏行用户的身份。
	 * 
	 * @param token
	 *            客户端提供的SSO Token。几种获取方式
	 *            1.第三方系统如果和敏行属于一个域下，比如类似的*.minxin.com，可以从cookie获取mx_sso_token
	 *            2.第三方系统可以从HttpServletRequest的parameter中获取mx_sso_token
	 *            3.第三方系统可以从HttpServletRequest的header中获取mx_sso_token
	 * @param app_id
	 *            校验客户端提供的Token是不是来自这个app_id产生的，如果不是，则校验失败。
	 * @return 如果校验成功，返回token对应的用户信息
	 * @throws MxVerifyException
	 *             校验失败，则抛出这个异常.
	 */
	public User verifyAppSSOToken(String token, String app_id)
			throws MxVerifyException {

		try {
			JSONObject o = this.get("/api/v1/oauth/user_info/" + token);

			String by_app_id = o.getString("by_app_id");
			String by_ocu_id = o.getString("by_ocu_id");

			if (app_id != null && !app_id.equals(by_app_id)) {
				if (by_ocu_id != null) {
					throw new MxVerifyException("校验Token:" + token
							+ "错误, token是ocu_id:" + by_ocu_id
							+ "创建的,但期望的是app_id:" + app_id);
				} else {
					throw new MxVerifyException("校验Token:" + token
							+ "错误, token创建的AppId为" + by_app_id + ",但期望的是:"
							+ app_id);
				}

			}

			return getUser(o);
		} catch (JSONException e) {
			throw new MxVerifyException("校验Token:" + token + "错误", e);
		}

	}

	/**
	 * 校验公众号消息打开时携带的 SSOTOken，通过连接minxing服务器，检查token代表的敏行用户的身份。
	 * 
	 * @param token
	 *            客户端提供的SSO Token.几种获取方式
	 *            1.第三方系统如果和敏行属于一个域下，比如类似的*.minxin.com，可以从cookie获取mx_sso_token
	 *            2.第三方系统可以从HttpServletRequest的parameter中获取mx_sso_token
	 *            3.第三方系统可以从HttpServletRequest的header中获取mx_sso_token
	 * @param app_id
	 *            校验客户端提供的Token是不是来自这个app_id产生的，如果不是，则校验失败。
	 * @return 如果校验成功，返回token对应的用户信息
	 * @throws MxVerifyException
	 *             校验失败，则抛出这个异常.
	 */

	public User verifyOcuSSOToken(String token, String ocu_id)
			throws MxVerifyException {

		try {
			JSONObject o = this.get("/api/v1/oauth/user_info/" + token);

			String by_ocu_id = o.getString("by_ocu_id");
			String by_app_id = o.getString("by_app_id");

			if (ocu_id != null && !ocu_id.equals(by_ocu_id)) {

				if (by_app_id != null) {
					throw new MxVerifyException("校验Token:" + token
							+ "错误, token是app_id:" + by_app_id
							+ "创建的,但期望的是ocu_id:" + ocu_id);
				} else {
					throw new MxVerifyException("校验Token:" + token
							+ "错误, token创建的ocu_id为" + by_ocu_id
							+ ",但期望的是ocu_id:" + ocu_id);
				}

			}
			return getUser(o);
		} catch (JSONException e) {
			throw new MxVerifyException("JSON parse error", e);
		}

	}

	/**
	 * 校验应用商店的应用携带的SSOTOken是否有效，通过连接minxing服务器，检查token代表的敏行用户的身份。
	 * 
	 * @param token
	 *            客户端提供的SSO Token。几种获取方式
	 *            1.第三方系统如果和敏行属于一个域下，比如类似的*.minxin.com，可以从cookie获取mx_sso_token
	 *            2.第三方系统可以从HttpServletRequest的parameter中获取mx_sso_token
	 *            3.第三方系统可以从HttpServletRequest的header中获取mx_sso_token
	 * @return 如果校验成功，返回token对应的用户信息
	 * @throws MxVerifyException
	 *             校验失败，则抛出这个异常.
	 */
	public User verifySSOToken(String token) throws MxVerifyException {

		try {
			JSONObject o = this.get("/api/v1/oauth/user_info/" + token);
			return getUser(o);
		} catch (JSONException e) {
			throw new MxVerifyException("校验Token:" + token + "错误", e);
		}

	}

	/**
	 * 
	 * @param login_name
	 * @param password
	 * @return
	 * @throws MxVerifyException
	 */
	public boolean verifyPassword(String login_name, String password)
			throws MxVerifyException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("login_name", login_name);
		params.put("password", password);
		Map<String, String> headers = new HashMap<String, String>();
		try {
			Response o = this.post("/api/v1/oauth/verify_password", params,
					headers);
			JSONObject json = o.asJSONObject();
			if ("success".equals(json.getString("status"))) {
				return true;
			}
			return false;
		} catch (JSONException e) {
			throw new MxVerifyException("Verify password failed!", e);
		}

	}

	private User getUser(JSONObject o) throws JSONException {
		User user = new User();
		user.setId(o.getLong("user_id"));
		user.setLoginName(o.getString("login_name"));

		user.setEmail(o.getString("email"));
		user.setName(o.getString("name"));
		user.setTitle(o.getString("login_name"));
		user.setCellvoice1(o.getString("cell_phone"));
		user.setCellvoice2(o.getString("preferred_mobile"));

		user.setEmpCode(o.getString("emp_code"));
		user.setNetworkId(o.getLong("network_id"));

		JSONArray depts = o.getJSONArray("departs");
		Department[] allDept = new Department[depts.length()];
		for (int i = 0, n = depts.length(); i < n; i++) {
			JSONObject dobj = depts.getJSONObject(i);

			Department udept = new Department();
			udept.setCode(dobj.getString("dept_ref_id"));
			udept.setShortName(dobj.getString("dept_short_name"));
			udept.setFull_name(dobj.getString("dept_full_name"));
			allDept[i] = udept;
		}
		user.setAllDepartments(allDept);
		return user;
	}

	/**
	 * 校验一下URL上的签名信息，确认这个请求来自敏行的服务器
	 * 
	 * @param queryString
	 *            url的query String部分，例如 http://g.com?abc=1&de=2 的url，query
	 *            string 为abc=1&de=2
	 * @param securet
	 *            ocu或者app的 securet。
	 * @return true 如果签名被认证。
	 */
	public boolean verifyURLSignature(String queryString, String secret) {

		String signed = null;
		String timestamp = null;
		String nonce = null;
		String mx_sso_token = null;
		String login_name = null;

		String qstring = queryString;
		if (queryString.startsWith("http://")
				|| queryString.startsWith("https://")) {

			qstring = URIUtil.getQuery(queryString);
		}

		ParameterParser pp = new ParameterParser();

		@SuppressWarnings("unchecked")
		List<NameValuePair> list = (List<NameValuePair>) pp.parse(qstring, '&');

		try {

			for (NameValuePair np : list) {

				if (np.getName().equals("timestamp")) {
					timestamp = URIUtil.decode(np.getValue());
					continue;
				}

				if (np.getName().equals("nonce")) {
					nonce = URIUtil.decode(np.getValue());
					continue;
				}

				if (np.getName().equals("login_name")) {
					login_name = URIUtil.decode(np.getValue());
					continue;
				}

				if (np.getName().equals("mx_sso_token")) {
					mx_sso_token = URIUtil.decode(np.getValue());
					continue;
				}

				if (np.getName().equals("signed")) {
					signed = URIUtil.decode(np.getValue());
					continue;
				}
			}

		} catch (URIException e) {
			throw new MxException("Query string not valid:" + queryString, e);
		}

		StringBuilder sb = new StringBuilder();
		sb.append(timestamp).append(":").append(nonce).append(":")
				.append(login_name).append(":").append(mx_sso_token);

		String t = HMACSHA1.getSignature(sb.toString(), secret);
		return t.equals(signed);

	}

	/**
	 * 创建工作圈。
	 * 
	 * @param name
	 *            工作圈的名字
	 * @param description
	 *            工作圈的名字
	 * @param isPublic
	 *            公开的还是私有的工作圈，true创建公开的工作圈，false：创建私有的工作圈
	 * @param groupType
	 *            工作圈的类型，Group.SUPPORT， Group.NORMAL,表示咨询组，普通类型的组
	 * @return 如果创建成功，则返回创建成功的组信息。如果失败抛出 ApiErrorException。
	 * @throws ApiErrorException
	 */
	public Group createGroup(String name, String description, boolean isPublic,
			String groupType) throws ApiErrorException {
		return createGroup(name, description, isPublic, groupType, false, 0);
	}

	/**
	 * 创建工作圈。
	 * 
	 * @param name
	 *            工作圈的名字
	 * @param description
	 *            工作圈的名字
	 * @param isPublic
	 *            公开的还是私有的工作圈，true创建公开的工作圈，false：创建私有的工作圈
	 * @param groupType
	 *            工作圈的类型，Group.SUPPORT， Group.NORMAL,表示咨询组，普通类型的组
	 * @param hidden
	 *            是否隐藏，仅对私有组生效。
	 * @param limteSize
	 *            组内成员数限制.
	 * @return 如果创建成功，则返回创建成功的组信息。如果失败抛出 ApiErrorException。
	 * @throws ApiErrorException
	 */
	public Group createGroup(String name, String description, boolean isPublic,
			String groupType, boolean hidden, int limteSize)
			throws ApiErrorException {
		try {

			HashMap<String, String> params = new HashMap<String, String>();
			params.put("name", name);

			boolean isHidden = false;
			boolean isSupportGroup = false;

			if (isPublic) {
				params.put("public", "true");
			} else {
				params.put("public", "false");
				params.put("moderated", "true");
			}

			if (hidden) {
				params.put("hidden", "true");
				isHidden = true;
			}

			if (Group.SUPPORT.equals(groupType)) {
				params.put("group_type", "support");
				isSupportGroup = true;
			}

			params.put("limit_size", String.valueOf(limteSize));
			Map<String, String> headers = new HashMap<String, String>();

			Response respone = post("/api/v1/groups", params, headers);

			if (respone.getStatusCode() != 200
					&& respone.getStatusCode() != 201) {

				throw respone.getApiError();

			}
			JSONArray json_result = respone.asJSONArray(); // 设计有问题，应该返回一个对象
			Long groupId = json_result.getJSONObject(0).getLong("id");
			Group g = new Group(groupId, name, description, isPublic,
					isSupportGroup, isHidden);
			return g;
		} catch (JSONException e) {
			throw new ApiErrorException("返回JSON错误", 500, e);
		}

	}

	/**
	 * 为群组增加管理员
	 * 
	 * @param departement
	 * @return
	 * @throws ApiErrorException
	 */

	/**
	 * 为群组增加管理人员
	 * 
	 * @param groupId
	 *            群组的Id
	 * @param loginNames
	 *            人员的登录名
	 * @throws ApiErrorException
	 *             如果执行失败，抛出异常
	 */
	public void addGroupAdmin(Long groupId, String[] loginNames)
			throws ApiErrorException {

		HashMap<String, String> params = new HashMap<String, String>();
		User[] users = this.findUserByLoginNames(loginNames);

		if (users != null && users.length > 0) {
			StringBuilder user_ids = new StringBuilder();
			for (int i = 0; i < users.length; i++) {
				if (i > 0) {
					user_ids.append(",");
				}
				user_ids.append(users[i].getId());

			}
			params.put("user_ids", user_ids.toString());
		}

		Map<String, String> headers = new HashMap<String, String>();

		post("/api/v1/groups/" + groupId + "/admins", params, headers)
				.asJSONObject();

	}

	/**
	 * 为群组增加管理人员
	 * 
	 * @param groupId
	 *            群组的Id
	 * @param loginNames
	 *            人员的登录名
	 * @throws ApiErrorException
	 *             如果执行失败，抛出异常
	 */
	public void addGroupMember(Long groupId, String[] loginNames)
			throws ApiErrorException {

		HashMap<String, String> params = new HashMap<String, String>();
		User[] users = this.findUserByLoginNames(loginNames);

		if (users != null && users.length > 0) {
			StringBuilder user_ids = new StringBuilder();
			for (int i = 0; i < users.length; i++) {
				if (i > 0) {
					user_ids.append(",");
				}
				user_ids.append(users[i].getId());

			}
			params.put("user_ids", user_ids.toString());
		}

		Map<String, String> headers = new HashMap<String, String>();

		post("/api/v1/groups/" + groupId + "/members", params, headers)
				.asJSONObject();

	}

	/**
	 * 将部门放入群组中
	 * 
	 * @param groupId
	 *            群组Id.
	 * @param department_codes
	 *            部门代码,每个部门的唯一编码，创建部门时候提供的
	 * @throws ApiErrorException
	 *             如果执行失败，抛出该错误。
	 */
	public void addGroupDepartmentMember(Long groupId, String[] department_codes)
			throws ApiErrorException {

		HashMap<String, String> params = new HashMap<String, String>();

		if (department_codes != null && department_codes.length > 0) {
			StringBuilder dept_ids = new StringBuilder();
			for (int i = 0; i < department_codes.length; i++) {
				if (i > 0) {
					dept_ids.append(",");
				}

				Department dept = findDepartmentByDeptCode(department_codes[i]);
				dept_ids.append(dept.getId());

			}
			params.put("dept_ids", dept_ids.toString());
		}

		Map<String, String> headers = new HashMap<String, String>();

		post("/api/v1/groups/" + groupId + "/members", params, headers)
				.asJSONObject();

	}

	/**
	 * 
	 * @param groupId
	 * @param loginNames
	 */

	public void removeGroupAdmin(long groupId, String[] loginNames) {

		HashMap<String, String> params = new HashMap<String, String>();
		User[] users = this.findUserByLoginNames(loginNames);

		if (users != null && users.length > 0) {
			StringBuilder user_ids = new StringBuilder();
			for (int i = 0; i < users.length; i++) {
				if (i > 0) {
					user_ids.append(",");
				}
				user_ids.append(users[i].getId());
				delete("/api/v1/groups/" + groupId + "/admins/"
						+ users[i].getId(), params);
			}

		}

	}

	/**
	 * 列出专家支持组
	 * 
	 * @return 专家支持组的列表
	 * @throws ApiErrorException
	 */
	public Group[] getSupportTypeGroups() throws ApiErrorException {
		return getGroups(true);
	}

	public Group[] getGroups(boolean only_support_type)
			throws ApiErrorException {

		HashMap<String, String> params = new HashMap<String, String>();
		if (only_support_type) {
			params.put("only_support_type", "true");
		}

		JSONObject o = this.get("/api/v1/groups", params).asJSONObject();

		ArrayList<Group> userList = new ArrayList<Group>();

		try {

			JSONArray groups = o.getJSONArray("items");

			for (int i = 0; i < groups.length(); i++) {
				JSONObject g = groups.getJSONObject(i);
				Group user = null;
				if (g.getLong("id") > 0) {
					user = new Group(g.getLong("id"), g.getString("name"),
							g.getString("description"),
							g.getBoolean("public_group"), "support".equals(g
									.getString("group_type")), false);

				}

				if (user != null) {
					userList.add(user);
				}

			}
		} catch (JSONException e) {
			throw new ApiErrorException("返回JSON错误", 500, e);
		}

		return userList.toArray(new Group[userList.size()]);

	}

	/**
	 * 获得组的管理员列表
	 * 
	 * @param groupId
	 *            组的Id信息。
	 * @return
	 * @throws ApiErrorException
	 */
	public User[] getGroupAdmins(Long groupId) throws ApiErrorException {

		HashMap<String, String> params = new HashMap<String, String>();
		JSONArray users = this.get("/api/v1/groups/" + groupId + "/admins",
				params).asJSONArray();

		ArrayList<User> userList = new ArrayList<User>();

		try {
			for (int i = 0; i < users.length(); i++) {
				JSONObject u = users.getJSONObject(i);
				User user = null;
				if (u.getLong("id") > 0) {
					user = new User();
					user.setId(u.getLong("id"));
					user.setLoginName(u.getString("login_name"));

					user.setEmail(u.getString("email"));
					user.setName(u.getString("name"));
					user.setTitle(u.getString("login_name"));
					user.setCellvoice1(u.getString("cellvoice1"));
					user.setCellvoice2(u.getString("cellvoice2"));
					user.setWorkvoice(u.getString("workvoice"));
					user.setEmpCode(u.getString("emp_code"));
				}

				if (user != null) {
					userList.add(user);
				}

			}
		} catch (JSONException e) {
			throw new ApiErrorException("返回JSON错误", 500, e);
		}

		return userList.toArray(new User[userList.size()]);

	}

	/**
	 * 删除工作圈
	 * 
	 * @param groupId
	 *            工作圈的Id
	 * @throws ApiErrorException
	 *             如果阐述产生异常，则扔出该Exception.
	 */

	public void removeGroup(long groupId) throws ApiErrorException {

		HashMap<String, String> params = new HashMap<String, String>();
		JSONObject json_result = this.delete("/api/v1/groups/" + groupId,
				params);

		try {
			int code = json_result.getInt("code");
			if (code > 0 && code != 200 && code != 201) {

				String msg = json_result.getString("message");
				throw new ApiErrorException(code, msg);

			}
		} catch (JSONException e) {

			throw new ApiErrorException("返回JSON错误", 500, e);
		}

	}

	public void updateGroupInfo(long groupId, String name, String description)
			throws ApiErrorException {

		HashMap<String, String> params = new HashMap<String, String>();

		params.put("name", name);

		if (description != null) {
			params.put("description", description);
		}

		JSONObject json_result = this.put("/api/v1/groups/" + groupId, params);

		try {
			int code = json_result.getInt("code");
			if (code > 0 && code != 200 && code != 201) {

				String msg = json_result.getString("message");
				throw new ApiErrorException(code, msg);

			}
		} catch (JSONException e) {

			throw new ApiErrorException("返回JSON错误", 500, e);
		}

	}

	public long ping() throws ApiErrorException {
		try {
			return get("/api/v1/ping").getLong("user_id");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			throw new ApiErrorException("Error return", 500, e);
		}
	}

}
