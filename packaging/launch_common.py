import configparser
import os
import common_constants as constants

def configuration_file_exists() -> bool:
  return os.path.isfile(constants.CONFIGURATION_FILE)


def read_project_information() -> dict:
  config_parser = configparser.ConfigParser()
  config_parser.read(constants.CONFIGURATION_FILE)
  result_dictionary = {constants.SERVICE_PORT_OPTION : config_parser.get(constants.SERVICE_SECTION, constants.SERVICE_PORT_OPTION),
                       constants.ENGINE_PORTS_OPTION : config_parser.get(constants.ENGINE_SECTION, constants.ENGINE_PORTS_OPTION)}
  return result_dictionary

