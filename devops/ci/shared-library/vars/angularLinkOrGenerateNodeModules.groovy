import org.mlsk.ci.builder.AngularBuilder;


def call() {
  AngularBuilder angularBuilder = new AngularBuilder(this);

  if (params.GENERATE_NODE_MODULES) {
    echo 'Generating node_modules'
    angularBuilder.regenerateNodeModules();
  }

  echo 'Linking to cached node_modules'
  angularBuilder.linkNodeModules();
}
