#####
# 
# This class is part of the Programming the Internet of Things project.
# 
# It is provided as a simple shell to guide the student and assist with
# implementation for the Programming the Internet of Things exercises,
# and designed to be modified by the student as needed.
#

import logging, io, os
from time import sleep
from PIL import Image
import programmingtheiot.common.ConfigConst as ConfigConst
from programmingtheiot.common.ConfigUtil import ConfigUtil
from programmingtheiot.data.ActuatorData import ActuatorData
from programmingtheiot.cda.sim.BaseActuatorSimTask import BaseActuatorSimTask

from playsound import playsound

class FireAlarmEmulatorTask(BaseActuatorSimTask):
    """
    Shell representation of class for fire alarm emulator task.
    
    """

    def __init__(self):
        """
        Constructor
        """
        # Derive from BaseActuatorSimTask
        super(FireAlarmEmulatorTask, self).__init__(actuatorName = ConfigConst.ALARM_ACTUATOR_NAME, actuatorType = ActuatorData.ALARM_ACTUATOR_TYPE, simpleName = "FIRE ALARM")
        # Create an instance of SenseHAT and set the emulate flag to True if running the emulator, or False if using real hardware
        # This can be read from ConfigUtil using the ConfigConst.CONSTRAINED_DEVICE section and the ConfigConst.ENABLE_SENSE_HAT_KEY
        # If the ConfigConst.ENABLE_SENSE_HAT_KEY is False, set the emulate flag to True, otherwise set to False
        self.configUtil = ConfigUtil()


    def _handleActuation(self,  cmd: int) -> int:
        """
        Handle actuator command
        
        @return int
        """
        # If the command set ON, play alarm sound using playsound
        if cmd == ActuatorData.COMMAND_ON:
            logging.info("Fire Alarm Actuator Turn ON")
            try:
                file = "/home/darasy/git/constrained-device-app-rethd/src/main/python/programmingtheiot/cda/emulated/Alarm_Sound.mp3"
                os.system("mpg123 " + file)
                logging.info("Play successfully")
                return 0
            except Exception as e:
                logging.warn(e)
                return -1
        else:
            logging.info("Turn off the sound")
            return -1
            
        
        
        
        
        
        