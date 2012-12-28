package org.orbund.sms.gateways;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.orbund.sms.SmsResponce;
import org.orbund.sms.SmsSender;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

//
// curl -d
// "apikey=3c702b5762684a8b6a99fe13b18db657ca26bbb2&numbers=008801681056217&message=test message"
// https://www.callfire.com/cloud/1/sms/send

/*
 * Sample Error response
 * 
 * <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
 * <campaignResponse> <description>SMS not enabled</description>
 * <successful>false</successful> </campaignResponse>
 */

/**
 * @author A.K.M. Ashrafuzzaman
 * 
 */
public class CallfireSmsSender extends SmsSender {
	private static final String SMS_SEND_PATH = "/sms/send";
	private static final String API_KEY = "3c702b5762684a8b6a99fe13b18db657ca26bbb2";
	private static final String BASE_URL = "https://www.callfire.com/cloud/1";

	private List<String> numbers;

	/**
	 * @param numbers
	 *            List of numbers numbers
	 * @param message
	 *            the message that is sent to the numbers
	 */
	public CallfireSmsSender(List<String> numbers) {
		super();
		this.numbers = numbers;
	}

	public CallfireSmsSender(String number) {
		super();
		this.numbers = new ArrayList<String>();
		numbers.add(number);
	}

	@Override
	public SmsResponce send(String message) {
		WebResource webResource = getWebResource(BASE_URL);
		String serviceParams = String.format("apikey=%s&numbers=%s&message=%s", API_KEY, getNumbersAsCsv(), getEncodedMessage(message));
		logger.info(String.format("Posting with :: PATH = %s params :: %s", SMS_SEND_PATH, serviceParams));
		ClientResponse response = webResource.path(SMS_SEND_PATH)
						.accept(MediaType.APPLICATION_XML)
						.post(ClientResponse.class, serviceParams);
		String responseStr = getResponseAsString(response);
		return parseResponse(responseStr);
	}

	public SmsResponce parseResponse(String responseStr) {
		try {
			Document document = getDocumentFromString(responseStr);
			Element rootNode = document.getRootElement();
			String description = rootNode.getChild("description").getValue();
			boolean success = Boolean.parseBoolean(rootNode.getChild("successful").getValue());
			return new SmsResponce(success, description);
		} catch (Exception e) {
			e.printStackTrace();
			return new SmsResponce(false, "Something went wrong");
		}

	}

	private Document getDocumentFromString(String responseStr) throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();
		Document document = (Document) builder.build(new StringReader(responseStr));
		return document;
	}

	private String getNumbersAsCsv() {
		return StringUtils.join(numbers, ',');
	}

}