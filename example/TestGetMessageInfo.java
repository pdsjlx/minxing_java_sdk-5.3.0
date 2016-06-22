import com.minxing.client.app.AppAccount;
import com.minxing.client.model.ApiErrorException;
import com.minxing.client.ocu.Message;

public class TestGetMessageInfo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		AppAccount account = AppAccount.loginByAccessToken(
				"http://127.0.0.1:3000",
				"iPefUDrrardwZMWQXaZnBDBCLyY3iksJTmYtP2rcrJ0EYCJA");
		Message[] msgs;
		try {
//			account.setFromUserLoginName("oajcs3@js.chinamobile.com");
			msgs = account.getAllMessagesInThread(63962L);
			for (int i = 0; i < msgs.length; i++) {
				System.out.println("Get message:" + msgs[i]);
			}
		} catch (ApiErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
