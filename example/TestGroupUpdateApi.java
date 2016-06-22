import com.minxing.client.app.AppAccount;
import com.minxing.client.model.ApiErrorException;

public class TestGroupUpdateApi {

	public static void main(String[] args) {
		AppAccount account = AppAccount.loginByAccessToken(
				"http://127.0.0.1:3000",
				"iPefUDrrardwZMWQXaZnBDBCLyY3iksJTmYtP2rcrJ0EYCJA");
		

		try {
			account.setFromUserLoginName("oajcs3@js.chinamobile.com");
			
			//删除组需要耗时很长时间，有可能超时
			account.updateGroupInfo(144L,"新名字x1",null);

		} catch (ApiErrorException e) {
			e.printStackTrace();
		}

	}

}
