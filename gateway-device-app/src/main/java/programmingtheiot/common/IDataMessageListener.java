/**
 * This class is part of the Programming the Internet of Things
 * project, and is available via the MIT License, which can be
 * found in the LICENSE file at the top level of this repository.
 * 
 * You may find it more helpful to your design to adjust the
 * functionality, constants and interfaces (if there are any)
 * provided within in order to meet the needs of your specific
 * Programming the Internet of Things project.
 */ 

package programmingtheiot.common;

import programmingtheiot.data.ActuatorData;
import programmingtheiot.data.SensorData;
import programmingtheiot.data.SystemPerformanceData;

/**
 * A simple callback interface for handling data messages
 * from a connection.
 *
 */
public interface IDataMessageListener
{
	/**
	 * Callback signature for data message passing using the given parameters.
	 * 
	 * @param resourceName The enum representing the String resource name.
	 * @param data The ActuatorData data - this will usually be the decoded payload
	 * from a connection using either MQTT or CoAP.
	 * @return True on success; false otherwise.
	 */
	public boolean handleActuatorCommandResponse(ResourceNameEnum resourceName, ActuatorData data);

	/**
	 * Callback signature for data message passing using the given parameters.
	 * 
	 * @param resourceName The enum representing the String resource name.
	 * @param msg The String message - this will usually be the decoded payload
	 * from a connection using either MQTT or CoAP.
	 * @return True on success; false otherwise.
	 */
	public boolean handleIncomingMessage(ResourceNameEnum resourceName, String msg);
	
	/**
	 * Callback signature for data message passing using the given parameters.
	 * 
	 * @param resourceName The enum representing the String resource name.
	 * @param data The SensorData data - this will usually be the decoded payload
	 * from a connection using either MQTT or CoAP.
	 * @return True on success; false otherwise.
	 */
	public boolean handleSensorMessage(ResourceNameEnum resourceName, SensorData data);
	
	/**
	 * Callback signature for data message passing using the given parameters.
	 * 
	 * @param resourceName The enum representing the String resource name.
	 * @param data The SystemPerformanceData data - this will usually be the decoded payload
	 * from a connection using either MQTT or CoAP.
	 * @return True on success; false otherwise.
	 */
	public boolean handleSystemPerformanceMessage(ResourceNameEnum resourceName, SystemPerformanceData data);
	
}
