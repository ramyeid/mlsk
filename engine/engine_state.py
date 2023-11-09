#!/usr/bin/python3

from classifier.registry.classifier_data_registry import ClassifierDataBuilderRegistry, ClassifierDataBuilder


class Engine:


  def __init__(self):
    self.inflight_requests = set()
    self.classifier_data_builder_registry = ClassifierDataBuilderRegistry()


  def contains_request(self, request_id: int) -> bool:
    return request_id in self.inflight_requests


  def release_request(self, request_id: int) -> None:
    self.classifier_data_builder_registry.release_request(request_id)
    if request_id in self.inflight_requests:
      self.inflight_requests.remove(request_id)


  def register_new_time_series_request(self, request_id: int) -> None:
    self.inflight_requests.add(request_id)


  def register_classifier_request(self, request_id: int) -> ClassifierDataBuilder:
    self.inflight_requests.add(request_id)
    return self.classifier_data_builder_registry.new_builder(request_id)


  def get_classifier_data_builder(self, request_id: int) -> ClassifierDataBuilderRegistry:
    return self.classifier_data_builder_registry.get_builder(request_id)


  def contains_classifier_data_builder(self, request_id: int) -> bool:
    return self.classifier_data_builder_registry.contains_builder(request_id)


  def release_all_inflight_requests(self) -> None:
    self.classifier_data_builder_registry.reset()
    self.inflight_requests = set()


ENGINE = Engine()

def get_engine():
  return ENGINE
