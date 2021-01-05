/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.gda.connection.handlers;

import java.util.logging.Logger;

import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.OptionSet;

import programmingtheiot.common.IDataMessageListener;


/**
 * Shell representation of class for student implementation.
 *
 */
public class GenericCoapResponseHandler implements CoapHandler
{
	// static
	
	private static final Logger _Logger =
		Logger.getLogger(GenericCoapResponseHandler.class.getName());
	
	// params
	private IDataMessageListener dataMsgListener = null;

	// constructors
	
	/**
	 * Constructor.
	 * 
	 * @param resource Basically, the path (or topic)
	 */
	public GenericCoapResponseHandler(IDataMessageListener dataMsgListener)
	{
		setDataMessageListener(dataMsgListener);
	}
	
	
	public void setDataMessageListener(IDataMessageListener listener)
	{
		this.dataMsgListener = listener;
	}
	
	@Override
	public void onLoad(CoapResponse response)
	{
		if (response != null) {
			OptionSet options = response.getOptions();
			
			_Logger.info("Processing CoAP response. Options: " + options);
			_Logger.info("Processing CoAP response. MID: " + response.advanced().getMID());
			_Logger.info("Processing CoAP response. Token: " + response.advanced().getTokenString());
			_Logger.info("Processing CoAP response. Code: " + response.getCode());
			
			// TODO: parse payload and notify listener
			_Logger.info(" --> Payload: " + response.getResponseText());
			
			if (this.dataMsgListener != null) {
				// TODO: send listener the response
			}
		} else {
			_Logger.warning("No CoAP response to process. Response is null.");
		}
	}

	@Override
	public void onError()
	{
		_Logger.warning("Error processing CoAP response. Ignoring.");
	}
}
