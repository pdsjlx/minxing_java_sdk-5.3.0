import java.util.List;

import com.minxing.client.app.AppAccount;
import com.minxing.client.model.MxException;
import com.minxing.client.organization.Department;

public class TestGetAllDepartment {

	public static void main(String[] args) {
		AppAccount account = AppAccount.loginByAccessToken(
				"http://localhost:3000",
				"iPefUDrrardwZMWQXaZnBDBCLyY3iksJTmYtP2rcrJ0EYCJA");

		List<Department> result;
		try {
			result = account.getAllDepartments();
			for (int i = 0; i < result.size(); i++) {
				System.out.println("department:" + result.get(i));
			}

		} catch (MxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
