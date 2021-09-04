#!/usr/bin/python3

import json
from lib import helper
from lib import file_helper
from packaging import constants as const
from packaging import config_helper


def create_bs_config_json(web_ui_port: str, base_dir: str):
  bs_config_dict = {
    'port': web_ui_port,
    'server': {
      'baseDir': base_dir
    }
  }
  file_helper.write_file('bs-config.json', json.dumps(bs_config_dict))


if __name__ == '__main__':
  if config_helper.configuration_file_exists():
    web_ui_port = config_helper.read_web_ui_port()

    helper.print_inner_step('launching web ui', 0)
    helper.print_inner_step('with angular port: {}'.format(web_ui_port), 1)
    helper.print_inner_step('with dist folder: {}'.format(const.DIST_DIRECTORY), 1)
    create_bs_config_json(web_ui_port, const.COMPONENTS_WEB_UI_DIST_DIRECTORY)
    helper.launch_command('lite-server', 'WebUI stopped')

  else:
    helper.print_inner_step('ERROR: Please make sure configuration file: {} exists'.format(const.CONFIGURATION_FILE), 0)
