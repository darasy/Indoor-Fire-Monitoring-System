/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.gda.connection;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.WebLink;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;

/**
 * Shell representation of class for student implementation.
 *
 */
public class CoapClientConnector implements IRequestResponseClient
{
	// static
	private static final Logger _Logger =
		Logger.getLogger(CoapClientConnector.class.getName());
	
	// params
	private String     protocol;
	private String     host;
	private int        port;
	private String     serverAddr;
	private CoapClient clientConn;
	private IDataMessageListener dataMsgListener;
	
	// constructors
	
	/**
	 * Default.
	 * 
	 * All config data will be loaded from the config file.
	 */
	public CoapClientConnector()
	{
		ConfigUtil config = ConfigUtil.getInstance();
		this.host = config.getProperty(ConfigConst.COAP_GATEWAY_SERVICE, ConfigConst.HOST_KEY, ConfigConst.DEFAULT_HOST);

		if (config.getBoolean(ConfigConst.COAP_GATEWAY_SERVICE, ConfigConst.ENABLE_CRYPT_KEY)) {
			this.protocol = ConfigConst.DEFAULT_COAP_SECURE_PROTOCOL;
			this.port     = config.getInteger(ConfigConst.COAP_GATEWAY_SERVICE, ConfigConst.SECURE_PORT_KEY, ConfigConst.DEFAULT_COAP_SECURE_PORT);
		} else {
			this.protocol = ConfigConst.DEFAULT_COAP_PROTOCOL;
			this.port     = config.getInteger(ConfigConst.COAP_GATEWAY_SERVICE, ConfigConst.PORT_KEY, ConfigConst.DEFAULT_COAP_PORT);
		}
		
		// NOTE: URL does not have a protocol handler for "coap",
		// so we need to construct the URL manually
		this.serverAddr = this.protocol + "://" + this.host + ":" + this.port;

		initClient();

		_Logger.info("Using URL for server conn: " + this.serverAddr);
	}
		
	

	/**
	 * Constructor.
	 * 
	 * @param host
	 * @param isSecure
	 * @param enableConfirmedMsgs
	 */
	public CoapClientConnector(String host, boolean isSecure, boolean enableConfirmedMsgs)
	{
		ConfigUtil config = ConfigUtil.getInstance();
		this.host = host;

		if (isSecure && enableConfirmedMsgs) {
			this.protocol = ConfigConst.DEFAULT_COAP_SECURE_PROTOCOL;
			this.port     = config.getInteger(ConfigConst.COAP_GATEWAY_SERVICE, ConfigConst.SECURE_PORT_KEY, ConfigConst.DEFAULT_COAP_SECURE_PORT);
		} else {
			this.protocol = ConfigConst.DEFAULT_COAP_PROTOCOL;
			this.port     = config.getInteger(ConfigConst.COAP_GATEWAY_SERVICE, ConfigConst.PORT_KEY, ConfigConst.DEFAULT_COAP_PORT);
		}
		
		// NOTE: URL does not have a protocol handler for "coap",
		// so we need to construct the URL manually
		this.serverAddr = this.protocol + "://" + this.host + ":" + this.port;

		initClient();

		_Logger.info("Using URL for server conn: " + this.serverAddr);
	}
	
	private void initClient() {
		try {
			this.clientConn = new CoapClient(this.serverAddr);
			_Logger.info("Created client connection to server / resource: " + this.serverAddr);
		} catch (Exception e) {
			_Logger.log(Level.SEVERE, "Failed to connect to broker: " + (this.clientConn != null ? this.clientConn.getURI() : this.serverAddr), e);
		}
		
	}
	
	// public methods
	
	@Override
	/* This will issue a discovery request to the server, which will provide a list of resource names (fully qualified) 
	 * that are currently registered with the server.
	 * 
	 * OPTION 2
	 */
	public boolean sendDiscoveryRequest(int timeout)
	{
		_Logger.log(Level.INFO, "sendDiscoveryRequest() is called");
		try {
			this.clientConn.setURI("/.well-known/core");
	
			CoapResponse response = this.clientConn.get();
	
			String[] resources = response.getResponseText().split(",");
	
			for (String resource : resources) {
				_Logger.info("--> URI: " + resource.replace("<", "").replace(">",  ""));
			}
			return true;
		} 
		catch (Exception e) {
			_Logger.warning("Cannot send a DISCOVERY request");
			return false;
		}
		
	}

	@Override
	public boolean sendDeleteRequest(ResourceNameEnum resource, boolean enableCON, int timeout)
	{
		_Logger.log(Level.INFO, "sendDeleteRequest() is called");
		CoapResponse response = null;

		if (enableCON) {
			this.clientConn.useCONs();
		} else {
			this.clientConn.useNONs();
		}

		this.clientConn.setURI(this.serverAddr + "/" + resource.getResourceName());
		response = this.clientConn.delete();

		if (response != null) {
			// TODO: implement your logic here
			
			_Logger.info("Handling DELETE. Response: " + response.isSuccess() + " - " + response.getOptions() + " - " +
				response.getCode() + " - " + response.getResponseText());
			
			if (this.dataMsgListener != null) {
				// TODO: implement this
			}
			
			return true;
		} else {
			_Logger.warning("Handling DELETE. No response received.");
		}

		return false;
	}

	@Override
	public boolean sendGetRequest(ResourceNameEnum resource, boolean enableCON, int timeout)
	{
		_Logger.log(Level.INFO, "sendGetRequest() is called");
		CoapResponse response = null;

		if (enableCON) {
			this.clientConn.useCONs();
		} else {
			this.clientConn.useNONs();
		}

		this.clientConn.setURI(this.serverAddr + "/" + resource.getResourceName());
		response = this.clientConn.get();

		if (response != null) {
			// TODO: implement your logic here
			
			_Logger.info("Handling GET. Response: " + response.isSuccess() + " - " + response.getOptions() + " - " +
				response.getCode() + " - " + response.getResponseText());
			
			if (this.dataMsgListener != null) {
				// TODO: implement this
			}
			
			return true;
		} else {
			_Logger.warning("Handling GET. No response received.");
		}

		return false;
	}

	@Override
	public boolean sendPostRequest(ResourceNameEnum resource, boolean enableCON, String payload, int timeout)
	{
		//_Logger.log(Level.INFO, "sendPostRequest() is called");
		CoapResponse response = null;

		if (enableCON) {
			this.clientConn.useCONs();
		} else {
			this.clientConn.useNONs();
		}

		this.clientConn.setURI(this.serverAddr + "/" + resource.getResourceName());

		// TODO: determine which MediaTypeRegistry const should be used for this call
		response = this.clientConn.post(payload, MediaTypeRegistry.TEXT_PLAIN);

		if (response != null) {
			// TODO: implement your logic here
			
			//_Logger.info("Handling POST. Response: " + response.isSuccess() + " - " + response.getOptions() + " - " +
			//	response.getCode() + " - " + response.getResponseText());
			
			if (this.dataMsgListener != null) {
				// TODO: implement this
			}
			
			return true;
		} else {
			_Logger.warning("Handling POST. No response received.");
		}

		return false;
	}

	@Override
	public boolean sendPutRequest(ResourceNameEnum resource, boolean enableCON, String payload, int timeout)
	{
		//_Logger.log(Level.INFO, "sendPutRequest() is called");
		CoapResponse response = null;

		if (enableCON) {
			this.clientConn.useCONs();
		} else {
			this.clientConn.useNONs();
		}

		this.clientConn.setURI(this.serverAddr + "/" + resource.getResourceName());

		// TODO: determine which MediaTypeRegistry const should be used for this call
		response = this.clientConn.put(payload, MediaTypeRegistry.TEXT_PLAIN);

		if (response != null) {
			// TODO: implement your logic here
			
			//_Logger.info("Handling PUT. Response: " + response.isSuccess() + " - " + response.getOptions() + " - " +
			//	response.getCode() + " - " + response.getResponseText());
			
			if (this.dataMsgListener != null) {
				// TODO: implement this
			}
			
			return true;
		} else {
			_Logger.warning("Handling PUT. No response received.");
		}

		return false;
	}

	@Override
	public boolean setDataMessageListener(IDataMessageListener listener)
	{
		if (listener != null) {
			this.dataMsgListener = listener;
			return true;
		}
		return false;
	}

	@Override
	public boolean startObserver(ResourceNameEnum resource, int ttl)
	{
		return false;
	}

	@Override
	public boolean stopObserver(int timeout)
	{
		return false;
	}

	
	// private methods
	
}
