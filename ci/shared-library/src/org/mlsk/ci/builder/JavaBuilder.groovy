package org.mlsk.ci.builder;

class JavaBuilder implements IBuilder {

  private final def steps;

  public JavaBuilder(def steps) {
    this.steps = steps;
  }

  @Override
  public void build() {
    steps.sh "mvn clean package -DskipTests";
  }

  @Override
  public void test() {
    steps.sh 'mvn test'
  }

  @Override
  public void publishTestReports() {
    steps.junit '**/target/*reports/**/*.xml'
  }
}

