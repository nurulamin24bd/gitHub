package org.orbund.sms.gateways;

public class ClickATellAuthInfo {

	private static final String API_ID = "1811649";
	private static final String USER = "orbund";
	private static final String PASSWORD = "j24v35";
	private String user;
	private String password;
	private String apiId;

	public ClickATellAuthInfo(String user, String password, String apiId) {
		this.user = user;
		this.password = password;
		this.apiId = apiId;
	}

	public ClickATellAuthInfo() {
		this.user = USER;
		this.password = PASSWORD;
		this.apiId = API_ID;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public String getApiId() {
		return apiId;
	}
}