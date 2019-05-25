pipeline {
    agent any

    options { disableConcurrentBuilds() }

    stages{
        stage('Generate jobs'){
            steps {
                jobDsl(
                    targets: 'dsl_seed/*',
                    unstableOnDeprecation: true,
                    sandbox: true
                )
            }
        }

    }
}