import org.mlsk.ci.builder.AngularBuilder;


def call() {
  AngularBuilder angularBuilder = new AngularBuilder(this);
  angularBuilder.checkQualityGate();
}
