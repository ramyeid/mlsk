package org.mlsk.ci.builder;

class JavaBuilder implements IBuilder {

  private final def steps;

  public JavaBuilder(def steps) {
    this.steps = steps;
  }

  @Override
  public void build() {
    steps.sh 'mvn clean install -DskipTests';
  }

  @Override
  public void test() {
    steps.sh 'mvn test'
  }

  @Override
  public void publishTestReports() {
    steps.junit '**/target/*reports/**/*.xml'
  }

  @Override
  public void checkQualityGate() {
    steps.withSonarQubeEnv('SonarqubeServer') {
      steps.sh 'mvn sonar:sonar \
                -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml \
                -Dsonar.language=java \
                -Dsonar.dynamicAnalysis=reuseReports'
    }
    steps.timeout(time: 1, unit: 'HOURS') {
      steps.waitForQualityGate abortPipeline: true
    }
  }
}

