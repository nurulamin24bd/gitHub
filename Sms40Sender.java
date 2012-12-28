package org.orbund.sms.gateways;

import javax.ws.rs.core.MediaType;

import org.orbund.sms.SmsResponce;
import org.orbund.sms.SmsSender;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/*
 * http://www.sms40.com/api.php?username=khalid1sa&password=0555421654&type=SendSMS&sender=Orbund&mobile=+8801681056217&message=TestSMS
 */

/**
 * @author A.K.M. Ashrafuzzaman
 * 
 */
public class Sms40Sender extends SmsSender {
	private static final String SMS_SEND_PATH = "/api.php";
	private static final String SENDER = "Orbund";
	private static final String USER_NAME = "khalid1sa";
	private static final String PASSWORD = "0555421654";
	private static final String BASE_URL = "http://www.sms40.com";
	
	public static final String PERMISSION_DENIED_MSG = "YOU DONT HAVE PERMISSION TO ACCESS THIS PAGE";

	private String number;

	public Sms40Sender(String number) {
		this.number = number;
	}

	@Override
	public SmsResponce send(String message) {
		WebResource webResource = getWebResource(BASE_URL);
		String serviceParams = String.format("username=%s&password=%s&type=SendSMS&sender=%s&mobile=%s&message=%s", USER_NAME, PASSWORD, SENDER, number, getEncodedMessage(message));
		logger.info(String.format("Posting with :: PATH = %s params :: %s", SMS_SEND_PATH, serviceParams));
		ClientResponse response = webResource.path(SMS_SEND_PATH)
						.accept(MediaType.APPLICATION_XML)
						.post(ClientResponse.class, serviceParams);
		String responseStr = getResponseAsString(response).trim();
		return parseResponse(responseStr);
	}

	public SmsResponce parseResponse(String responseStr) {
		try {
			boolean success = true;
			if(responseStr.startsWith("ERROR") || responseStr.contains(PERMISSION_DENIED_MSG))
				success = false;
			return new SmsResponce(success, responseStr);
		} catch (Exception e) {
			e.printStackTrace();
			return new SmsResponce(false, "Something went wrong");
		}

	}

}