#!/usr/bin/python3

from pathlib import Path
from lib import constants as const
from lib import file_helper
from lib import helper


def compile_java():
  command = 'cd {} && mvn clean package -DskipTests -q'.format(const.JAVA_PROJECTS)
  helper.launch_command(command, 'Java compilation failed')


def compile_java_and_run_tests():
  compile_java()
  command = 'cd {} && mvn verify -q'.format(const.JAVA_PROJECTS)
  helper.launch_command(command, 'Java test failed')


def copy_swing_ui_jar(output: str):
  file_helper.copy_file(get_jar_path(const.SWING_UI_JAR), output)


def copy_service_jar(output: str):
  file_helper.copy_file(get_jar_path(const.SERVICE_JAR), output)


def get_jar_path(jar_name: str) -> str:
  for path in Path(const.JAVA_PROJECTS).rglob(jar_name):
    return path.resolve()
