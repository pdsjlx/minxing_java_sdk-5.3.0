import com.minxing.client.app.AppAccount;
import com.minxing.client.model.ApiErrorException;

public class SendServerSidePushToAppUsersByApi {

	public static void main(String[] args) {

		// 创建一个连接
		AppAccount account = AppAccount.loginByAccessToken(
				"http://localhost:3000",
				"iPefUDrrardwZMWQXaZnBDBCLyY3iksJTmYtP2rcrJ0EYCJA");

		String message = "<msg>my message</msg>"; // 发送给移动端的消息，可以是json，xml，base64或者其他序列化成string的

		// 推送
		try {
			int appid = 81; // 接收人列表
			String alert = "您有一个提醒"; // iOS通知栏信息，可选
			String alert_extend = "{'count': 1}"; // iOS的扩展内容,可选
			int send_count = account.pushMessageToAllAppUsers(appid, message, alert,
					alert_extend);
			
			System.out.println(send_count); // 成功发送给几个人

		} catch (ApiErrorException e) {

			System.err.println("错误信息" + e.getMessage()); // 成功发送给几个人
			e.printStackTrace();

		}

	}

}
