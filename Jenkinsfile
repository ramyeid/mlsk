@Library('mlsk-shared-library@ci/jenkinsfile') _


pipeline {

  options {
    buildDiscarder(logRotator(numToKeepStr: "3"))
    disableConcurrentBuilds()
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
        catchError(message: 'Java tests failed', buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
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
          catchError(message: 'Python tests failed', buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
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
          catchError(message: 'Angular tests failed', buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
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

  }
}
