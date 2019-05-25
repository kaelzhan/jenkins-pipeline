@Library('kael_test_jenkins') pipelineLibrary

def container_Template = libraryResource 'com/kubernetes/kael_k8s_bio_computer_template.yaml'

pipeline {
    agent {
        kubernetes {
            label 'bio_computer_' + UUID.randomUUID()
            defaultContainer 'kael_k8s_bio_computer'
            yaml "${container_Template}"
        }
    }
    parameters {
        string(name: 'client_id', defaultValue: '500000001', description: 'clinet_id')
        string(name: 'env', defaultValue: 'playground', description: 'environment')
        string(name: 'JENKINS_EXECUTION_ID', defaultValue: 'xxxxxxx', description: 'job id')
    }

    stages {
        stage('kael test step 1') {
            steps {
                sh 'env'
                script{
                    learn_s3_helper.parameter_save()
                }
            }
        }
        stage('kael test step 2') {
            steps {
                sh 'env'
                script{
                    learn_s3_helper.env_read()
                }
            }
        }
    }
}

