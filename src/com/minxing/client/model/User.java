package com.minxing.client.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.minxing.client.json.JSONArray;
import com.minxing.client.json.JSONException;
import com.minxing.client.json.JSONObject;

public class User {
	private static Map<String, Method> deptMethods;

	private String email;
	private String cellPhone;
	private String empCode;
	private int network_id;
	private String loginName;
	private int user_id;
	private String preferredMobile;
	private String name;

	private List<Department> departs = new ArrayList<User.Department>();

	public String getEmpCode() {
		return empCode;
	}

	public void setEmpCode(String empCode) {
		this.empCode = empCode;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCellPhone() {
		return cellPhone;
	}

	public void setCellPhone(String cellPhone) {
		this.cellPhone = cellPhone;
	}

	public String getNetwork_id() {
		return String.valueOf(network_id);
	}

	// public void setNetwork_id(String network_id) {
	// this.network_id = network_id;
	// }

	public void setNetwork_id(int network_id) {
		this.network_id = network_id;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	@Override
	public String toString() {
		return "User [loginName=" + loginName + ",empCode=" + empCode
				+ ",email=" + email + ", cellPhone=" + cellPhone
				+ ",network_id=" + network_id +departs+ "]";
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public List<Department> getDeparts() {
		return departs;
	}

	public void setDeparts(JSONArray depts) {
		if (deptMethods == null) {
			createDeptMethods();
		}
		try {
			for (int i = 0; i < depts.length(); i++) {
				JSONObject jsonDept = depts.getJSONObject(i);
				Iterator<String> iter = jsonDept.keys();
				Department dept = new Department();
				while (iter.hasNext()) {
					String key = iter.next();
					Object obj = jsonDept.get(key);
					Object value = "";

					if (obj != null && !obj.equals(JSONObject.NULL)) {
						value = obj;// .toString();
					}
					key = key.substring(0, 1).toUpperCase() + key.substring(1); // first
																				// letter
																				// to
																				// uppercase
					Method m = deptMethods.get("set" + key);
					if (m != null) {
						m.invoke(dept, value);
					}
				}
				departs.add(dept);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void createDeptMethods() {
		deptMethods = new HashMap<String, Method>();
		Method[] methods = Department.class.getMethods();
		for (Method m : methods) {
			if (m.getName().startsWith("set")) {
				deptMethods.put(m.getName(), m);
			}
		}
	}

	public String getPreferredMobile() {
		return preferredMobile;
	}

	public void setPreferredMobile(String preferredMobile) {
		this.preferredMobile = preferredMobile;
	}

	public static class Department {
		private String dept_code;
		private String dept_short_name;
		private String dept_full_name;

		public String getDept_code() {
			return dept_code;
		}

		public void setDept_code(String dept_code) {
			this.dept_code = dept_code;
		}

		public String getDept_short_name() {
			return dept_short_name;
		}

		public void setDept_short_name(String dept_short_name) {
			this.dept_short_name = dept_short_name;
		}

		public String getDept_full_name() {
			return dept_full_name;
		}

		public void setDept_full_name(String dept_full_name) {
			this.dept_full_name = dept_full_name;
		}

		@Override
		public String toString() {
			return new StringBuilder("[Department:").append(dept_code)
					.append(",").append(dept_full_name).append("]").toString();
		}

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
