#!/usr/bin/python3

import os
import argparse


def launch_command(command: str, exception_message: str):
  if os.system(command) != 0:
    raise Exception(exception_message)


def print_start_step(step_name: str):
  print('-' * 10 + 'START[' + step_name + ']' + '-' * 10)


def print_end_step(step_name: str):
  print('-' * 11 + 'END[' + step_name + ']' + '-' * 11)
  print('\n')


def print_inner_step(step_name: str, step_number: int):
  print('  ' * (step_number * 2)  + step_name)
