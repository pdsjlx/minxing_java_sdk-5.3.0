minxing-java-sdk
================

Minxing sdk for java



#简单的例子


发送信息给某个用户的例子。
```java

AppAccount account = AppAccount.loginByToken("http://localhost:3000",
				"yh5EgUi0rV51l2_s0oZ6Q45nd8zWdgUqiyxiLgwEDtzPmNVy"); //使用token登录
// account.setFromUserId(30766);
account.setFromUserLoginName("13911759994");

// 发送消息给莫个人

User a = new User();
a.setLoginName("oajcs3@js.chinamobile.com");

TextMessage message = account.sendMessageToUser(a, "一条个人消息2");
System.out.println(message);
				
```

