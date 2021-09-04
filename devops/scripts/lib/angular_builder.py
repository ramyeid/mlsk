#!/usr/bin/python3

from lib import constants as const
from lib import file_helper
from lib import helper


def read_angular_prod_environment_file() -> str:
  return file_helper.read_file('{}/src/environments/environment.prod.ts'.format(const.WEB_UI_DIRECTORY))


def overwrite_angular_prod_environment_file(service_port: str):
  file_helper.replace_placeholder_in_file('{}/src/environments/environment.prod.ts'.format(const.WEB_UI_DIRECTORY),
                                      const.ANGULAR_SERVER_PORT_OPTION, service_port)


def reset_angular_prod_environment_file(initial_content: str):
  file_helper.write_file('{}/src/environments/environment.prod.ts'.format(const.WEB_UI_DIRECTORY), initial_content)


def compile_angular():
  command = 'cd {} && ng build --configuration production'.format(const.WEB_UI_DIRECTORY)
  helper.launch_command(command, 'Angular compilation failed')


def compile_angular_and_run_tests():
  compile_angular()
  command = 'cd {} && ng test --watch=false --browsers=ChromeHeadlessNoSandbox'.format(const.WEB_UI_DIRECTORY)
  helper.launch_command(command, 'Angular test failed')


def copy_web_ui_dist(output: str):
  file_helper.copy_dir('{}/dist/machine-learning-swissknife'.format(const.WEB_UI_DIRECTORY), output)
