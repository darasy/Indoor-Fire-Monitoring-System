/**
 * This class is part of the Programming the Internet of Things
 * project, and is available via the MIT License, which can be
 * found in the LICENSE file at the top level of this repository.
 * 
 * Copyright (c) 2020 by Andrew D. King
 */

package programmingtheiot.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A convenience class to provide type consistency around commonly used
 * topics and resource names.
 * 
 */
public enum ResourceNameEnum
{
	// static
	
	CDA_SENSOR_MSG_RESOURCE(ConfigConst.CDA_SENSOR_DATA_MSG_RESOURCE, "constraineddevice", "", false),
	CDA_ACTUATOR_CMD_RESOURCE(ConfigConst.CDA_ACTUATOR_CMD_MSG_RESOURCE, "constraineddevice", "", false),
	CDA_ACTUATOR_RESPONSE_RESOURCE(ConfigConst.CDA_ACTUATOR_RESPONSE_MSG_RESOURCE, "constraineddevice", "", false),
	CDA_MGMT_STATUS_MSG_RESOURCE(ConfigConst.CDA_MGMT_STATUS_MSG_RESOURCE, "constraineddevice", "", false),
	CDA_SYSTEM_PERF_MSG_RESOURCE(ConfigConst.CDA_SYSTEM_PERF_MSG_RESOURCE, "constraineddevice", "", false),
	CDA_MGMT_STATUS_CMD_RESOURCE(ConfigConst.CDA_MGMT_CMD_MSG_RESOURCE, "constraineddevice", "", false),
	
	GDA_MGMT_STATUS_MSG_RESOURCE(ConfigConst.GDA_MGMT_STATUS_MSG_RESOURCE, "gatewaydevice", "mgmt_status_msg", true),
	GDA_MGMT_STATUS_CMD_RESOURCE(ConfigConst.GDA_MGMT_CMD_MSG_RESOURCE, "gatewaydevice", "", true),
	GDA_SYSTEM_PERF_MSG_RESOURCE(ConfigConst.GDA_SYSTEM_PERF_MSG_RESOURCE, "gatewaydevice", "", true),
	
	UBIDOT_ACTUATOR_LED(ConfigConst.UBIDOT_ACTUATOR_LED, "actuator", "led/lv", true), 
	CDA_SENSOR_TEMP(ConfigConst.CDA_SENSOR_TEMP, "constraineddevice", "temperature", true),
	CDA_SENSOR_HUMID(ConfigConst.CDA_SENSOR_HUMID, "constraineddevice", "humidity",  true),
	CDA_SENSOR_PRESS(ConfigConst.CDA_SENSOR_PRESS, "constraineddevice", "pressure",  true),
	CDA_MEM_UTIL(ConfigConst.CDA_MEM_UTIL, "constraineddevice", "memory",  true),
	CDA_CPU_UTIL(ConfigConst.CDA_CPU_UTIL, "constraineddevice", "cpu",  true),
	GDA_MEM_UTIL(ConfigConst.GDA_MEM_UTIL, "gatewaydevice", "memory",  true),
	GDA_CPU_UTIL(ConfigConst.GDA_CPU_UTIL, "gatewaydevice", "cpu",  true);
	
	private static final HashMap<String, ResourceNameEnum> _ResourceNameLookupMap = new HashMap<>();
	
	static {
		for (ResourceNameEnum rn : ResourceNameEnum.values()) {
			_ResourceNameLookupMap.put(rn.getResourceName(), rn);
		}
	}
	
	/**
	 * Convenience method for looking up an enum type based on
	 * the value String. If the lookup fails, null will be returned.
	 * <p>
	 * No error or warning message will be logged, and no exception
	 * will be thrown. If this is called and null is returned, it's
	 * safe to assume that the value simply does not map to any of
	 * the enum type values represented by this class.
	 * 
	 * @param valStr The value of the enum to lookup.
	 * @return ResourceNameEnum The enum instance, or null if not found.
	 */
	public static ResourceNameEnum getEnumFromValue(String valStr)
	{
		if (valStr != null && valStr.length() > 0) {
			if (_ResourceNameLookupMap.containsKey(valStr)) {
				return _ResourceNameLookupMap.get(valStr);
			}
		}
		
		return null;
	}
	
	
	// private var's
	
	private String resourceName = "";
	private String resourceType = "";
	private String deviceName = "";
	private boolean isLocalToGDA = false;
	
	
	// constructor
	
	/**
	 * Constructor.
	 * 
	 * @param resourceName
	 * @param isLocalToGda
	 */
	private ResourceNameEnum(String resourceName, boolean isLocalToGda)
	{
		this.resourceName = resourceName;
		this.isLocalToGDA = isLocalToGda;
	}
	
	private ResourceNameEnum(String resourceName, String deviceName, String resourceType,  boolean isLocalToGda)
	{
		this.resourceName = resourceName;
		this.deviceName = deviceName;
		this.resourceType = resourceType;
		this.isLocalToGDA = isLocalToGda;
	}
	
	// public methods
	
	/**
	 * 
	 * @return String
	 */
	public String getResourceName()
	{
		return this.resourceName;
	}
	public String getResourceType()
	{
		return this.resourceType;
	}
	public String getDeviceeName()
	{
		return this.deviceName;
	}
	
	/**
	 * 
	 * @return List<String> The ordered list of Strings representing this
	 * resource name split by '/'.
	 */
	public List<String> getResourceNameChain()
	{
		String[] names = this.resourceName.split("/");
		
		List<String> nameList = new ArrayList<>(names.length);
		
		for (String name : names) {
			nameList.add(name);
		}
		
		return nameList;
	}
	
	/**
	 * 
	 * @return boolean True if this resource is local to the GDA (meaning any
	 * use of the resource is internal to the GDA); false if it's not (meaning
	 * it's a resource used by the CDA).
	 */
	public boolean isLocalToGda()
	{
		return this.isLocalToGDA;
	}
	
}