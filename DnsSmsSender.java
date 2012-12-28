package org.orbund.sms.gateways;

import javax.ws.rs.core.MediaType;

import org.orbund.sms.SmsResponce;
import org.orbund.sms.SmsSender;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

//
// curl http://cp.ring.com.bd/SendTextMessage.php?uname=XXXXX&pass=XXXXX&to=8801681056217&msg=MyMessage

/*
 * Sample Error response
 * 
 * Wrong username or password or mobile number not given!!!!!!!!
 */

public class DnsSmsSender extends SmsSender {
	private static final String SMS_SEND_PATH = "/SendTextMessage.php";
	private static final String USER_NAME = "USER_NAME";
	private static final String PASSWORD = "PASSWORD";
	private static final String BASE_URL = "http://cp.ring.com.bd";
	public static final String WRONG_UNAME_PASS_MOB_NUM_ERROR = "Wrong username or password or mobile number not given!!!!!!!!";

	private String number;
	/**
	 * @param number only one number is supported, i.e. 8801XXXXXXXXX
	 * @param message the message that is sent to the numbers
	 */
	public DnsSmsSender(String number) {
		super();
		this.number = number;
	}

	/**
	 * This sends text message to the numbers given
	 * 
	 * @param number Phone number i.e. 8801XXXXXXXXX
	 * @param message the text message to send
	 * @return
	 */
	@Override
	public SmsResponce send(String message) {
		WebResource webResource = getWebResource(BASE_URL);
		String serviceParams = String.format("uname=%s&pass=%s&to=%s&msg=%s",USER_NAME, PASSWORD, format(number), message);
		logger.info(String.format("Posting with :: PATH = %s params :: %s", SMS_SEND_PATH, serviceParams));
		ClientResponse response = webResource.path(SMS_SEND_PATH)
				.accept(MediaType.APPLICATION_XML)
				.post(ClientResponse.class, serviceParams);
		String responseStr = getResponseAsString(response);
		return parseResponse(responseStr);
	}

	public SmsResponce parseResponse(String responseStr) {
		try {
			boolean success = true;
			if(responseStr.equals(WRONG_UNAME_PASS_MOB_NUM_ERROR))
				success = false;
			return new SmsResponce(success, responseStr);
		} catch (Exception e) {
			e.printStackTrace();
			return new SmsResponce(false, "Something went wrong");
		}

	}

	private String format(String number) {
		String formatedNumber = number;
		if(number.startsWith("+"))
			formatedNumber = number.substring(1);
		return formatedNumber;
	}


}