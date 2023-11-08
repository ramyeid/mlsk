import abc
import pandas as pd


class IClassifierService(abc.ABC):


  @abc.abstractmethod
  def predict(self) -> pd.DataFrame:
    '''
    Predict the next {number_of_values} values to come using a Classifier algorithm
    This method will compute the implementation specific algorithm with feature and target values taken from {data}

    Returns
      pandas.DataFrame -> data frame containing one column with predicted values only
    '''
    pass


  @abc.abstractmethod
  def compute_predict_accuracy(self) -> float:
    '''
    Compute the accuracy of the predict service using the implementation specific algorithm.
    Predict the last {number_of_values} values and compare them to the actual values

    Returns
      float -> computed accuracy of the predict service
    '''
    pass
