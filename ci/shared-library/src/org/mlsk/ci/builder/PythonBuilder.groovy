package org.mlsk.ci.builder;

class PythonBuilder implements IBuilder {

  private final def steps;

  public PythonBuilder(def steps) {
    this.steps = steps;
  }

  @Override
  public void build() {
    steps.sh "python3 -m compileall -f .";
  }

  @Override
  public void test() {
    steps.sh 'python3 -m pytest -s --junitxml=python-test-reports.xml'
  }

  @Override
  public void publishTestReports() {
    steps.junit '**/python-test-reports.xml'
  }
}

