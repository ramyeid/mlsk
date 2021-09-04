package org.mlsk.ci.builder;

interface IBuilder {

  void build();

  void test();

  void publishTestReports();

  void checkQualityGate();
}

