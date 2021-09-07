#!/usr/bin/python3

from lib import helper
from lib import constants as lib_const
from packaging import constants as const
from packaging import config_helper


if __name__ == '__main__':
  if config_helper.configuration_file_exists():
    service_port = config_helper.read_service_port()

    helper.print_inner_step('launching swing ui', 0)
    helper.print_inner_step('with service port: {}'.format(service_port), 1)
    helper.launch_command('java -jar {}{} --service-port {}'
                          .format(const.COMPONENTS_DIRECTORY, lib_const.SWING_UI_JAR, service_port),
                          'Swing UI stopped')

  else:
    helper.print_inner_step('ERROR: Please make sure configuration file: {} exists'.format(const.CONFIGURATION_FILE), 0)
