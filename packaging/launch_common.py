#!/usr/bin/python3

import configparser
import os
import common_constants as const


def configuration_file_exists() -> bool:
    return os.path.isfile(const.CONFIGURATION_FILE)


def read_project_information() -> dict:
    config_parser = configparser.ConfigParser()
    config_parser.read(const.CONFIGURATION_FILE)
    result_dictionary = {const.SERVICE_PORT_OPTION: config_parser.get(const.SERVICE_SECTION, const.PORT_SECTION),
                         const.ENGINE_PORTS_OPTION: config_parser.get(const.ENGINE_SECTION, const.PORTS_SECTION),
                         const.WEB_UI_PORT_OPTION:  config_parser.get(const.WEB_UI_SECTION, const.PORT_SECTION)}
    return result_dictionary
