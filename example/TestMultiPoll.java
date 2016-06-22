import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.minxing.client.app.AppAccount;
import com.minxing.client.ocu.UserInfo;

public class TestMultiPoll {

	static int WORKER_COUNT = 20;

	static int COUNT = 5;

	public static void main(String[] ss) {

		// testMyHost();
		testTest0();
	}

	private static void testTest0() {
		AppAccount acc = AppAccount.loginByAccessToken(
				"http://test.dehuinet.com:8030/",
				"14tcrJ_1ZOyRnXlvXROzP8CTWyemge0TU5dj2fk35XeQzPcf");

		acc.setFromUserLoginName("admin@dehuinet");
		List<UserInfo> users = acc.getAllUsersInDepartment("001");
		System.out.print(users.size());
		// Map<String, String> params = new HashMap<String, String>();
		// params.put("app_name", "poll");
		// params.put("index", "0,1");
		// Map<String, String> headers = new HashMap<String, String>();
		// for (int i = 0; i < users.size(); i++) {
		// String login = users.get(i).getLogin_name();
		// acc.setFromUserLoginName(login);
		// params.put("index", "0" );
		// System.out.println(login + " poll !!!");
		// try {
		// acc.post("/api/v1/mmodules/poll/37", params, headers);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		List<Thread> ws = new ArrayList<Thread>();
		for (int i = 0; i < WORKER_COUNT; i++) {
			ws.add(new Thread(new Worker(users, i)));
		}
		for (int i = 0; i < WORKER_COUNT; i++) {
			ws.get(i).start();
		}
	}

	private static void testMyHost() {
		AppAccount acc = AppAccount.loginByAccessToken("http://localhost:3000",
				"qd37z3o2AuJhjGpaa9kWEyOmixueaJI1ILhnlKQTGe4Ao4bp");

		acc.setFromUserLoginName("admin@thread.dev");
		List<UserInfo> users = acc.getAllUsersInDepartment("001");
		System.out.print(users.size());
		Map<String, String> params = new HashMap<String, String>();
		params.put("app_name", "poll");
		params.put("index", "0,1,4");
		params.put("index", "0,1,4");
		Map<String, String> headers = new HashMap<String, String>();
		for (int i = 0; i < users.size(); i++) {
			String login = users.get(i).getLogin_name();
			acc.setFromUserLoginName(login);
			params.put("index", i % 5 + "," + (i + 2) % 5);
			System.out.println(login + " poll !!!");
			try {
				acc.post("/api/v1/mmodules/poll/173", params, headers);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static class Worker implements Runnable {
		List<UserInfo> users;

		private int number;
		AppAccount acc;
		Map<String, String> params;
		Map<String, String> headers;

		public Worker(List<UserInfo> users, int number) {
			this.users = users;
			this.number = number;
			this.acc = AppAccount.loginByAccessToken(
					"http://test.dehuinet.com:8030/",
					"14tcrJ_1ZOyRnXlvXROzP8CTWyemge0TU5dj2fk35XeQzPcf");
			this.params = new HashMap<String, String>();
			params.put("app_name", "poll");
			params.put("has_voted", "false");
			this.headers = new HashMap<String, String>();

		}

		@Override
		public void run() {
			for (int j = 0; j < COUNT; j++) {
				for (int i = number; i < users.size();) {
					UserInfo u = users.get(i);
					acc.setFromUserLoginName(u.getLogin_name());
					System.out.println(u.getLogin_name() + " poll !!!");
					int index = ((int)(Math.random()*1000))%5;
					if(index<0||index>4){
						System.out.println("index = "+index);
					}
					params.put("index", index+"");
					try {
						acc.post("api/v1/mmodules/poll/37", params, headers);
					} catch (Exception e) {
						e.printStackTrace();
					}
					i += WORKER_COUNT;
				}
			}
		}

	}

}
