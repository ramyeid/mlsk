#!/usr/bin/python3

from lib import helper
from deployment import constants as const


def deploy_service(should_push_deployment: bool):
  deploy(should_push_deployment, const.DOCKER_DEPLOYMENT_SERVICE_DIRECTORY, const.SERVICE_IMAGE)


def deploy_web_ui(should_push_deployment: bool):
  deploy(should_push_deployment, const.DOCKER_DEPLOYMENT_WEB_UI_DIRECTORY, const.WEB_UI_IMAGE)


def deploy(should_push_deployment: bool, path:str, image_name: str):
  try_remove_old_image(image_name)
  helper.launch_command('cd {} && docker build -t {} .'.format(path, image_name), 'Unable to build image')
  if should_push_deployment:
    helper.launch_command('docker push {}'.format(image_name), 'Unable to push image')


def try_remove_old_image(image_name: str):
  try:
    helper.launch_command('docker image rm {}'.format(image_name), 'Unable to remove image')
  except Exception:
    helper.print_inner_step('Could not remove old image', 2)
