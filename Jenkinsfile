@Library('mlsk-shared-library') _


pipeline {

  options {
    buildDiscarder(logRotator(numToKeepStr: '3'))
    disableConcurrentBuilds()
    skipStagesAfterUnstable()
  }

  parameters {
    booleanParam(name: "GENERATE_NODE_MODULES", description: 'Regenerate and overwrite node_modules (in case of updated and new dependencies)', defaultValue: false)
  }

  agent {
    label 'docker'
  }

  stages {

    stage('SetUp') {
      steps {
        angularLinkOrGenerateNodeModules()
      }
    }

    stage('Build Java') {
      steps {
        javaBuild()
      }
    }

    stage('Build Python') {
      steps {
        dir('engine') {
          pythonBuild()
        }
      }
    }

    stage('Build Angular') {
      steps {
        dir('web-ui') {
          angularBuild()
        }
      }
    }

    stage('Test Java') {
      steps {
        warnError(message: 'Java tests failed') {
          javaTest()
        }
      }
      post {
        always {
          javaPublishTestReports()
        }
      }
    }

    stage('Test Python') {
      steps {
        dir('engine') {
          warnError(message: 'Python tests failed') {
            pythonTest()
          }
        }
      }
      post {
        always {
          dir('engine') {
            pythonPublishTestReports()
          }
        }
      }
    }

    stage('Test Angular') {
      steps {
        dir('web-ui') {
          warnError(message: 'Angular tests failed') {
            angularTest()
          }
        }
      }
      post {
        always {
          dir('web-ui') {
            angularPublishTestReports()
          }
        }
      }
    }

    stage('Quality Gate - Service') {
      steps {
        dir('service/impl') {
          warnError(message: 'Service Quality Gate Breached') {
            javaCheckQualityGate()
          }
        }
      }
    }

    stage('Quality Gate - Swing UI') {
      steps {
        dir('swing-ui') {
          warnError(message: 'Swing UI Quality Gate Breached') {
            javaCheckQualityGate()
          }
        }
      }
    }

    stage('Quality Gate - Engine') {
      steps {
        dir('engine') {
          warnError(message: 'Engine Quality Gate Breached') {
            pythonCheckQualityGate()
          }
        }
      }
    }

    stage('Quality Gate - Web UI') {
      steps {
        dir('web-ui') {
          warnError(message: 'Web UI Quality Gate Breached') {
            angularCheckQualityGate()
          }
        }
      }
    }
  }
}
