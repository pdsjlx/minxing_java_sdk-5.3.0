import com.minxing.client.app.AppAccount;
import com.minxing.client.app.OcuMessageSendResult;
import com.minxing.client.ocu.Article;
import com.minxing.client.ocu.ArticleMessage;
import com.minxing.client.ocu.Resource;

public class TestCreateOcuResource {
	public static void main(String[] args) throws InterruptedException {

		// oa.sendMessageToUsersStr(new
		// HtmlMessage("测试<a href='http://www.baidu.com'>百度</a>"),
		// "dev001@dehui220.com.cn");

		AppAccount account = AppAccount.loginByAccessToken(
				"http://localhost:3000",
				"iPefUDrrardwZMWQXaZnBDBCLyY3iksJTmYtP2rcrJ0EYCJA");

		String ocuId = "c9c96fa22d160b7ed878c908dbec8b42";
		String ocuSecret = "7ddfec54bcde626460b0ff466de144bc";

		ArticleMessage m = new ArticleMessage();
		Resource resource = new Resource(
				"美国次贷危机启示",
				"",
				"",
				"2014-05-27",
				"https://www.minxing365.com/plists/html/new2.jpg",
				"<p>三大因素成危机爆发隐患</p>\n\n"
						+ "<p>降息复加息，货币政策大幅调转：2000 年后美联储大幅降息，经济获得高速发展，自04 年6 月起，出于通胀抬头的压力，美联储开始连续加息，大动静的货币政策转变未能让市场有所缓冲，成为次贷危机的第一个隐患。</p>\n\n"
						+ "<p>低利率促成房地产泡沫：受低利率利好，房贷成本下降，房地产需求扩大，新屋开工和新房销售大幅度提高，整个行业欣欣向荣，而房价增长逐步脱离整体价格水平，即使04 年起连续加息也没能阻止房价泡沫。</p>\n\n"
						+ "<p>次贷证券化“雪球”越滚越大：在低利率、高房价的环境下，次贷的风险被低估，同时美国政府设立两房、提倡居民购房，金融机构大量发行次贷和次贷证券化产品以获取巨额利润；MBS、CMO、浮动利息产品等都加大了金融风险。</p>\n\n"
						+ "<p>对中国的启示</p>\n\n"
						+ "<p>货币超增导致地产泡沫：过去10 年中国房价涨幅远超通胀，而与货币增速高度相关，意味着房价泡沫的出现源于货币超额扩张。</p>\n\n"
						+ "<p>货币长期宽松，影子银行泛滥：而货币超增又与两大因素有关，一是长期宽松的货币政策，刻意维持偏低利率以刺激经济增长，体现为贷款平均利率远低于GDP名义增速；二是影子银行的野蛮生长，过去10 年非标融资年均增速高达58%，远高于21%的社融增速和15%的GDP 名义增速。</p>\n\n"
						+ "<p>货币挤出泡沫，经济影响深远：新政府强调稳健货币政策，从8 号文、107 号文到127 号文对同业、影子银行全面拨乱反正，以信托、委托贷款为标志的非标融资开始明显收缩，这也意味着货币高速扩张成为历史。而与地产相关的各类金融产品价格均面临缩水风险，进而会对投资、消费形成冲击，对后续经济影响深远。</p>\n\n");

		m.addArticle(new Article(
				resource,
				"美国次贷危机启示",
				"https://www.minxing365.com/plists/html/new2.jpg",
				"降息复加息，货币政策大幅调转：2000 年后美联储大幅降息，经济获得高速发展，自04 年6 月起，出于通胀抬头的压力，美联储开始连续加息，大动静的货币政策转变未能让市场有所缓冲，成为次贷危机的第一个隐患。"));

		OcuMessageSendResult r = account.sendOcuMessageToUsers(new String[] {},
				m, ocuId, ocuSecret);
		System.out.println("result:" + r);
	}

}
