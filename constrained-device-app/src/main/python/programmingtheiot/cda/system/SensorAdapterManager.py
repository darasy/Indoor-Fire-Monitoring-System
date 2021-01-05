#####
# 
# This class is part of the Programming the Internet of Things project.
# 
# It is provided as a simple shell to guide the student and assist with
# implementation for the Programming the Internet of Things exercises,
# and designed to be modified by the student as needed.
#

import logging
import time 

from apscheduler.schedulers.background import BackgroundScheduler

from programmingtheiot.common.IDataMessageListener import IDataMessageListener

from programmingtheiot.cda.sim.TemperatureSensorSimTask import TemperatureSensorSimTask
from programmingtheiot.cda.sim.PressureSensorSimTask import PressureSensorSimTask
from programmingtheiot.cda.sim.HumiditySensorSimTask import HumiditySensorSimTask

import programmingtheiot.common.ConfigConst as ConfigConst
from programmingtheiot.common.ConfigUtil import ConfigUtil
from programmingtheiot.cda.sim.SensorDataGenerator import SensorDataGenerator

class SensorAdapterManager(object):
    """
    Shell representation of class for Sensor Adapter Manager.
    
    """

    def __init__(self, useEmulator: bool = False, pollRate: int = 5, allowConfigOverride: bool = True):
        """
        Constructor
        """
        self.useEmulator = useEmulator
        self.pollRate = pollRate
        self.allowConfigOverride = allowConfigOverride
        self.dataMsgListener = IDataMessageListener()
        
        logging.info("Emulators will be used.")
        # load the Humidity emulator
        humidityModule = __import__('programmingtheiot.cda.emulated.HumiditySensorEmulatorTask', fromlist = ['HumiditySensorEmulatorTask'])
        heClazz = getattr(humidityModule, 'HumiditySensorEmulatorTask')
        self.humidityEmulator = heClazz()
        
        # load the Pressure emulator
        pressureModule = __import__('programmingtheiot.cda.emulated.PressureSensorEmulatorTask', fromlist = ['PressureSensorEmulatorTask'])
        heClazz = getattr(pressureModule, 'PressureSensorEmulatorTask')
        self.pressureEmulator = heClazz()
        
        # load the Temperature emulator
        tempModule = __import__('programmingtheiot.cda.emulated.TemperatureSensorEmulatorTask', fromlist = ['TemperatureSensorEmulatorTask'])
        heClazz = getattr(tempModule, 'TemperatureSensorEmulatorTask')
        self.tempEmulator = heClazz()
            
        # load the Camera emulator
        camModule = __import__('programmingtheiot.cda.emulated.CameraSensorEmulatorTask', fromlist = ['CameraSensorEmulatorTask'])
        heClazz = getattr(camModule, 'CameraSensorEmulatorTask')
        self.camEmulator = heClazz()
        
        self.scheduler = BackgroundScheduler()
        self.scheduler.add_job(self.handleTelemetry, 'interval', seconds = pollRate, max_instances=9)
        self.scheduler.add_job(self.handleCameraTelemetry, 'interval', seconds = 1, max_instances=9)
            
    def handleCameraTelemetry(self):
        """
        handle the video streaming from the webcam
        """
        CameraSensorEmuTask = self.camEmulator.generateTelemetry()
        logging.info("Emulated Cam data: name=" + str(CameraSensorEmuTask.getName()) + 
                ",timeStamp=" + str(CameraSensorEmuTask.getTimeStamp()))
        self.dataMsgListener.handleSensorMessage(CameraSensorEmuTask)


    def handleTelemetry(self):
        """
        handle temperature, pressure and humidity sensors
        """
        # Use simulator
        if self.useEmulator == False:
            # Generate telemetry of humidity sensor
            sdHumiditySensorSimTask = self.humiditySensorSimTask.generateTelemetry()
            logging.info("Simulated humidity data: name=" + str(sdHumiditySensorSimTask.getName()) + 
                    ",timeStamp=" + str(sdHumiditySensorSimTask.getTimeStamp()) + 
                    ",curValue=" + str(sdHumiditySensorSimTask.getValue()))
            # Generate telemetry of pressure sensor
            sdPressureSensorSimTask = self.pressureSensorSimTask.generateTelemetry()
            logging.info("Simulated pressure data: name=" + str(sdPressureSensorSimTask.getName()) + 
                    ",timeStamp=" + str(sdPressureSensorSimTask.getTimeStamp()) + 
                    ",curValue=" + str(sdPressureSensorSimTask.getValue()))
            # Generate telemetry of temperature sensor
            sdTemperatureSensorSimTask = self.temperatureSensorSimTask.generateTelemetry()
            logging.info("Simulated temp data: name=" + str(sdTemperatureSensorSimTask.getName()) + 
                    ",timeStamp=" + str(sdTemperatureSensorSimTask.getTimeStamp()) + 
                    ",curValue=" + str(sdTemperatureSensorSimTask.getValue()))
            # Pass the data to message listener
            self.dataMsgListener.handleSensorMessage(sdHumiditySensorSimTask)
            self.dataMsgListener.handleSensorMessage(sdPressureSensorSimTask)
            self.dataMsgListener.handleSensorMessage(sdTemperatureSensorSimTask)
        # Use emulator
        else:
            #if self.sensorTurn == 0:
            # Generate telemetry of humidity sensor
            sdHumiditySensorEmuTask = self.humidityEmulator.generateTelemetry()
            logging.info("Emulated humidity data: name=" + str(sdHumiditySensorEmuTask.getName()) + 
                    ",timeStamp=" + str(sdHumiditySensorEmuTask.getTimeStamp()) + 
                    ",curValue=" + str(sdHumiditySensorEmuTask.getValue()))
            self.dataMsgListener.handleSensorMessage(sdHumiditySensorEmuTask)
            
            #if self.sensorTurn == 1:
            # Generate telemetry of pressure sensor
            sdPressureSensorEmuTask = self.pressureEmulator.generateTelemetry()
            logging.info("Emulated pressure data: name=" + str(sdPressureSensorEmuTask.getName()) + 
                    ",timeStamp=" + str(sdPressureSensorEmuTask.getTimeStamp()) + 
                    ",curValue=" + str(sdPressureSensorEmuTask.getValue()))
            self.dataMsgListener.handleSensorMessage(sdPressureSensorEmuTask)

            #if self.sensorTurn == 2:
            # Generate telemetry of temperature sensor
            sdTemperatureSensorEmuTask = self.tempEmulator.generateTelemetry()
            logging.info("Emulated temp data: name=" + str(sdTemperatureSensorEmuTask.getName()) + 
                    ",timeStamp=" + str(sdTemperatureSensorEmuTask.getTimeStamp()) + 
                    ",curValue=" + str(sdTemperatureSensorEmuTask.getValue()))
            self.dataMsgListener.handleSensorMessage(sdTemperatureSensorEmuTask)
                
            #self.sensorTurn = (self.sensorTurn + 1) % 3
        
    def setDataMessageListener(self, listener: IDataMessageListener) -> bool:
        """
        Set a new data message listener
        
        @return Boolean
        """
        if listener:
            self.dataMsgListener = listener
            return True
        return False
            
    def startManager(self):
        """
        Start the manager
        """
        if not(self.scheduler.running):
            logging.info("Started SensorAdapterManager.")
            self.scheduler.start()
        
    def stopManager(self):
        """
        Stop the manager
        """
        if self.scheduler.running:
            logging.info("Stopped SensorAdapterManager.")
            self.scheduler.shutdown()
