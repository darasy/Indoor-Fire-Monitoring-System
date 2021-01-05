/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.gda.connection.handlers;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;


/**
 * Shell representation of class for student implementation.
 *
 */
public class GenericCoapResourceHandler extends CoapResource
{
	// static
	
	private static final Logger _Logger =
		Logger.getLogger(GenericCoapResourceHandler.class.getName());
	
	// params
	private IDataMessageListener dataMsgListener = null;

	
	// constructors
	
	/**
	 * Constructor.
	 * 
	 * @param resource Basically, the path (or topic)
	 */
	public GenericCoapResourceHandler(ResourceNameEnum resource)
	{
		this(resource.getResourceName());
	}
	
	/**
	 * Constructor.
	 * 
	 * @param resourceName The name of the resource.
	 */
	public GenericCoapResourceHandler(String resourceName)
	{
		super(resourceName);
	}
	
	
	// public methods
	
	@Override
	public void handleDELETE(CoapExchange context)
	{
		_Logger.log(Level.CONFIG, "handleDELETE is called: " + context.getRequestText());
		// accept the request
	    context.accept();
	    // TODO: retrieve the requested data and generate a response message: 'msg'
	    List<String> options = context.getRequestOptions().getUriPath();
	    String msg = "Generic handler. No DELETE action taken: " + options.get(options.size() - 1);
	    // send an appropriate response
	    context.respond(ResponseCode.DELETED, msg);
	}
	
	@Override
	public void handleGET(CoapExchange context)
	{
		_Logger.log(Level.CONFIG, "handleGET is called: " + context.getRequestText());
		// accept the request
	    context.accept();
	    // TODO: retrieve the requested data and generate a response message: 'msg'
	    List<String> options = context.getRequestOptions().getUriPath();
	    String msg = "Generic handler. No GET action taken: " + options.get(options.size() - 1);
	    // send an appropriate response
	    context.respond(ResponseCode.VALID, msg);
	}
	
	@Override
	public void handlePOST(CoapExchange context)
	{
		//_Logger.log(Level.CONFIG, "handlePOST is called: " + context.getRequestText());
		// accept the request
	    context.accept();
	    // TODO: create (or update) the resource with the payload
	    String payload = context.getRequestText();
	    // TODO: generate a response message: 'msg'
	    List<String> options = context.getRequestOptions().getUriPath();
	    String msg = "Generic handler. No POST action taken: " + options.get(options.size() - 1);
	    // send an appropriate response
	    context.respond(ResponseCode.CREATED, msg);
	}
	
	@Override
	public void handlePUT(CoapExchange context)
	{
		//_Logger.log(Level.CONFIG, "handlePUT is called: " + context.getRequestText());
		// accept the request
	    context.accept();
	    // TODO: create (or update) the resource with the payload
	    String payload = context.getRequestText();
	    // TODO: generate a response message: 'msg'
	    List<String> options = context.getRequestOptions().getUriPath();
	    String msg = "Generic handler. No PUT action taken: " + options.get(options.size() - 1); 
	    // send an appropriate response
	    context.respond(ResponseCode.CHANGED, msg);
	}
	
	public void setDataMessageListener(IDataMessageListener listener)
	{
		this.dataMsgListener = listener;
	}
	
}
