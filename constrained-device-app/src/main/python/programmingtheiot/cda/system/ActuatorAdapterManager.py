#####
# 
# This class is part of the Programming the Internet of Things project.
# 
# It is provided as a simple shell to guide the student and assist with
# implementation for the Programming the Internet of Things exercises,
# and designed to be modified by the student as needed.
#

import logging

from programmingtheiot.common.IDataMessageListener import IDataMessageListener

from programmingtheiot.data.ActuatorData import ActuatorData
from programmingtheiot.cda.sim.HumidifierActuatorSimTask import HumidifierActuatorSimTask
from programmingtheiot.cda.sim.HvacActuatorSimTask import HvacActuatorSimTask

from programmingtheiot.cda.emulated.HumidifierEmulatorTask import HumidifierEmulatorTask
from programmingtheiot.cda.emulated.HvacEmulatorTask import HvacEmulatorTask

from time import sleep

class ActuatorAdapterManager(object):
    """
    Shell representation of class for Actuator Adapter Manager.
    """
    
    def __init__(self, useEmulator: bool = False):
        """
        Constructor
        """
        self.useEmulator = useEmulator
        self.dataMsgListener = IDataMessageListener()
        # Use emulator
        if self.useEmulator:
            logging.info("Emulators will be used.")
            
            # create the humidifier actuator
            self.humidifierActuator = HumidifierEmulatorTask()
            # create the HVAC actuator
            self.hvacActuator = HvacEmulatorTask()
            
            # load the Humidifier actuation emulator
            humidifierModule = __import__('programmingtheiot.cda.emulated.HumidifierEmulatorTask', fromlist = ['HumidifierEmulatorTask'])
            hueClazz = getattr(humidifierModule, 'HumidifierEmulatorTask')
            self.humidifierEmulator = hueClazz()
            # load the Hvac actuation emulator
            hvacModule = __import__('programmingtheiot.cda.emulated.HvacEmulatorTask', fromlist = ['HvacEmulatorTask'])
            hueClazz = getattr(hvacModule, 'HvacEmulatorTask')
            self.hvacEmulator = hueClazz()
            # load the LED actuation emulator
            ledModule = __import__('programmingtheiot.cda.emulated.LedDisplayEmulatorTask', fromlist = ['LedDisplayEmulatorTask'])
            hueClazz = getattr(ledModule, 'LedDisplayEmulatorTask')
            self.ledEmulator = hueClazz()
            
            # load the Video Streaming actuation emulator
            alarmModule = __import__('programmingtheiot.cda.emulated.FireAlarmEmulatorTask', fromlist = ['FireAlarmEmulatorTask'])
            hueClazz = getattr(alarmModule, 'FireAlarmEmulatorTask')
            self.alarmEmulator = hueClazz()
            
        # Use simulator
        else: 
            logging.info("Simulators will be used.")
            
            # create the humidifier actuator
            self.humidifierActuator = HumidifierActuatorSimTask()
            # create the HVAC actuator
            self.hvacActuator = HvacActuatorSimTask()

        
    def sendActuatorCommand(self, data: ActuatorData) -> bool:
        """
        Send the command to the appropriate simulator by checking the actuator type within ActuatorData. 
        
        @return Boolean
        """
        # Check if the data is not None
        if data:
            logging.info("Actuator command received. Processing...")
            # Check if the data has not been responded, otherwise, no need to do anything
            logging.info("Created DataUtil instance.")
            newData = data
            if (data.isResponse == False):
                # Use simulator
                if self.useEmulator == False:
                    # Check type of the sensor and handle data accordingly
                    if data.actuatorType == ActuatorData.HVAC_ACTUATOR_TYPE:
                        newData.setName("ActuatorHVAC")
                        self.hvacActuator.updateActuator(data)
                    if data.actuatorType == ActuatorData.HUMIDIFIER_ACTUATOR_TYPE:
                        newData.setName("ActuatorHUMIDIFIER")
                        self.humidifierActuator.updateActuator(data)
                # Use emulator
                else:
                    # Check type of the sensor and handle data accordingly
                    if data.actuatorType == ActuatorData.HVAC_ACTUATOR_TYPE:
                        newData.setName("ActuatorHVAC")
                        self.hvacEmulator._handleActuation(data.getCommand(), data.getValue())
                    if data.actuatorType == ActuatorData.HUMIDIFIER_ACTUATOR_TYPE:
                        newData.setName("ActuatorHUMIDIFIER")
                        self.humidifierEmulator._handleActuation(data.getCommand(), data.getValue())
                    if data.actuatorType == ActuatorData.LED_DISPLAY_ACTUATOR_TYPE:
                        newData.setName("ActuatorLED")
                        self.ledEmulator._handleActuation(cmd=newData.getCommand(), stateData = newData.getStateData())
                    if data.actuatorType == ActuatorData.ALARM_ACTUATOR_TYPE:
                        newData.setName("FireAlarmActuator")
                        logging.info("Fire Alarm Actuator Called")
                        self.alarmEmulator._handleActuation(data.getCommand())
            # Pass in the data to te data message listener
            newData.isResponse = True
            data.isResponse = True
            self.dataMsgListener.handleActuatorCommandResponse(newData)
            return True
        return False
    
    
    def setDataMessageListener(self, listener: IDataMessageListener) -> bool:
        """
        Set a new data message listener
        
        @return Boolean
        """
        if listener:
            self.dataMsgListener = listener
            return True
        return False
