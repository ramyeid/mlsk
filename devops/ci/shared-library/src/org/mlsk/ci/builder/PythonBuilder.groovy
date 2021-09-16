package org.mlsk.ci.builder;

class PythonBuilder implements IBuilder {

  private final def steps;

  public PythonBuilder(def steps) {
    this.steps = steps;
  }

  @Override
  public void build() {
    steps.sh 'python3 -m compileall -f .';
  }

  @Override
  public void test() {
    steps.sh 'python3 -m pytest -s --junitxml=python-test-reports.xml --cov=. --cov-report xml:coverage.xml'
  }

  @Override
  public void publishTestReports() {
    steps.junit 'python-test-reports.xml'
  }

  @Override
  public void checkQualityGate() {
    steps.withSonarQubeEnv('SonarqubeServer') {
      steps.sh 'sonar-scanner \
                -Dsonar.language=python \
                -Dsonar.projectKey=engine \
                -Dsonar.projectName=engine \
                -Dsonar.python.xunit.reportPath=python-test-reports.xml \
                -Dsonar.python.coverage.reportPaths=coverage.xml \
                -Dsonar.exclusions=debug/*'
    }
    steps.timeout(time: 1, unit: 'HOURS') {
      steps.waitForQualityGate abortPipeline: true
    }
  }
}

