#!/usr/bin/python3

from multiprocessing.managers import SyncManager


class MultiProcessingManager(SyncManager):
  '''
  Custom MultiProcessing Manager used to create shared objects accross all subprocesses.
  '''
  pass

