#!/usr/bin/python3

import argparse
from lib import file_helper
from lib import helper
from lib import angular_builder
from lib import python_builder
from lib import java_builder
from deployment import constants as const
from deployment import deployment_helper


def create_deployment_directory():
  if not file_helper.does_dir_exist(const.DOCKER_DEPLOYMENT_DIRECTORY):
    file_helper.create_dir(const.DOCKER_DEPLOYMENT_DIRECTORY)


def create_service_directory():
  if not file_helper.does_dir_exist(const.DOCKER_DEPLOYMENT_SERVICE_DIRECTORY):
    file_helper.create_dir(const.DOCKER_DEPLOYMENT_SERVICE_DIRECTORY)


def create_web_ui_directory():
  if not file_helper.does_dir_exist(const.DOCKER_DEPLOYMENT_WEB_UI_DIRECTORY):
    file_helper.create_dir(const.DOCKER_DEPLOYMENT_WEB_UI_DIRECTORY)


def create_swing_ui_directory():
  if not file_helper.does_dir_exist(const.DOCKER_DEPLOYMENT_SWING_UI_DIRECTORY):
    file_helper.create_dir(const.DOCKER_DEPLOYMENT_SWING_UI_DIRECTORY)


def delete_deployment_directory():
  if file_helper.does_dir_exist(const.DOCKER_DEPLOYMENT_DIRECTORY):
    file_helper.remove_dir(const.DOCKER_DEPLOYMENT_DIRECTORY)


if __name__ == '__main__':
  parser = argparse.ArgumentParser()
  parser.add_argument('--push', dest='should_push_deployment', action='store_true')
  args = parser.parse_args()

  prod_environment_file_content = angular_builder.read_angular_prod_environment_file()

  if file_helper.does_dir_exist(const.DOCKER_DEPLOYMENT_DIRECTORY):
    helper.print_inner_step('ERROR: Please remove the deployment directory {}, to package the solution'.format(const.DOCKER_DEPLOYMENT_DIRECTORY), 0)
  else:
    try:
      helper.print_start_step('CREATING DIRECTORIES')
      helper.print_inner_step('Creating deployment directory', 1)
      create_deployment_directory()
      helper.print_inner_step('Creating service directory', 1)
      create_service_directory()
      helper.print_inner_step('Creating web-ui directory', 1)
      create_web_ui_directory()
      helper.print_inner_step('Creating swing-ui directory', 1)
      create_swing_ui_directory()
      helper.print_end_step('CREATING DIRECTORIES')

      helper.print_start_step('DEPLOY SERVICE')

      helper.print_start_step('COMPILE JAVA')
      java_builder.compile_java_and_run_tests()
      helper.print_end_step('COMPILE JAVA')

      helper.print_start_step('COPY SERVICE')
      helper.print_inner_step('Copying service jar', 1)
      java_builder.copy_service_jar(const.DOCKER_DEPLOYMENT_SERVICE_DIRECTORY)
      helper.print_end_step('COPY SERVICE')

      helper.print_start_step('COMPILE PYTHON')
      python_builder.compile_python_and_run_tests()
      helper.print_end_step('COMPILE PYTHON')

      helper.print_start_step('COPY PYTHON')
      helper.print_inner_step('Copying python directory', 1)
      python_builder.copy_engine_project(const.DOCKER_DEPLOYMENT_SERVICE_ENGINE_DIRECTORY)
      helper.print_end_step('COPY PYTHON')

      helper.print_start_step('COPY DOCKERFILE SERVICE')
      helper.print_inner_step('Copying Dockerfile', 1)
      file_helper.copy_file('{}Dockerfile'.format(const.DEPLOYMENT_SERVICE_DIRECTORY), const.DOCKER_DEPLOYMENT_SERVICE_DIRECTORY)
      helper.print_end_step('COPY DOCKERFILE SERVICE')

      helper.print_start_step('BUILD SERVICE IMAGE')
      deployment_helper.deploy_service(args.should_push_deployment)
      helper.print_end_step('BUILD SERVICE IMAGE')

      helper.print_end_step('DEPLOY SERVICE')


      helper.print_start_step('DEPLOY WEB UI')

      helper.print_start_step('OVERWRITE PROD ENV FILE ANGULAR')
      angular_builder.overwrite_angular_prod_environment_file(const.DEFAULT_SERVICE_PORT)
      helper.print_end_step('OVERWRITE PROD ENV FILE ANGULAR')

      helper.print_start_step('COMPILE ANGULAR')
      angular_builder.compile_angular_and_run_tests()
      helper.print_end_step('COMPILE ANGULAR')

      helper.print_start_step('COPY ANGULAR')
      helper.print_inner_step('Copying angular dist directory', 1)
      angular_builder.copy_web_ui_dist(const.DOCKER_DEPLOYMENT_WEB_UI_DIST_DIRECTORY)
      helper.print_end_step('COPY ANGULAR')

      helper.print_start_step('COPY DOCKERFILE WEBUI')
      helper.print_inner_step('Copying Dockerfile', 1)
      file_helper.copy_file('{}Dockerfile'.format(const.DEPLOYMENT_WEB_UI_DIRECTORY), const.DOCKER_DEPLOYMENT_WEB_UI_DIRECTORY)
      helper.print_end_step('COPY DOCKERFILE WEBUI')

      helper.print_start_step('BUILD WEB UI IMAGE')
      deployment_helper.deploy_web_ui(args.should_push_deployment)
      helper.print_end_step('BUILD WEB UI IMAGE')

      helper.print_end_step('DEPLOY WEB UI')


      helper.print_start_step('DEPLOY SWING UI')

      helper.print_start_step('COPY SWING UI')
      helper.print_inner_step('Copying swing ui jar', 1)
      java_builder.copy_swing_ui_jar(const.DOCKER_DEPLOYMENT_SWING_UI_DIRECTORY)
      helper.print_end_step('COPY SWING UI')

      helper.print_start_step('COPY DOCKERFILE SWING UI')
      helper.print_inner_step('Copying Dockerfile', 1)
      file_helper.copy_file('{}Dockerfile'.format(const.DEPLOYMENT_SWING_UI_DIRECTORY), const.DOCKER_DEPLOYMENT_SWING_UI_DIRECTORY)
      helper.print_end_step('COPY DOCKERFILE SWING UI')

      helper.print_start_step('BUILD SWING UI IMAGE')
      deployment_helper.deploy_swing_ui(args.should_push_deployment)
      helper.print_end_step('BUILD SWING UI IMAGE')

      helper.print_end_step('DEPLOY SWING UI')

    except Exception as exception:
      print('\n'*5)
      helper.print_start_step('ERROR')
      helper.print_inner_step('Error: {}'.format(str(exception)), 1)
      helper.print_inner_step('Deleting Deployment Directory', 1)
      delete_deployment_directory()
      helper.print_end_step('ERROR')

    finally:
      angular_builder.reset_angular_prod_environment_file(prod_environment_file_content)
