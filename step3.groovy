pipeline {
    kubernetes {
        label 'test-label'
    }
    parameters {
        string(name: 'job_id', defaultValue: 'xxxxxxx', description: 'job id')
    }

    stages {
        stage('Stream job') {
            steps {
                echo 'pppppp'
            }
        }
        stage('Stream job eoore') {
            steps {
                container('lcjenkins-java8') {
                    sh 'java -version'
                    sh('cd /tmp/jenkins_jobs/test')
                }
            }
        }
    }
}
