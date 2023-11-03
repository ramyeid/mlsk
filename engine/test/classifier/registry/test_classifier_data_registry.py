#!/usr/bin/python3

import unittest
import pandas as pd
from pandas.testing import assert_frame_equal
from classifier.registry.classifier_data_registry import ClassifierDataBuilderRegistry, ClassifierDataBuilder, ClassifierData
from classifier.registry.classifier_data_registry_exception import ClassifierDataRegistryException


class TestClassifierDataBuilderRegistry(unittest.TestCase):


  def test_new_builder_and_get_builder(self) -> None:
    # Given
    data_builder_registry = ClassifierDataBuilderRegistry()
    data_builder_registry.new_builder(123)

    # When
    data_builder = data_builder_registry.get_builder(123)
    contains_start_data = data_builder.contains_start_data()
    data_builder_count = data_builder_registry.len()
    contains_123 = data_builder_registry.contains_builder(123)

    # Then
    self.assertFalse(contains_start_data)
    self.assertEqual(1, data_builder_count)
    self.assertTrue(contains_123)


  def test_new_builder_throws_if_request_id_exists(self) -> None:
    # Given
    data_builder_registry = ClassifierDataBuilderRegistry()
    data_builder_registry.new_builder(123)

    # When
    with self.assertRaises(ClassifierDataRegistryException) as context:
      data_builder_registry.new_builder(123)

    # Then
    self.assertEqual('RequestId (123) already inflight!', str(context.exception))


  def test_get_builder_throws_if_request_id_does_not_exist(self) -> None:
    # Given
    data_builder_registry = ClassifierDataBuilderRegistry()

    # When
    with self.assertRaises(ClassifierDataRegistryException) as context:
      data_builder_registry.get_builder(123)

    # Then
    self.assertEqual('RequestId (123) not inflight!', str(context.exception))


  def test_drop_request_removes_builder(self) -> None:
    # Given
    data_builder_registry = ClassifierDataBuilderRegistry()
    data_builder_registry.new_builder(123)
    count_pre_drop = data_builder_registry.len()
    contains_123_pre_drop = data_builder_registry.contains_builder(123)

    # When
    contains_data = data_builder_registry.drop_request(123)

    # Then
    count_post_drop = data_builder_registry.len()
    contains_123_post_drop = data_builder_registry.contains_builder(123)
    self.assertEqual(1, count_pre_drop)
    self.assertTrue(contains_123_pre_drop)
    self.assertEqual(0, count_post_drop)
    self.assertFalse(contains_123_post_drop)


  def test_reset_removes_all_content(self) -> None:
    # Given
    data_builder_registry = ClassifierDataBuilderRegistry()
    data_builder_registry.new_builder(123)
    data_builder_registry.new_builder(124)
    contains_123_pre_reset = data_builder_registry.contains_builder(123)
    contains_124_pre_reset = data_builder_registry.contains_builder(124)
    count_pre_reset = data_builder_registry.len()

    # When
    data_builder_registry.reset()

    # Then
    count_post_reset = data_builder_registry.len()
    contains_123_post_reset = data_builder_registry.contains_builder(123)
    contains_124_post_reset = data_builder_registry.contains_builder(124)
    self.assertEqual(2, count_pre_reset)
    self.assertTrue(contains_123_pre_reset)
    self.assertTrue(contains_124_pre_reset)
    self.assertEqual(0, count_post_reset)
    self.assertFalse(contains_123_post_reset)
    self.assertFalse(contains_124_post_reset)



class TestClassifierDataBuilder(unittest.TestCase):


  def test_set_start_data(self) -> None:
    # Given
    data_builder = ClassifierDataBuilder()
    data_builder.set_start_data('pred', ['col0', 'col1'], 2)

    # When
    contains_start_data = data_builder.contains_start_data()

    # Then
    self.assertTrue(contains_start_data)


  def test_contains_start_data(self) -> None:
    # Given
    data_builder = ClassifierDataBuilder()

    # When
    contains_start_data = data_builder.contains_start_data()

    # Then
    self.assertFalse(contains_start_data)


  def test_add_data(self) -> None:
    # Given
    data_builder = ClassifierDataBuilder()
    data_builder.add_data('col0', [0, 1, 0, 1, 1])
    data_builder.add_data('col1', [1, 1, 1, 1, 1])

    # When
    contains_data = data_builder.contains_data()

    # Then
    self.assertTrue(contains_data)


  def test_contains_data(self) -> None:
    # Given
    data_builder = ClassifierDataBuilder()

    # When
    contains_data = data_builder.contains_data()

    # Then
    self.assertFalse(contains_data)


  def test_reset(self) -> None:
    # Given
    data_builder = ClassifierDataBuilder()
    data_builder.set_start_data('pred', ['col0', 'col1'], 2)
    data_builder.add_data('col0', [0, 1, 0, 1, 1])
    data_builder.add_data('col1', [1, 1, 1, 1, 1])

    # When
    data_builder.reset()
    contains_start_data = data_builder.contains_start_data()
    contains_data = data_builder.contains_data()

    # Then
    self.assertFalse(contains_start_data)
    self.assertFalse(contains_data)


  def test_build_classifier_data(self) -> None:
    # Given
    data_builder = ClassifierDataBuilder()
    data_builder.set_start_data('pred', ['col0', 'col1'], 2)
    data_builder.add_data('col0', [0, 1, 0, 1, 1])
    data_builder.add_data('col1', [1, 1, 1, 1, 1])

    # When
    actual_classifier_data = data_builder.build_classifier_data()

    # Then
    expected_dict = {'col0': [0, 1, 0, 1, 1], 'col1':[1, 1, 1, 1, 1]}
    expected_classifier_data = ClassifierData('pred', ['col0', 'col1'], 2, expected_dict)
    self.assertEqual(expected_classifier_data, actual_classifier_data)


  def test_classifier_data_getters(self) -> None:
    # Given
    data_builder = ClassifierDataBuilder()
    data_builder.set_start_data('pred', ['col0', 'col1'], 2)
    data_builder.add_data('col0', [0, 1, 0, 1, 1])
    data_builder.add_data('col1', [1, 1, 1, 1, 1])

    # When
    actual_classifier_data = data_builder.build_classifier_data()
    actual_prediction_column_name = actual_classifier_data.get_prediction_column_name()
    actual_action_column_names = actual_classifier_data.get_action_column_names()
    actual_number_of_values = actual_classifier_data.get_number_of_values()
    actual_data = actual_classifier_data.get_data()

    # Then
    expected_prediction_column_name = 'pred'
    expected_action_column_names = ['col0', 'col1']
    expected_number_of_values = 2
    expected_data = {'col0': [0, 1, 0, 1, 1], 'col1':[1, 1, 1, 1, 1]}
    self.assertEqual(expected_prediction_column_name, actual_prediction_column_name)
    self.assertEqual(expected_action_column_names, actual_action_column_names)
    self.assertEqual(expected_number_of_values, actual_number_of_values)
    self.assertEqual(expected_data, actual_data)


  def test_to_data_frame(self) -> None:
    # Given
    data_builder = ClassifierDataBuilder()
    data_builder.add_data('col0', [0, 1, 0, 1, 1])
    data_builder.add_data('col1', [1, 1, 1, 1])
    data_builder.add_data('col2', [0])

    # When
    actual_classifier_data = data_builder.build_classifier_data()
    actual_data_frame = actual_classifier_data.to_data_frame()

    # Then
    expected_data = { 'col0': pd.Series([0, 1, 0, 1, 1], index=['0', '1', '2', '3', '4']),
                      'col1': pd.Series([1, 1, 1, 1], index=['0', '1', '2', '3']),
                     'col2': pd.Series([0], index=['0'])}
    expected_data_frame = pd.DataFrame(expected_data)
    assert_frame_equal(expected_data_frame, actual_data_frame)


  def test_to_string(self) -> None:
    # Given
    data_builder = ClassifierDataBuilder()
    data_builder.set_start_data('pred', ['col0', 'col1'], 2)
    data_builder.add_data('col0', [0, 1, 0, 1, 1])
    data_builder.add_data('col1', [1, 1, 1, 1, 1])

    # When
    actual_classifier_data = data_builder.build_classifier_data()
    actual_str = actual_classifier_data.__str__()

    # Then
    self.assertEqual("{'predictionColumnName': 'pred', 'actionColumnNames': ['col0', 'col1'], "\
      "'numberOfValues': 2, 'data': {'col0': [0, 1, 0, 1, 1], 'col1': [1, 1, 1, 1, 1]}}", actual_str)


  def test_repr(self) -> None:
    # Given
    data_builder = ClassifierDataBuilder()
    data_builder.set_start_data('pred', ['col0', 'col1'], 2)
    data_builder.add_data('col0', [0, 1, 0, 1, 1])
    data_builder.add_data('col1', [1, 1, 1, 1, 1])

    # When
    actual_classifier_data = data_builder.build_classifier_data()
    actual_str = actual_classifier_data.__repr__()

    # Then
    self.assertEqual("{'predictionColumnName': 'pred', 'actionColumnNames': ['col0', 'col1'], "\
      "'numberOfValues': 2, 'data': {'col0': [0, 1, 0, 1, 1], 'col1': [1, 1, 1, 1, 1]}}", actual_str)


  def test_to_json(self) -> None:
    # Given
    data_builder = ClassifierDataBuilder()
    data_builder.set_start_data('pred', ['col0', 'col1'], 2)
    data_builder.add_data('col0', [0, 1, 0, 1, 1])
    data_builder.add_data('col1', [1, 1, 1, 1, 1])

    # When
    actual_classifier_data = data_builder.build_classifier_data()
    actual_json = actual_classifier_data.to_json()

    # Then
    expected_json = dict(predictionColumnName='pred',
                         actionColumnNames=['col0', 'col1'],
                         numberOfValues=2,
                         data=dict(col0=[0, 1, 0, 1, 1], col1=[1, 1, 1, 1, 1]))
    self.assertDictEqual(expected_json, actual_json)


if __name__ == '__main__':
  unittest.main()
