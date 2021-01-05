/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.gda.connection;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import javax.mail.PasswordAuthentication;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.io.FileUtils;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;

import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.data.ActuatorData;
import programmingtheiot.data.SensorData;


/**
 * Shell representation of class for SMTP Client connector.
 *
 */
public class SmtpClientConnector implements IRequestResponseClient
{
	// static
	private static final Logger _Logger =
		Logger.getLogger(SmtpClientConnector.class.getName());
	
	// private var's
	private IDataMessageListener listener = null;
	
	// constructors
	/**
	 * Default.
	 * 
	 */
	public SmtpClientConnector()
	{
		super();
	}
	
	
	// public methods
	
	/**
	 * IGNORE THIS METHOD.
	 */
	@Override
	public boolean sendDiscoveryRequest(int timeout)
	{
		// NOTE: No need to implement this!
		
		_Logger.warning("Method not required to be implemented.");
		
		return false;
	}

	/**
	 * IGNORE THIS METHOD.
	 */
	public boolean sendDeleteRequest(ResourceNameEnum resource, int timeout)
	{
		// NOTE: No need to implement this!
		
		_Logger.warning("Method not required to be implemented.");
		
		return false;
	}

	/**
	 * IGNORE THIS METHOD.
	 */
	public boolean sendGetRequest(ResourceNameEnum resource, int timeout)
	{
		// NOTE: No need to implement this!
		
		_Logger.warning("Method not required to be implemented.");
		
		return false;
	}

	public boolean sendPostRequest(ResourceNameEnum resource, String payload, int timeout)
	{
		return configureAndSendMessage(resource, payload.getBytes());
	}

	public boolean sendPutRequest(ResourceNameEnum resource, String payload, int timeout)
	{
		return configureAndSendMessage(resource, payload.getBytes());
	}

	/**
	 * A method to set a data message listener
	 */
	@Override
	public boolean setDataMessageListener(IDataMessageListener listener)
	{
		try {
			this.listener = listener;
			return true;
		} catch (Exception e) {
			_Logger.warning("Fail to set a data message listener");
			return false;
		}
		
	}

	/**
	 * IGNORE THIS METHOD.
	 */
	@Override
	public boolean startObserver(ResourceNameEnum resource, int ttl)
	{
		// NOTE: No need to implement this!
		
		_Logger.warning("Method not required to be implemented.");
		
		return false;
	}

	/**
	 * IGNORE THIS METHOD.
	 */
	@Override
	public boolean stopObserver(int timeout)
	{
		// NOTE: No need to implement this!
		
		_Logger.warning("Method not required to be implemented.");
		
		return false;
	}

	
	// private methods
	private boolean configureAndSendMessage(ResourceNameEnum resource, byte[] payload)
	{
		return false;
	}
	
	private boolean configureAndSendMessage(ResourceNameEnum resource, String payloadFileName)
	{
		return false;
	}
	
	private boolean sendSmtpMessage(Message smtpMsg)
	{
		return false;
	}
	
	private Message createSmtpMessage(String subject)
	{
		return null;
	}
	
	private void initSmtpSession()
	{
	}


	@Override
	public boolean sendDeleteRequest(ResourceNameEnum resource, boolean enableCON, int timeout) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean sendGetRequest(ResourceNameEnum resource, boolean enableCON, int timeout) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean sendPostRequest(ResourceNameEnum resource, boolean enableCON, String payload, int timeout) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean sendPutRequest(ResourceNameEnum resource, boolean enableCON, String payload, int timeout) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * A method to send an email to the user using SMTP
	 * @return
	 */
	public boolean sendEmail(ActuatorData data) {
		try
	    {
			_Logger.info("sendEmail() method has been called");
			ConfigUtil configUtil = ConfigUtil.getInstance();

	        // Get system properties for the mail server which I used Gmail in this case
	        Properties properties = System.getProperties();
	        properties.put(ConfigConst.SMTP_PROP_HOST_KEY, "smtp.gmail.com");
	        properties.put(ConfigConst.SMTP_PROP_PORT_KEY, "465");
	        properties.put(ConfigConst.SMTP_PROP_ENABLE_TLS_KEY, "true");
	        properties.put(ConfigConst.SMTP_PROP_AUTH_KEY, "true");
	        // Get emails of sender and receiver as well as sender password
	        String configSectionName = ConfigConst.SMTP_GATEWAY_SERVICE;
	        String senderEmail = configUtil.getProperty(configSectionName, ConfigConst.SENDER_EMAIL);
	        String receiverEmail = configUtil.getProperty(configSectionName, ConfigConst.RECEIVER_EMAIL);
	        Path path = Paths.get(configUtil.getProperty(configSectionName, ConfigConst.USER_AUTH_TOKEN_KEY));
	        String password = Files.readString(path, StandardCharsets.ISO_8859_1);
	        // Create the Session object
	        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
	        	protected PasswordAuthentication getPasswordAuthentication()
	        	{
	        		return new PasswordAuthentication(senderEmail, password);
	        	}
	        });
	        
	        // Configure email setting
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(senderEmail, "no_reply"));
			msg.setReplyTo(InternetAddress.parse("no_reply@example.com", false));
			msg.setSentDate(new Date());
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiverEmail, false));
			msg.setSubject("Fire Detected", "UTF-8");
			
			// Create a multi part message to handle images and email content
			Multipart multiPart = new MimeMultipart("alternative");
			MimeBodyPart textSection = new MimeBodyPart();
			MimeBodyPart imageSection = new MimeBodyPart();
			
			// BODY SECTION
			String content = "Fire has been detected in your room with the probability of " + data.getValue() + " at " + LocalDateTime.now().toString();
			textSection.setContent(content, "text/plain; charSet=UTF-8");
			// IMAGE SECTION
			// Try decoding an image
			byte[] decodedBytes = Base64.getDecoder().decode(data.getStateData());
			// Convert byte array into an image
			//FileUtils.writeByteArrayToFile(new File("TEST90909.JPG"), decodedBytes);
			// Create image source from the image byte array
			ByteArrayDataSource imageSource = new ByteArrayDataSource(decodedBytes, "image/png");
			// Create image data handler
			DataHandler dataHandler = new DataHandler(imageSource);
			imageSection.setDataHandler(dataHandler);
			imageSection.setFileName("FireDetected.png");
			
			// Add all the sections to the message
			multiPart.addBodyPart(textSection);
			multiPart.addBodyPart(imageSection);
			msg.setContent(multiPart);
			// Send an email
			Transport.send(msg);  
			_Logger.info("Alert Email Has Been Sent Successfully!!");
			return true;
		}
		catch (Exception e) {
		  e.printStackTrace();
		  return false;
		}
	}
	
}
