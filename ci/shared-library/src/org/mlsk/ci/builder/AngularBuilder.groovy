package org.mlsk.ci.builder;

class AngularBuilder implements IBuilder {

  private final def steps;

  public AngularBuilder(def steps) {
    this.steps = steps;
  }

  @Override
  public void build() {
    steps.sh "ng build --preserve-symlinks";
  }

  @Override
  public void test() {
    steps.sh 'ng test --watch=false --preserve-symlinks --browsers=ChromeHeadlessNoSandbox --reporters=junit'
  }

  @Override
  public void publishTestReports() {
    steps.junit 'angular-junit-report.xml'
  }

  public void linkNodeModules() {
    steps.sh 'ln -s /root/.mlsk_node_modules/node_modules/ ./web-ui/'
  }
}

