@Library('mlsk-shared-library') _


pipeline {

  options {
    buildDiscarder(logRotator(numToKeepStr: '3'))
    disableConcurrentBuilds()
    skipStagesAfterUnstable()
  }

  agent {
    label 'docker'
  }

  stages {

    stage('SetUp') {
      steps {
        angularLinkNodeModules()
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
            sleep(time:1, unit:"SECONDS") // Needed bcs publishReport was failing.
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

    stage('Quality Gate - Java UI') {
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
