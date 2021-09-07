#!/usr/bin/python3

import argparse
from lib import file_helper
from lib import helper
from lib import angular_builder
from lib import python_builder
from lib import java_builder
from packaging import constants as const
from packaging import config_helper as config_helper


def create_build_directory():
  if not file_helper.does_dir_exist(const.BUILD_DIRECTORY):
    file_helper.create_dir(const.BUILD_DIRECTORY)


def create_components_directory():
  if not file_helper.does_dir_exist(const.BUILD_COMPONENTS_DIRECTORY):
    file_helper.create_dir(const.BUILD_COMPONENTS_DIRECTORY)


def create_logs_directory():
  if not file_helper.does_dir_exist(const.BUILD_LOGS_DIRECTORY):
    file_helper.create_dir(const.BUILD_LOGS_DIRECTORY)


def delete_build_directory():
  if file_helper.does_dir_exist(const.BUILD_DIRECTORY):
    file_helper.remove_dir(const.BUILD_DIRECTORY)


def copy_packaging_library():
  file_helper.copy_file('{}__init__.py'.format(const.PACKAGING_DIRECTORY), const.BUILD_PACKAGING_DIRECTORY)
  file_helper.copy_file('{}config_helper.py'.format(const.PACKAGING_DIRECTORY), const.BUILD_PACKAGING_DIRECTORY)
  file_helper.copy_file('{}constants.py'.format(const.PACKAGING_DIRECTORY), const.BUILD_PACKAGING_DIRECTORY)
  file_helper.copy_file('{}mlsk.ini'.format(const.PACKAGING_DIRECTORY), const.BUILD_PACKAGING_DIRECTORY)


def copy_launchers():
  file_helper.copy_file('{}launch_service.py'.format(const.PACKAGING_DIRECTORY), const.BUILD_DIRECTORY)
  file_helper.copy_file('{}launch_ui.py'.format(const.PACKAGING_DIRECTORY), const.BUILD_DIRECTORY)
  file_helper.copy_file('{}launch_web_ui.py'.format(const.PACKAGING_DIRECTORY), const.BUILD_DIRECTORY)
  file_helper.copy_file('{}__init__.py'.format(const.PACKAGING_DIRECTORY), const.BUILD_DIRECTORY)


if __name__ == '__main__':
  parser = argparse.ArgumentParser()
  parser.add_argument('--skipTests', dest='should_skip_tests', action='store_true')
  args = parser.parse_args()

  prod_environment_file_content = angular_builder.read_angular_prod_environment_file()

  if file_helper.does_dir_exist(const.BUILD_DIRECTORY):
    helper.print_inner_step('ERROR: Please remove the build directory {}, to package the solution'.format(const.BUILD_DIRECTORY), 0)
  else:
    try:
      helper.print_start_step('CREATING DIRECTORIES')
      helper.print_inner_step('Creating build directory', 1)
      create_build_directory()
      helper.print_inner_step('Creating components directory', 1)
      create_components_directory()
      helper.print_inner_step('Creating logs directory', 1)
      create_logs_directory()
      helper.print_end_step('CREATING DIRECTORIES')

      helper.print_start_step('COMPILE PYTHON')
      helper.print_inner_step('Compiling python', 1) if args.should_skip_tests else helper.print_inner_step('Compiling and testing python', 1)
      python_builder.compile_python() if args.should_skip_tests else python_builder.compile_python_and_run_tests()
      helper.print_end_step('COMPILE PYTHON')
      helper.print_start_step('COPY PYTHON')
      helper.print_inner_step('Copying python directory', 1)
      python_builder.copy_engine_project(const.BUILD_COMPONENTS_ENGINE_DIRECTORY)
      helper.print_end_step('COPY PYTHON')

      helper.print_start_step('COMPILE JAVA')
      helper.print_inner_step('Compiling java', 1) if args.should_skip_tests else helper.print_inner_step('Compiling and testing java', 1)
      java_builder.compile_java() if args.should_skip_tests else java_builder.compile_java_and_run_tests()
      helper.print_end_step('COMPILE JAVA')
      helper.print_start_step('COPY JAVA')
      helper.print_inner_step('Copying jars', 1)
      java_builder.copy_service_jar(const.BUILD_COMPONENTS_DIRECTORY)
      java_builder.copy_ui_jar(const.BUILD_COMPONENTS_DIRECTORY)
      helper.print_end_step('COPY JAVA')

      helper.print_start_step('OVERWRITE PROD ENV FILE ANGULAR')
      angular_builder.overwrite_angular_prod_environment_file(config_helper.read_service_port())
      helper.print_end_step('OVERWRITE PROD ENV FILE ANGULAR')
      helper.print_start_step('COMPILE ANGULAR')
      helper.print_inner_step('Compiling angular', 1) if args.should_skip_tests else helper.print_inner_step('Compiling and testing angular', 1)
      angular_builder.compile_angular() if args.should_skip_tests else angular_builder.compile_angular_and_run_tests()
      helper.print_end_step('COMPILE ANGULAR')
      helper.print_start_step('COPY ANGULAR')
      helper.print_inner_step('Copying angular dist directory', 1)
      angular_builder.copy_web_ui_dist(const.BUILD_COMPONENTS_WEB_UI_DIST_DIRECTORY)
      helper.print_end_step('COPY ANGULAR')

      helper.print_start_step('COPY SETUP')
      helper.print_inner_step('Copying lib', 1)
      file_helper.copy_dir('lib', const.BUILD_LIB_DIRECTORY)
      helper.print_inner_step('Creating packaging', 1)
      file_helper.create_dir(const.BUILD_PACKAGING_DIRECTORY)
      helper.print_inner_step('Copying packaging library', 1)
      copy_packaging_library()
      helper.print_inner_step('Copying launchers', 1)
      copy_launchers()
      helper.print_end_step('COPY SETUP')

    except Exception as exception:
      print('\n'*5)
      helper.print_start_step('ERROR')
      helper.print_inner_step('Error: {}'.format(str(exception)), 1)
      helper.print_inner_step('Deleting Build Directory', 1)
      delete_build_directory()
      helper.print_end_step('ERROR')

    finally:
      angular_builder.reset_angular_prod_environment_file(prod_environment_file_content)
