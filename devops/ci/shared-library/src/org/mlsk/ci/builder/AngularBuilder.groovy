package org.mlsk.ci.builder;

class AngularBuilder implements IBuilder {

  private final def steps;

  public AngularBuilder(def steps) {
    this.steps = steps;
  }

  @Override
  public void build() {
    steps.sh 'ng build --preserve-symlinks';
  }

  @Override
  public void test() {
    steps.sh 'ng test --watch=false --preserve-symlinks --browsers=ChromeHeadlessNoSandbox --reporters=junit --code-coverage'
  }

  @Override
  public void publishTestReports() {
    steps.junit 'angular-junit-report.xml'
  }

  @Override
  public void checkQualityGate() {
    steps.withSonarQubeEnv('SonarqubeServer') {
      steps.sh 'sonar-scanner \
                -Dsonar.language=ts \
                -Dsonar.projectKey=web-ui \
                -Dsonar.projectName=web-ui \
                -Dsonar.sources=src \
                -Dsonar.exclusions=node_modules/*,**/*.spec.ts \
                -Dsonar.typescript.lcov.reportPaths=coverage/lcov.info'
                // did not link eslint file to discover best practices in sonar for angular.
                //sonar.typescript.eslint.reportPaths
    }
    steps.timeout(time: 1, unit: 'HOURS') {
      steps.waitForQualityGate abortPipeline: true
    }
  }

  public void linkNodeModules() {
    steps.sh 'ln -s /root/.mlsk_node_modules/node_modules/ ./web-ui/'
  }
}

