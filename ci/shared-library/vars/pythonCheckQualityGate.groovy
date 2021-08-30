import org.mlsk.ci.builder.PythonBuilder;


def call() {
  PythonBuilder pythonBuilder = new PythonBuilder(this);
  pythonBuilder.checkQualityGate();
}
