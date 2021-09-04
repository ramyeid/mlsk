import org.mlsk.ci.builder.JavaBuilder;


def call() {
  JavaBuilder javaBuilder = new JavaBuilder(this);
  javaBuilder.checkQualityGate();
}
