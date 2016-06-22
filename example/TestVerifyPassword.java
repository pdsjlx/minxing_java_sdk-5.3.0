

import com.minxing.client.app.AppAccount;
import com.minxing.client.model.MxVerifyException;
import com.minxing.client.organization.User;

public class TestVerifyPassword {
	public static void main(String[] args) {
		AppAccount account = AppAccount.loginByAccessToken("http://localhost:3000",
				"iPefUDrrardwZMWQXaZnBDBCLyY3iksJTmYtP2rcrJ0EYCJA");
		
		try {
			boolean valid = account.verifyPassword("oajcs3@js.chinamobile.com", "11111");
			System.out.println("password valid return:" + valid);
		} catch (MxVerifyException e) { 
			
			e.printStackTrace();
		}
	}

}
