
import com.minxing.client.app.AppAccount;
import com.minxing.client.app.OcuMessageSendResult;
import com.minxing.client.ocu.Article;
import com.minxing.client.ocu.ArticleMessage;

public class TestOcuAccount {
	public static void main(String[] args) throws InterruptedException {
		for (int i = 0; i < 100; i++) {
			// oa.sendMessageToUsersStr(new
			// HtmlMessage("测试<a href='http://www.baidu.com'>百度</a>"),
			// "dev001@dehui220.com.cn");
			ArticleMessage am = new ArticleMessage();
			Article pt = new Article("标题\r\n", "内容\r详细内容", "", "", "");
			am.addArticle(pt);

			AppAccount account = AppAccount.loginByAccessToken(
					"http://localhost:3000",
					"iPefUDrrardwZMWQXaZnBDBCLyY3iksJTmYtP2rcrJ0EYCJA");

			String ocuId = "40dbd78cc10e32d7a36f2a518460f7f7";
			String ocuSecret = "88cb9f89de14332abc787486a4249b30";

			OcuMessageSendResult send_to = account.sendOcuMessageToUsers(
					new String[] {}, am, ocuId, ocuSecret);
			System.out.println("发送至:" + send_to.getCount() + "人");
			System.out.println("发送消息Id:" + send_to.getMessageId());
			System.out.println("发送用户Id列表:" + send_to.getUserIds());
			Thread.currentThread().sleep(1000);
		}
	}

}
