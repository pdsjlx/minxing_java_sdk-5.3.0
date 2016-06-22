import java.util.Iterator;

import com.minxing.client.app.AppAccount;
import com.minxing.client.app.UserPackage;
import com.minxing.client.app.UserSet;
import com.minxing.client.model.MxException;
import com.minxing.client.organization.Department;
import com.minxing.client.organization.User;

public class TestGetAllUser {

	public static void main(String[] args) {
		AppAccount account = AppAccount.loginByAccessToken(
				"http://test1.dehuinet.com:8031/",
				"vSo3xkp7qS-IMA0Zn_cdhusGfxy84cWcYFQ0W0sFPsFMJO-D");
//		AppAccount account = AppAccount.loginByAccessToken(
//				"http://localhost:3000",
//				"qd37z3o2AuJhjGpaa9kWEyOmixueaJI1ILhnlKQTGe4Ao4bp");

		UserPackage result;
		try {
			int count = 0;
			for (int k = 0; k < 1; k++) {
				result = account.exportUsers(100);
				Iterator<UserSet> it = result.iterator();

				while (it.hasNext()) {

					UserSet set = it.next();
					User[] eachPageUsers = set.getUsers();
					count += eachPageUsers.length;
					System.out.println(count);
					 for (int i = 0; i < eachPageUsers.length; i++) {
					 User u = eachPageUsers[i];
					 System.out.println("user:" + eachPageUsers[i]);
					 if (u.getRoleCode() == User.ROLE_ADMIN) {
					 System.out.println("管理员");
					 }
					 if (u.getRoleCode() == User.ROLE_USER) {
					 System.out.println("普通用户");
					 }
					
					 if (u.getRoleCode() == User.ROLE_NOTIFER) {
					 System.out.println("通知");
					 }
					 if (u.getRoleCode() == User.ROLE_OFFICAL_ACCOUNT_USER) {
					 System.out.println("公众号");
					 }
					 if (u.getRoleCode() ==
					 User.ROLE_APPLICATION_CONNECT_USER) {
					 System.out.println("接入端");
					 }
					

					System.out.println("cell1:" + u.getCellvoice1());
					
					Department[] depts = u.getAllDepartments();
					
					for (int j = 0; j < depts.length; j++) {
						System.out.println("  dept:" + depts[j]);
						System.out.println("  dept:" + depts[j].getDept_code());
					}
				}


				}
			}
		} catch (MxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
