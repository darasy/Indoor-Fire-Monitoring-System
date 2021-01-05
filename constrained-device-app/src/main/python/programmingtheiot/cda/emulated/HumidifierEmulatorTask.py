#####
# 
# This class is part of the Programming the Internet of Things project.
# 
# It is provided as a simple shell to guide the student and assist with
# implementation for the Programming the Internet of Things exercises,
# and designed to be modified by the student as needed.
#

import logging

from time import sleep

import programmingtheiot.common.ConfigConst as ConfigConst

from programmingtheiot.common.ConfigUtil import ConfigUtil
from programmingtheiot.data.ActuatorData import ActuatorData
from programmingtheiot.cda.sim.BaseActuatorSimTask import BaseActuatorSimTask

from pisense import SenseHAT

class HumidifierEmulatorTask(BaseActuatorSimTask):
    """
    Shell representation of class for humidifier emulator task.
    
    """

    def __init__(self):
        """
        Constructor
        """
        # Derive from BaseActuatorSimTask
        super(HumidifierEmulatorTask, self).__init__(actuatorName = ConfigConst.HUMIDIFIER_ACTUATOR_NAME, actuatorType = ActuatorData.HUMIDIFIER_ACTUATOR_TYPE, simpleName = "HUMIDIFIER")
        # Create an instance of SenseHAT and set the emulate flag to True if running the emulator, or False if using real hardware
        # This can be read from ConfigUtil using the ConfigConst.CONSTRAINED_DEVICE section and the ConfigConst.ENABLE_SENSE_HAT_KEY
        # If the ConfigConst.ENABLE_SENSE_HAT_KEY is False, set the emulate flag to True, otherwise set to False
        self.configUtil = ConfigUtil()
        enableEmulation = False
        if self.configUtil.getProperty(ConfigConst.CONSTRAINED_DEVICE, ConfigConst.ENABLE_SENSE_HAT_KEY) == "False":
            enableEmulation = True
        self.sh = SenseHAT(emulate = enableEmulation)


    def _handleActuation(self, cmd: int, val: float = 0.0, stateData: str = None) -> int:
        """
        Handle actuator command
        
        @return int
        """
        # NOTE: use the API instructions for pisense for help
        if cmd == ActuatorData.COMMAND_ON:
            if self.sh.screen:
                # create a message with the value and an 'ON' message, then scroll it across the LED display
                self.sh.screen.scroll_text("HUMIDIFIER ON = {}".format(val))
                return 0
            else:
                logging.warning("No SenseHAT LED screen instance to update.")
                return -1
        else:
            if self.sh.screen:
                # create a message with an 'OFF' message, then scroll it across the LED display
                self.sh.screen.scroll_text("OFF")
                return 0
            else:
                logging.warning("No SenseHAT LED screen instance to clear / close.")
                return -1
