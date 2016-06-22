import com.minxing.client.app.AppAccount;
import com.minxing.client.model.ApiErrorException;
import com.minxing.client.model.Group;

public class TestGetSupportTypeGroup {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		AppAccount account = AppAccount.loginByAccessToken(
				"http://127.0.0.1:3000",
				"iPefUDrrardwZMWQXaZnBDBCLyY3iksJTmYtP2rcrJ0EYCJA");
		// User[] users = account.findUserByLoginNames(new String[]
		// {"oajcs3@js.chinamobile.com","aijianlin@nt.js.chinamobile.com"});
		//
		// for(int i=0;i<users.length;i++) {
		// System.out.println("User:" + users[i]);
		//

		// }

		try {
			account.setFromUserLoginName("oajcs3@js.chinamobile.com");
			Group[] groups = account.getSupportTypeGroups();

			for (int i = 0; i < groups.length; i++) {
				System.out.println("admin:" + groups[i]);
			}

		} catch (ApiErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
