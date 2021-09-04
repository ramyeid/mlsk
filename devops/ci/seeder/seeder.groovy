multibranchPipelineJob('MLSK') {

  factory {
    workflowBranchProjectFactory {
      scriptPath('Jenkinsfile')
    }
  }

  orphanedItemStrategy {

    discardOldItems {
      numToKeep(3)
    }

    defaultOrphanedItemStrategy {
      pruneDeadBranches(true)
      numToKeepStr('3')
      daysToKeepStr('2')
    }

  }

  branchSources {
    git {
      id('123456789')
      remote('https://github.com/ramyeid/mlsk.git')
      credentialsId('github-token')
    }
  }

}
