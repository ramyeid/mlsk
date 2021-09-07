#!/usr/bin/python3

import os
from shutil import rmtree, copy, copytree


def get_absolute_path(directory: str) -> str:
  return os.path.abspath(directory)


def does_dir_exist(directory: str) -> bool:
  return os.path.isdir(directory)


def copy_dir(src: str, dst: str):
  copytree(src, dst)


def create_dir(directory: str):
  os.mkdir(directory)


def remove_dir(directory: str):
  rmtree(directory)


def does_file_exist(file: str) -> bool:
  return os.path.isfile(file)


def copy_file(src: str, dst: str):
  copy(src, dst)


def read_file(file_path: str) -> str:
  with open(file_path, 'r') as file:
    return file.read()


def write_file(file_path: str, content: str):
  with open(file_path, 'w+') as file:
    file.write(content)


def replace_placeholder_in_file(file_path: str, place_holder: str, value: str):
  file_content = read_file(file_path)

  updated_file_content = file_content.replace(place_holder, value)

  write_file(file_path, updated_file_content)
