package org.mlsk.service.classifier.resource.decisiontree;

public final class DecisionTreeConstants {

  public static final String START_URL = "/decision-tree/start";
  public static final String DATA_URL = "/decision-tree/data";
  public static final String PREDICT_URL = "/decision-tree/predict";
  public static final String PREDICT_ACCURACY_URL = "/decision-tree/predict-accuracy";
  public static final String CANCEL_URL = "/decision-tree/cancel";

  public static final String DECISION_TREE_START = "decision-tree-start";
  public static final String DECISION_TREE_DATA = "decision-tree-data";
  public static final String DECISION_TREE_PREDICT = "decision-tree-predict";
  public static final String DECISION_TREE_PREDICT_ACCURACY = "decision-tree-compute-predict-accuracy";
  public static final String DECISION_TREE_CANCEL = "decision-tree-cancel";

  private DecisionTreeConstants() {
  }
}
