package org.orbund.sms.gateways;

import org.orbund.sms.SmsResponce;
import org.orbund.sms.SmsSender;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/*
 * Request
 * curl -d "api_id=1811649&user=orbund&password=j24v35&to=+8801681056217&text=A+Test+For+orbund+SMS" https://api.clickatell.com/http/sendmsg
 * 
 * https://api.clickatell.com/http/sendmsg?api_id=1811649&user=orbund&password=j24v35&to=+8801681056217&text=test+message
 * 
 * Sample response
 * 
 * ID: cda8ef5c7c8f6a45b342cbb7130acfb3
 */

/**
 * @author A.K.M. Ashrafuzzaman
 * 
 */
public class ClickATellSmsSender extends SmsSender {
	private static final String SMS_SEND_PATH = "/http/sendmsg";
	private static final String BASE_URL = "https://api.clickatell.com";
	private ClickATellAuthInfo authInfo;
	
	private String number;

	public ClickATellSmsSender(ClickATellAuthInfo authInfo, String number) {
		this.authInfo = authInfo;
		this.number = number;
	}

	@Override
	public SmsResponce send(String message) {
		String serviceParams = String.format("api_id=%s&user=%s&password=%s&to=%s&text=%s",
						authInfo.getApiId(), 
						authInfo.getUser(), 
						authInfo.getPassword(), 
						number, 
						getEncodedMessage(message));
		WebResource webResource = getWebResource(BASE_URL + SMS_SEND_PATH + "?" + serviceParams);
		logger.info(String.format("Posting with :: PATH = %s", webResource.getURI()));
		ClientResponse response = webResource.get(ClientResponse.class);
		String responseStr = getResponseAsString(response);
		return parseResponse(responseStr);
	}

	public SmsResponce parseResponse(String responseStr) {
		try {
			boolean success = responseStr.startsWith("ID:") ? true : false;
			String message = null;
			if (success) {
				message = "Message send";
			} else {
				message = responseStr.split(", ")[1];
			}
			return new SmsResponce(success, message);
		} catch (Exception e) {
			e.printStackTrace();
			return new SmsResponce(false, "Something went wrong");
		}

	}

}