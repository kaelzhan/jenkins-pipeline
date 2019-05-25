@Library('test-cj') pipelineLibrary
def container_Template = libraryResource 'com/k8s/containerTemplate.yaml'

pipeline {
    agent any

    parameters {
        string(name: 'job_id', defaultValue: 'xxxxxxx', description: 'job id')
        text(name: 'parameterA', defaultValue: '', description: 'parameterA')
    }

    stages {
        stage('Upload file') {
            steps {
                script {
                    echo "${params.parameterA}"
                    sh 'cd /test_step2'
                }
            }
        }
    }
}