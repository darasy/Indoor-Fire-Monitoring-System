/**
 * 
 * This class is part of the Programming the Internet of Things
 * project, and is available via the MIT License, which can be
 * found in the LICENSE file at the top level of this repository.
 * 
 * Copyright (c) 2020 by Andrew D. King
 */ 

package programmingtheiot.part03.integration.connection;

import static org.junit.Assert.*;

import java.util.Set;
import java.util.logging.Logger;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.WebLink;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.DefaultDataMessageListener;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.data.DataUtil;
import programmingtheiot.data.SystemStateData;
import programmingtheiot.gda.connection.*;

/**
 * This test case class contains very basic integration tests for
 * CoapClientToServerConnectorTest. It should not be considered complete,
 * but serve as a starting point for the student implementing
 * additional functionality within their Programming the IoT
 * environment.
 *
 */
public class CoapClientConnectorTest
{
	// static
	
	public static final int DEFAULT_TIMEOUT = 300 * 10000;
	public static final boolean USE_DEFAULT_RESOURCES = true;
	
	private static final Logger _Logger =
		Logger.getLogger(CoapClientConnectorTest.class.getName());
	
	
	// member var's
	
	private CoapServerGateway csg = null;
	private IDataMessageListener dml = null;
	private CoapClientConnector coapClient = null;
	
	
	// test setup methods
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		this.csg = new CoapServerGateway(USE_DEFAULT_RESOURCES);
		this.csg.startServer();

		this.coapClient = new CoapClientConnector();
		this.dml = new DefaultDataMessageListener();
		
		this.coapClient.setDataMessageListener(this.dml);
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception
	{
		this.csg.stopServer();

	}


	
	// test methods
	
	/**
	 * 
	 */
	@Ignore
	@Test
	public void testConnectAndDiscover()
	{
		try {
			assertTrue(this.coapClient.sendDiscoveryRequest(DEFAULT_TIMEOUT));

			// NOTE: If you are using a custom asynchronous discovery, include a brief wait here
			try {
				Thread.sleep(2000L);
			} catch (InterruptedException e) {
				// ignore
			}
			
			
		} catch (Exception e) {
			// ignore
		}
	}
	@Ignore
	@Test
	public void testGetRequestCon()
	{
		assertTrue(this.coapClient.sendGetRequest(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, true, DEFAULT_TIMEOUT));
	}
	@Ignore	
	@Test
	public void testGetRequestNon()
	{
		assertTrue(this.coapClient.sendGetRequest(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, false, DEFAULT_TIMEOUT));
	}
	@Ignore
	@Test
	public void testPutRequestCon()
	{
		int actionCmd = 2;
		
		SystemStateData ssd = new SystemStateData();
		ssd.setActionCommand(actionCmd);
		
		String ssdJson = DataUtil.getInstance().systemStateDataToJson(ssd);
		assertTrue(this.coapClient.sendPutRequest(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, true, ssdJson, DEFAULT_TIMEOUT));
	}
	@Ignore	
	@Test
	public void testPutRequestNon()
	{
		int actionCmd = 2;
		
		SystemStateData ssd = new SystemStateData();
		ssd.setActionCommand(actionCmd);
		
		String ssdJson = DataUtil.getInstance().systemStateDataToJson(ssd);
		assertTrue(this.coapClient.sendPutRequest(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, false, ssdJson, DEFAULT_TIMEOUT));
	}
	@Ignore
	@Test
	public void testPostRequestCon()
	{
		int actionCmd = 2;
		
		SystemStateData ssd = new SystemStateData();
		ssd.setActionCommand(actionCmd);
		
		String ssdJson = DataUtil.getInstance().systemStateDataToJson(ssd);
		assertTrue(this.coapClient.sendPostRequest(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, true, ssdJson, DEFAULT_TIMEOUT));
	}
	@Ignore	
	@Test
	public void testPostRequestNon()
	{
		int actionCmd = 2;
		
		SystemStateData ssd = new SystemStateData();
		ssd.setActionCommand(actionCmd);
		
		String ssdJson = DataUtil.getInstance().systemStateDataToJson(ssd);
		assertTrue(this.coapClient.sendPostRequest(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, false, ssdJson, DEFAULT_TIMEOUT));
	}
	@Ignore
	@Test
	public void testDeleteRequestCon()
	{
		assertTrue(this.coapClient.sendDeleteRequest(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, true, DEFAULT_TIMEOUT));
	}
	@Ignore
	@Test
	public void testDeleteRequestNon()
	{
		assertTrue(this.coapClient.sendDeleteRequest(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, false, DEFAULT_TIMEOUT));
	}
	
}
